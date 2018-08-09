package com.yj.crawler.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import sun.reflect.generics.tree.Tree;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.TimeoutException;

/**
 * @author wuyang
 * @date 2018/8/1 16:09
 */
public class RabbitMQConnectionPool {

    /**
     * 存放RabbitMQ连接
     */
    private TreeMap<Integer,ConnectionConfig> connectionTreeMap = new TreeMap<Integer,ConnectionConfig>();
    /**
     * 记录通道对应的连接
     */
    private Map<Channel, ConnectionConfig> channelHashMap = new HashMap<Channel, ConnectionConfig>();
    /**
     * RabbitMQ配置
     */
    private RabbitMQConfig rabbitMQConfig;
    /**
     * 锁，防止资源释放与获取资源之间的冲突
     */
    private boolean sourceLock = false;
    /**
     *  共享锁--->>生命周期监听，获取通道
     */
    private Object lock = new Object();
    /**
     * 如果要关闭该通道，则要标注该状态
     */
    private boolean channelAliveKill = false;
    /**
     * 通道最大总数
     */
    private int channelTotalMax;
    /**
     * 通道最小总数
     */
    private int channelTotalMin;
    /**
     * 初始化RabbitMQ连接池
     *
     * @throws IOException
     * @throws TimeoutException
     */
    public void init(RabbitMQConfig rabbitMQConfig) throws IOException, TimeoutException {
        // 赋予RabbitMQ配置
        this.rabbitMQConfig = rabbitMQConfig;
        this.channelTotalMin = rabbitMQConfig.getChannelMin()*rabbitMQConfig.getConnectionMin();
        this.channelTotalMax = rabbitMQConfig.getChannelMax()*rabbitMQConfig.getConnectionMax();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitMQConfig.getHost());
        factory.setPort(rabbitMQConfig.getPort());
        factory.setUsername(rabbitMQConfig.getUsername());
        factory.setPassword(rabbitMQConfig.getPassword());

        int connectionMin = rabbitMQConfig.getConnectionMin();
        int channelMin = rabbitMQConfig.getChannelMin();
        ConnectionConfig connectionConfig = null;

