package com.reminder.controllers;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;




public class SendReminder extends Thread {
	
	private JdbcTemplate jdbcTemplate;
	private JavaMailSender mailSender;
	
	public SendReminder(JdbcTemplate jdbcTemplate, JavaMailSender mailSender) {
		
		this.jdbcTemplate = jdbcTemplate;
		this.mailSender = mailSender;
	}
	
	
	public void run () {
		
		while(true) {
			String sql = "SELECT * FROM REMINDER";
			
			List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
			
			for(Map<String,Object> m : list) {
				
				Calendar c = Calendar.getInstance();
//				c.setTimeZone(TimeZone.getTimeZone("UTC"));
				
				long diff = (long)c.getTimeInMillis() - (long)m.get("time") ;
				
				if(Math.abs(diff) < 5000){}
				try {
					MimeMessage message = mailSender.createMimeMessage();
			        MimeMessageHelper helper = new MimeMessageHelper(message);
			        
			        helper.setTo((m.get("email")).toString());
			        helper.setText(m.get("description").toString());
			        helper.setSubject("Reminder for " + m.get("name").toString());
			        
			        mailSender.send(message);
				} catch(Exception e) {
					System.out.println(e.toString());
				}
			}
			
			try {
				Thread.sleep(5000);
			} catch (Exception e) {
				continue;
			}
			
		}
		
	}

}
