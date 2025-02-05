package com.org.B_SpringSecurity_role.repo;

//import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.org.B_SpringSecurity_role.model.MyUser;

public interface MyUserRepo extends JpaRepository<MyUser, Long> {
//Optional  <MyUser> findByUsername(String username);
MyUser findByEmail(String email); 
MyUser findByUsername(String username);
boolean existsByEmailOrUsername(String email, String username);
//MyUser findByEmailOrUsername(String email, String username);
}
