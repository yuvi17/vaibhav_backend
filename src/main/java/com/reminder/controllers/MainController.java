package com.reminder.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reminder.models.LoginResponseModel;
import com.reminder.models.Register;
import com.reminder.models.Reminder;
import com.reminder.models.RequestModel;

@RestController
public class MainController {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
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
		if(originalPassword.equals(body.getPassword())) {
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
			jdbcTemplate.update(sql,new Object[] {register.getName(),register.getEmail(), register.getPassword() });
			response.status = "Success";
			response.message = "User " + register.getName() + " added";
			response.isValid = true;
		}
		catch(Exception e) {
			response.status = "Error";
			response.message = "User " + register.getName() + " could not be added";
			response.isValid = false;
		}
		return response;
		
	}
	
	@CrossOrigin
	@RequestMapping("/reminder")
	public LoginResponseModel Reminder(@RequestBody Reminder reminder) {
		LoginResponseModel response = new LoginResponseModel();
		try {
			String sql = "INSERT into reminder(name,time,email,repeat,active,description) values(?,?,?,?,?,?)";
			jdbcTemplate.update(sql,new Object[] {reminder.getName(),reminder.getTime(),reminder.getEmail(),
													reminder.isRepeat(), reminder.isActive(), reminder.getDescription() });
			response.status = "Success";
			response.message = "Reminder " + reminder.getName() + " added";
			response.isValid = true;
		}
		catch(Exception e) {
			response.status = "Error";
			response.message = "Reminder " + reminder.getName() + " could not be added";
			response.isValid = false;
		}
		return response;
	}

}
