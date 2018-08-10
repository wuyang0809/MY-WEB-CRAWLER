package com.yj.crawler;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import com.yj.crawler.config.RabbitMQConfig;
import com.yj.crawler.config.RabbitMQConnectionPool;
import com.yj.crawler.ip.IpFilter;
import com.yj.crawler.ip.IpProduce;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MyWebCrawlerApplicationTests {

	@Test
	public void contextLoads() {
		Thread ipProduce = new IpProduce();
		ipProduce.start();
	}

}
