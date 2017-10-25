package com.reminder.controllers;

import org.springframework.web.client.RestTemplate;

public class SendReminder extends Thread {
	
	
	public void run () {
		
		System.out.println("Thread has been started");
		
		RestTemplate  restTemplate = new RestTemplate();
		
		try {
		restTemplate.getForObject("http://localhost:8080/start", void.class);
		} catch(Exception e) {
			System.out.println(e.toString());
		}
	}

}
