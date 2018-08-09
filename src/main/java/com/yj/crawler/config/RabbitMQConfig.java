package com.yj.crawler.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author wuyang
 * @date 2018/7/31 19:12
 */
public class RabbitMQConfig {

    /**
     * IP地址
     */
    private String host;
    /**
     * 端口号
     */
    private int port;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 最大连接数
     */
    private int connectionMax;
    /**
     * 最小连接数
     */
    private int connectionMin;
    /**
     * 每个连接中最大通道数
     */
    private int channelMax;
    /**
     * 每个连接中最小通道数
     */
    private int channelMin;
    /**
     * 空闲通道的有效时间 （单位-ms）
     */
    private int channelKeepAliveTime;
    /**
     * 空闲连接的有效时间（单位-ms）
     */
    private int ConnectionKeepAliveTime;
    /**
     * 默认rabbitMQ最大连接数
     */
    private final int DEFAULT_CONNECTION_MAX = 5;
    /**
     * 默认rabbitMQ最小连接数
     */
    private final int DEFAULT_CONNECTION_MIN = 1;
    /**
     * 默认rabbitMQ每个连接中最大通道数
     */
    private final int DEFAULT_CHANNEL_MAX = 5;
    /**
     * 默认rabbitMQ每个连接中最小通道数
     */
    private final int DEFAULT_CHANNEL_MIN = 1;
    /**
     * 默认rabbitMQ每个通道的有效时间
     */
    private final int DEFAULT_CHANNEL_KEEP_ALIVE_TIME = 30;
    /**
     * 默认rabbitMQ每个连接的有效时间
     */
    private final int DEFAULT_CONNECTION_KEEP_ALIVE_TIME = 30;

    public RabbitMQConfig() {
        this.connectionMax = this.DEFAULT_CONNECTION_MAX;
        this.connectionMin = this.DEFAULT_CONNECTION_MIN;
        this.channelMax = this.DEFAULT_CHANNEL_MAX;
        this.channelMin = this.DEFAULT_CHANNEL_MIN;
        this.ConnectionKeepAliveTime = this.DEFAULT_CHANNEL_KEEP_ALIVE_TIME;
        this.channelKeepAliveTime = this.DEFAULT_CHANNEL_KEEP_ALIVE_TIME;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getConnectionMax() {
        return connectionMax;
    }

    public void setConnectionMax(int connectionMax) {
        this.connectionMax = connectionMax;
    }

    public int getConnectionMin() {
        return connectionMin;
    }

    public void setConnectionMin(int connectionMin) {
        this.connectionMin = connectionMin;
    }

    public int getChannelMax() {
        return channelMax;
    }

    public void setChannelMax(int channelMax) {
        this.channelMax = channelMax;
    }

    public int getChannelMin() {
        return channelMin;
    }

    public void setChannelMin(int channelMin) {
        this.channelMin = channelMin;
    }

    public int getChannelKeepAliveTime() {
        return channelKeepAliveTime;
    }

    public void setChannelKeepAliveTime(int channelKeepAliveTime) {
        this.channelKeepAliveTime = channelKeepAliveTime;
    }

    public int getConnectionKeepAliveTime() {
        return ConnectionKeepAliveTime;
    }

    public void setConnectionKeepAliveTime(int connectionKeepAliveTime) {
        ConnectionKeepAliveTime = connectionKeepAliveTime;
    }

    public int getDEFAULT_CONNECTION_MAX() {
        return DEFAULT_CONNECTION_MAX;
    }

    public int getDEFAULT_CONNECTION_MIN() {
        return DEFAULT_CONNECTION_MIN;
    }

    public int getDEFAULT_CHANNEL_MAX() {
        return DEFAULT_CHANNEL_MAX;
    }

    public int getDEFAULT_CHANNEL_MIN() {
        return DEFAULT_CHANNEL_MIN;
    }

    public int getDEFAULT_CHANNEL_KEEP_ALIVE_TIME() {
        return DEFAULT_CHANNEL_KEEP_ALIVE_TIME;
    }

    public int getDEFAULT_CONNECTION_KEEP_ALIVE_TIME() {
        return DEFAULT_CONNECTION_KEEP_ALIVE_TIME;
    }
}