        for (int i = 0; i < connectionMin; i++) {
            connectionConfig = new ConnectionConfig();

            // 创建rabbitMQ连接，并存入集合
            Connection connection = factory.newConnection();
            ChannelConfig channelConfig = null;
            connectionConfig.setConnection(connection);
            connectionConfig.setConnectionKeepAliveTime(getEndOfLifeTime(rabbitMQConfig.getConnectionKeepAliveTime()));
            for (int k = 0; k < channelMin; k++) {
                // 创建该连接的通道，并存入集合
                Channel channel = connection.createChannel();
                channelConfig = new ChannelConfig(channel,getEndOfLifeTime(rabbitMQConfig.getChannelKeepAliveTime()));
                connectionConfig.getChannelList().add(channelConfig);
                channelHashMap.put(channel,connectionConfig);
            }
            connectionTreeMap.put(connectionConfig.getChannelList().size(),connectionConfig);
        }
        Thread timeOutFilter = new TimeOutFilter();
        timeOutFilter.start();
    }

    /**
     * 获取RabbitMQ通道
     * @return
     * @throws IOException
     */
    public Channel getChannel() throws IOException {
        sourceLock = true;
        synchronized (lock) {
            try{
                // 空闲通道生命有效时间
                final int channelKeepAliveTime = this.rabbitMQConfig.getChannelKeepAliveTime();
                final int connectionKeepAliveTime = this.rabbitMQConfig.getConnectionKeepAliveTime();

                // 拿通道数最少的连接，避免连接过忙造成的性能问题
                ConnectionConfig connectionConfig = connectionTreeMap.firstEntry().getValue();
                // 拿该相对空闲连接下面的通道
                List<ChannelConfig> channelList = connectionConfig.getChannelList();
                int channelNum = channelList.size();
                // 如果还存在空闲通道的话就去拿该连接下面的通道
                if (channelNum > 0) {
                    // 当前用户取得的通道 Channel
                    final Channel channel = channelList.remove(0).getChannel();

                    // 更新连接的记录
                    connectionTreeMap.remove(connectionTreeMap.firstEntry().getKey());
                    connectionTreeMap.put(channelList.size(), connectionConfig);

                    return channelProxy(channel, channelKeepAliveTime, connectionKeepAliveTime);
                    // 通道总数未达到上限
                } else if (channelHashMap.size() < channelTotalMax) {
                    // 拿到TreeMap中第一个也是相对最空闲的连接
                    Map.Entry<Integer, ConnectionConfig> connectionTreeMapEntry = connectionTreeMap.firstEntry();
                    connectionConfig = connectionTreeMapEntry.getValue();
                    // 为该链接创建通道
                    Channel channel = connectionConfig.getConnection().createChannel();

                    channelHashMap.put(channel,connectionConfig);
                    return channelProxy(channel, channelKeepAliveTime, connectionKeepAliveTime);
                } else {
                    System.out.println("连接池满了！！");
                    this.wait();
                    return getChannel();
                }
            }catch (Exception e){
                e.printStackTrace();
                try {
                    lock.wait();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                return getChannel();
            }finally {
                sourceLock = false;
                lock.notify();
            }
        }
    }

    /**
     * 获取生命终结时间
     * @param keepAliveTime
     */
    private Long getEndOfLifeTime(int keepAliveTime){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, keepAliveTime);
        return cal.getTimeInMillis();
    }
    private boolean aliveState(long keepAliveTime){
        Long nowTime = System.currentTimeMillis();
        if(nowTime >= keepAliveTime){
            return false;
        }else{
            return true;
        }
    }
    /**
     * 管控连接与通道数量
     */
    class TimeOutFilter extends Thread{

        @Override
        public void run() {
            int i = 0;
            try{
                synchronized (lock) {
                    while (true) {
                        if (sourceLock || channelHashMap.size()<= channelTotalMin) {
                            System.out.println("暂时停止监听");
                            lock.wait();
                        }
                        Thread.sleep(500);
                        System.out.println("扫描--" + (i++));

                        Iterator iterator = connectionTreeMap.entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry<Integer, ConnectionConfig> connectionConfigTreeMap = (Map.Entry<Integer, ConnectionConfig>) iterator.next();
                            ConnectionConfig connectionConfig = connectionConfigTreeMap.getValue();
                            List<ChannelConfig> channelConfigList = connectionConfig.getChannelList();
                            int channelConfigListSize = channelConfigList.size();
                            if (!aliveState(connectionConfig.getConnectionKeepAliveTime()) && channelConfigListSize == 0) {
                                System.out.println("连接真正关闭");
                                connectionConfig.getConnection().close();
                                iterator.remove();
                            } else {
                                Iterator iteratorList = channelConfigList.iterator();
                                while (iteratorList.hasNext()) {
                                    ChannelConfig channelConfig = (ChannelConfig) iteratorList.next();
                                    if (!aliveState(channelConfig.getConnectionKeepAliveTime())) {
                                        System.out.println("通道真正关闭");
                                        channelHashMap.remove(channelConfig.getChannel());
                                        channelConfig.getChannel().close();
                                        iteratorList.remove();
                                    }
                                }
                            }
                        }
                    }
                }
            }catch (TimeoutException e){
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 动态生成RabbitMQ通道Channel对象的代理对象，以便监听用户的关闭动作
     * @param channel
     * @param channelKeepAliveTime
     * @param connectionKeepAliveTime
     * @return
     */
    public Channel channelProxy(Channel channel,int channelKeepAliveTime, int connectionKeepAliveTime){
        return (Channel) Proxy.newProxyInstance(RabbitMQConnectionPool.class.getClassLoader(), channel.getClass().getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getName().equals("close") && !channelAliveKill) {

                    ChannelConfig channelConfig = new ChannelConfig(channel, getEndOfLifeTime(channelKeepAliveTime));
                    ConnectionConfig connectionConfigProxy = channelHashMap.get(channel);
                    connectionConfigProxy.getChannelList().add(channelConfig);
                    connectionConfigProxy.setConnectionKeepAliveTime(getEndOfLifeTime(connectionKeepAliveTime));

                    System.out.println("通道关闭" + connectionConfigProxy.getChannelList().size());
                    return null;
                } else {
                    return method.invoke(channel, args);
                }
            }
        });
    }
}
