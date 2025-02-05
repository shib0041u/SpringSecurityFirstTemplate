package com.org.B_SpringSecurity_role.config;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.org.B_SpringSecurity_role.services.AuthenticationSuccessHandler;
import com.org.B_SpringSecurity_role.services.MyUserDetailsServices;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
	@Autowired
	private MyUserDetailsServices userDetail;
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http)throws Exception {
		
		return http
				.csrf(httpSecurityCsrfConfigurer-> httpSecurityCsrfConfigurer.disable())
//				.csrf(AbtractHttpConfigurer::disable)
				.authorizeHttpRequests(registry->{
					registry.requestMatchers("/register/**","/forgot-password","/reset-password","/verify-otp").permitAll();
					registry.requestMatchers("/admin/**").hasRole("ADMIN");
					registry.requestMatchers("/user/**").hasAnyRole("USER", "ADMIN");
					registry.requestMatchers("/home/**").hasAnyRole("GUEST","USER", "ADMIN");
					registry.anyRequest().authenticated();
				})
//				.formLogin(formLogin-> formLogin.permitAll())
//				.formLogin(AbstractAuthenticationFilterConfigurer::permitAll)
				.formLogin(httpSecurityFormLoginConfigurer-> 
				httpSecurityFormLoginConfigurer.loginPage("/login")
				.successHandler(new AuthenticationSuccessHandler())
				.permitAll())
				.logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout") // Redirect to login page after logout
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
				//.addFilterBefore(new CustomLogoutFilter(), LogoutFilter.class)	//Custom logout methods
																					//added if redirect to register, By default logout
				.build();
		
	}
//	@Bean
//	public UserDetailsService userDetailsService() {
//		UserDetails normalUser= User.builder()
//				.username("shib0041u")
//				.password("$2a$12$aT/6smunATwYjx4TmOo9POX1z/C7D5AgItZaFc30bsZuH6AFHKQ2K") //123123
//				.roles("USER")
//				.build();
//		UserDetails adminUser= User.builder()
//				.username("shubh0041u")
//				.password("$2a$12$aUd037PI4pOtO0LQQG2PeunOq/4Ihlf0A70P8uziZxomxxkLKfUXm") //824936
//				.roles("ADMIN","USER")
//				.build();
//		return new InMemoryUserDetailsManager(normalUser,adminUser);
//		
//	}
	
	@Bean
	public UserDetailsService userDetailsService() {
		return userDetail;
		
	}
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider= new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetail);;
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
