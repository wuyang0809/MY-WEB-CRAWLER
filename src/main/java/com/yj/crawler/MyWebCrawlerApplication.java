package com.yj.crawler;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import com.yj.crawler.config.RabbitMQConnectionPool;
import com.yj.crawler.ip.Ip;
import com.yj.crawler.ip.IpFilter;
import com.yj.crawler.ip.IpProduce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@SpringBootApplication
@Controller
public class MyWebCrawlerApplication {

	static ThreadPoolExecutor executor = new ThreadPoolExecutor(150,
			150,
			60,
			TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
			new IpFilter(),
			new ThreadPoolExecutor.AbortPolicy());

	@Autowired
	RabbitMQConnectionPool rabbitMQConnectionPool;

	public static void main(String[] args) {
		SpringApplication.run(MyWebCrawlerApplication.class, args);
		Thread ipProduce = new IpProduce();
		ipProduce.start();

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				while(true){
					try {
						Ip ip = IpProduce.ipOriginal.take();
						if(ip.check()){
							IpProduce.ipEffective.put(ip);
							System.out.println("IP有效，存入队列"+IpProduce.ipEffective.size());
						}else{
							System.out.println("IP无效"+IpProduce.ipEffective.size());
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		for(int i=0;i<100;i++){
			executor.execute(runnable);
			System.out.println("任务数量"+executor.getPoolSize());
		}
	}

	@RequestMapping(value = "/test")
	@ResponseBody
	public String test(){
		try {
			Channel channel = (Channel) rabbitMQConnectionPool.getChannel();
			channel.queueDeclare("task_queue", true, false, false, null);
			for(int i=0;i<10;i++){
				String message = "Hello RabbitMQ" + i;
				channel.basicPublish("","task_queue", MessageProperties.PERSISTENT_TEXT_PLAIN,
						message.getBytes());
				System.out.println("NewTask send :'"+message+"'");
			}
			channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		return "";
	}
}
