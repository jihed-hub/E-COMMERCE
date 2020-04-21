package com.example.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Model.User;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Long> {
	
	User findByEmailAndPasswordAndUsertype(String email, String password, String usertype);

}
