package com.yj.crawler.config;


import com.rabbitmq.client.Channel;

/**
 * @author wuyang
 * @date 2018/8/6 17:13
 */
public class ChannelConfig {

    private Channel channel;

    /**
     * 空闲连接的有效时间 （单位-ms）
     */
    private long connectionKeepAliveTime;

    public ChannelConfig(Channel channel, long connectionKeepAliveTime) {
        this.channel = channel;
        this.connectionKeepAliveTime = connectionKeepAliveTime;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public long getConnectionKeepAliveTime() {
        return connectionKeepAliveTime;
    }

    public void setConnectionKeepAliveTime(long connectionKeepAliveTime) {
        this.connectionKeepAliveTime = connectionKeepAliveTime;
    }
}
