package com.org.B_SpringSecurity_role.services;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.org.B_SpringSecurity_role.model.CustomUserDetails;
import com.org.B_SpringSecurity_role.model.MyUser;
import com.org.B_SpringSecurity_role.repo.MyUserRepo;

@Service
public  class MyUserDetailsServices implements UserDetailsService {
    private static final Pattern EMAIL_REGEX = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public static boolean isEmail(String input) {
        return EMAIL_REGEX.matcher(input).matches();
    }
	@Autowired
	private MyUserRepo repo;
	@Override
	public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {
		MyUser user;
		if(isEmail(input)) {
			user = repo.findByEmail(input);
		}else {
			user= repo.findByUsername(input);
		}
		if(user!=null) {
//			System.out.println("got it");
//			return User.builder()
//					.username(user.getUsername())
//					.email(user.getEmail())			//not working for email
//					.password(user.getPassword()) //824936
//					.roles(getRole(user))
//					.build();
			
			return new CustomUserDetails(
					user.getName(),
					user.getUsername(),
					user.getEmail(),
					user.getPassword(),
					AuthorityUtils.createAuthorityList(getRole(user)));
		}else {
			throw new UsernameNotFoundException(input);
		}
//		Optional<MyUser > user= repo.findByUsername(username);
//		if(user.isPresent()) {
//			System.out.println("got it");
//			var userObj= user.get();
//			return User.builder()
//					.username(userObj.getUsername())
//					.password(userObj.getPassword()) //824936
//					.roles(getRole(userObj))
//					.build();
//		}else {
//			throw new UsernameNotFoundException(username);
//		}
//		return null;
	}
	private String[] getRole(MyUser user) {
		if(user.getRole()==null)
			return new String[] {"User"};
		return user.getRole().split(",");
	}

}
