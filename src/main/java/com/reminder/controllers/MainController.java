package com.reminder.controllers;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.reminder.models.LoginResponseModel;
import com.reminder.models.Register;
import com.reminder.models.Reminder;
import com.reminder.models.RequestModel;

@RestController
public class MainController {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private JavaMailSenderImpl mailSender;
	
	
	
	@CrossOrigin
	@RequestMapping("/login")
	public LoginResponseModel Login(@RequestBody RequestModel body) {
		LoginResponseModel response = new LoginResponseModel();
		String sql = "SELECT * FROM USER WHERE EMAIL = '" + body.getEmail() + "'";
		System.out.println(sql);
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		if(list.size() == 0) {
			response.message = "Email ID does not exist";
			response.isValid = false;
			response.status = "Success";
			return response;
		}
		String originalPassword = (String)list.get(0).get("password");
		System.out.println(originalPassword);
		if(encryptPassword(originalPassword).equals(encryptPassword(body.getPassword()))) {
			response.message = "Welcome User";
			response.isValid = true;
			response.status = "Success";
		} else {
			response.message = "Wrong Password";
			response.isValid = false;
			response.status = "Success";
		}
		
		return response;
		
	}
	
	
	@CrossOrigin
	@RequestMapping("/register")
	public LoginResponseModel Register(@RequestBody Register register) {
		LoginResponseModel response = new LoginResponseModel();
		try {
			String sql = "INSERT into user(name,email,password) values(?,?,?)";
			jdbcTemplate.update(sql,new Object[] {register.getName(),register.getEmail(), encryptPassword(register.getPassword()) });
			response.status = "Success";
			response.message = "User " + register.getName() + " added";
			response.isValid = true;
		}
		catch(Exception e) {
			System.out.println(e.toString());
			response.status = "Error";
			response.message = "User " + register.getName() + " could not be added" + e.toString();
			response.isValid = false;
		}
		return response;
		
	}
	
	@CrossOrigin
	@RequestMapping("/reminder")
	public LoginResponseModel Reminder(@RequestBody Reminder reminder) {
		LoginResponseModel response = new LoginResponseModel();
		try {
			String sql = "INSERT into reminder(name,time,email,description,active,repeated) values(?,?,?,?,?,?)";
			jdbcTemplate.update(sql,new Object[] {reminder.getName(),reminder.getTime(),reminder.getEmail(),
													reminder.getDescription(),true,true });
			response.status = "Success";
			response.message = "Reminder " + reminder.getName() + " added";
			response.isValid = true;
		}
		catch(Exception e) {
			System.out.println(e.toString());
			response.status = "Error";
			response.message = "Reminder " + reminder.getName() + " could not be added";
			response.isValid = false;
		}
		return response;
	}
	
	
	
	// helper functions to send reminder as mails
	
	
	public List<Map<String,Object>> getReminders() {
		try {
			String sql = "SELECT * FROM REMINDER";
			List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
			return list;
		
		} catch (Exception e) {
			
			System.out.println(e.toString() + " Exception in database read");
			List<Map<String, Object>> list = new ArrayList<>();
			return list;
		}
	}
	
	public boolean sendReminderAsEmail(Map<String,Object> m) {
		
		RestTemplate restTemplate = new RestTemplate();
		
		
		Map<String,String> body = new HashMap<String,String>();
		body.put("email", m.get("email").toString());
		body.put("message", m.get("description").toString());
		body.put("name", m.get("name").toString());
		
		
		
		try {
			restTemplate.postForObject("http://localhost:9191/mail/contact",body, new HashMap<String,Object>().getClass());
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	
	
	@CrossOrigin
	@RequestMapping("/start")
	public void startReminder() {
		while(true) {
			List<Map<String,Object>> list = getReminders();
			for(Map<String,Object> m : list) {
				
				Calendar c = Calendar.getInstance();
				c.setTimeZone(TimeZone.getTimeZone("IST"));
				long diff = (long)c.getTimeInMillis() - (long)m.get("time") ;
				System.out.println(diff);
				if(Math.abs(diff) <= 5000){
					boolean isSent = sendReminderAsEmail(m);
					
					if(isSent) {
						System.out.println("Reminder has been sent successfully by email");
					} else {
						System.out.println("Error in sending email for reminder");
					}
				}
			}
				
			
			try {
				Thread.sleep(5000);
			} catch (Exception e) {
				continue;
			}
			
		}
	}
	
	@RequestMapping("/send")
	public Boolean sendEmail() {
		try {
			MimeMessage message = mailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message);
	        
	        helper.setTo("kumaryuvraj118@gmail.com");
	        helper.setText("TEST");
	        helper.setSubject("TEST");
	        mailSender.send(message);
	        System.out.println("Email sent");
	        return true;
		} catch(Exception e) {
			System.out.println(e.toString());
			return false;
		}
	}
	
	private String encryptPassword(String password) {
		String passwordToHash = "password";
        String generatedPassword = null;
        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Add password bytes to digest
            md.update(passwordToHash.getBytes());
            //Get the hash's bytes
            byte[] bytes = md.digest();
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            generatedPassword = sb.toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (generatedPassword);
		
	}

}
