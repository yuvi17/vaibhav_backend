package com.reminder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.reminder.controllers.SendReminder;

@SpringBootApplication
public class DemoApplication {


	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
		SendReminder sendReminder = new SendReminder();
		sendReminder.start();

		
	}
	
}
