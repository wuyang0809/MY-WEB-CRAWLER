package com.yj.crawler;

import com.yj.crawler.main.MedicalInsuranceDrugCrawler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

@SpringBootApplication
@Controller
public class MyWebCrawlerApplication {

	@Resource
	private MedicalInsuranceDrugCrawler medicalInsuranceDrugCrawler;

	public static void main(String[] args) {
		SpringApplication.run(MyWebCrawlerApplication.class, args);


	}
}
