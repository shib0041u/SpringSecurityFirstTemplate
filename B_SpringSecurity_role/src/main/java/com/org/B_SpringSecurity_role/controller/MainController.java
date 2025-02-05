package com.org.B_SpringSecurity_role.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.org.B_SpringSecurity_role.model.MyUser;
import com.org.B_SpringSecurity_role.repo.MyUserRepo;



@Controller
public class MainController {
	


	@Autowired
	private MyUserRepo repo; 

	@GetMapping("/home")
	public String homepage() {
		return "home";
	}
	@GetMapping("/user")
	public String userhomepage() {
		return "UserHome";
	}
	@GetMapping("/admin")
    public String adminHome(Model model, Authentication authentication) {
        return "adminHome";  
    }
	
	@GetMapping("/admin/showMyDetails")
	public String adminShowMyDetails(Model model, Authentication authentication) {
		String username = authentication.getName();		
		MyUser adminUser = repo.findByUsername(username);
		if (adminUser != null) {
			model.addAttribute("email", adminUser.getEmail());
			model.addAttribute("username", adminUser.getUsername());
			model.addAttribute("name", adminUser.getName());
			model.addAttribute("role", adminUser.getRole());
			model.addAttribute("password", adminUser.getPassword());
		}
		
		return "AdminDetails";  
	}
	 @GetMapping("/admin/showAllUsers")
	    public String showAllUsers(Model model) {
	        List<MyUser> allUsers = repo.findAll();
	        model.addAttribute("users", allUsers);
	        return "showAllUsersAdmin";
	    }

	 @PostMapping("/admin/update-user")
	    public String updateUser(@ModelAttribute MyUser user) {
//		 System.out.println(user.getPassword());
	        repo.save(user);
	        return "redirect:/admin/showAllUsers"; 
	    }

	    
	    @PostMapping("/admin/delete-user")
	    public String deleteUser(@RequestParam("id") Long id) {
	        repo.deleteById(id);
	        return "redirect:/admin/showAllUsers";
	    }
	 
	@GetMapping("/login")
	public String customLoginpage() {
		return "custom_login";
	}
	@GetMapping("/register")
	public String customRegister() {
//		System.out.println("hiii");
		return "register";
	}
	@GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "forgot-password"; 
    }
}