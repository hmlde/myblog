package com.hanmlet.myblog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "com.hanmlet.myblog.common", "com.hanmlet.myblog" })
public class MyBlogApp {
	public static void main(String[] args) {
		SpringApplication.run(MyBlogApp.class, args);
	}
}
