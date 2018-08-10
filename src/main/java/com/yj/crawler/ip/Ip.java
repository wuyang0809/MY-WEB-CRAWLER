package com.yj.crawler.ip;

import org.jsoup.Connection;
import org.jsoup.helper.HttpConnection;

import javax.xml.bind.Element;
import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * @author wuyang
 * @date 2018/8/8 16:08
 */
public class Ip {

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 端口号
     */
    private int ipPort;

    public Ip(String ipAddress, int ipPort) {
        this.ipAddress = ipAddress;
        this.ipPort = ipPort;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getIpPort() {
        return ipPort;
    }

    public void setIpPort(int ipPort) {
        this.ipPort = ipPort;
    }

    public boolean check(){
        boolean flag = false;
        try{
            Connection connection = HttpConnection.connect("http://www.baidu.com");
            connection.proxy(this.ipAddress,this.ipPort);
            connection.timeout(3000);
            connection.get();
            Connection.Response response = connection.response();

            if(response.statusCode() == 200){
                flag = true;
            }
        }catch (Exception e){
            //e.printStackTrace();
        }
        return flag;
    }
}
