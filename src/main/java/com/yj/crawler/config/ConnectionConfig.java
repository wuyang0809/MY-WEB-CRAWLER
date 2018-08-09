package com.yj.crawler.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wuyang
 * @date 2018/8/6 11:01
 */
public class ConnectionConfig {

    private Connection connection;

    /**
     * 空闲连接的有效时间 （单位-ms）
     */
    private long connectionKeepAliveTime;

    /**
     * RabbitMQ 连接通道
     */
    private List<ChannelConfig> channelList = new ArrayList<ChannelConfig>();

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public long getConnectionKeepAliveTime() {
        return connectionKeepAliveTime;
    }

    public void setConnectionKeepAliveTime(long connectionKeepAliveTime) {
        this.connectionKeepAliveTime = connectionKeepAliveTime;
    }

    public List<ChannelConfig> getChannelList() {
        return channelList;
    }

    public void setChannelList(List<ChannelConfig> channelList) {
        this.channelList = channelList;
    }
}
