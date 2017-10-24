package com.reminder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;

import com.reminder.controllers.SendReminder;

@SpringBootApplication
public class DemoApplication {
	
	@Autowired
	public static JdbcTemplate jdbcTemplate;
	
	@Autowired
	public static JavaMailSender mailSender;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
		
		SendReminder sendReminder = new SendReminder(jdbcTemplate, mailSender);
		sendReminder.start();
		
	}
}
