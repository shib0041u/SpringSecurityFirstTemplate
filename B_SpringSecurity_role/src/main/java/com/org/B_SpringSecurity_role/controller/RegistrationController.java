package com.org.B_SpringSecurity_role.controller;



import com.org.B_SpringSecurity_role.model.MyUser;
import com.org.B_SpringSecurity_role.repo.MyUserRepo;
import com.org.B_SpringSecurity_role.services.MyUserDetailsServices;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class RegistrationController {
    
    @Autowired
    private MyUserRepo repo;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

	@Autowired
    private JavaMailSender mailSender;
    
    private String generatedOtp;
    @PostMapping("/register")
    public String register(@ModelAttribute MyUser user, Model model) {
        String result = registerUser(user);
        if ("User registered successfully!".equals(result)) {
            return "redirect:/login"; 
        } else {
            model.addAttribute("error", result);
            return "register";
        }
    }

    public String registerUser(MyUser user) {
        if (repo.existsByEmailOrUsername(user.getEmail(),user.getUsername()) ) {
            return "Email Or UserName already exists!";
        }
        
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        if (repo.count() == 0) {
            user.setRole("ROLE_ADMIN"); 
        } else {
            user.setRole("ROLE_GUEST");
        }

        repo.save(user); 
        return "User registered successfully!";
    }
    
    
    @PostMapping("/forgot-password")
    public String handleForgotPassword(String input, Model model) {
    	MyUser user;
//    	System.out.println(input);
    	boolean res=MyUserDetailsServices. isEmail(input);
//    	System.out.println(res);
    	if(res) {
    		user=repo.findByEmail(input);
    	}else {
    		user=repo.findByUsername(input);   		
    	}
        if (user != null) {
            generatedOtp = generateOtp();
            sendSimpleMessage(user.getEmail(),"We Got a request for Password Change",""
            		+ "Your One Time passWord is: "+generatedOtp);
            model.addAttribute("email", user.getEmail());
            return "verify-otp"; 
        } else {
        	if(res)
        		model.addAttribute("error", "Email not found");
        	else
        		model.addAttribute("error", "User name not found");
            return "forgot-password";
        }
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(String otp, String email, Model model) {
        if (generatedOtp.equals(otp)) {
            model.addAttribute("email", email);
            return "reset-password"; 
        } else {
            model.addAttribute("error", "Invalid OTP");
            return "verify-otp"; 
        }
    }

    @PostMapping("/reset-password")
    public String resetPassword(String newPassword, String email, Model model) {
        MyUser user = repo.findByEmail(email);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(newPassword));
            repo.save(user); 
            return "login";
        } else {
            model.addAttribute("error", "User not found");
            return "reset-password";
        }
    }

    private String generateOtp() {
    	String Capital_chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; 
        String Small_chars = "abcdefghijklmnopqrstuvwxyz"; 
        String numbers = "0123456789"; 
//        String symbols = "!@#$%^&*_=+-/.?<>)";
        String values = Capital_chars + Small_chars + numbers;
        int length = 62; 
        Random random = new Random();
        int size= 6;
        generatedOtp= "";
        for(int i = 0;i<size;i++) {
        	generatedOtp+=values.charAt(random.nextInt(length));
        }
		return generatedOtp;
    }

    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
