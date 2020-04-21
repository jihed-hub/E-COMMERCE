package com.example.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Model.Address;
import com.example.demo.Model.User;

@Repository
@Transactional
public interface AddressRepository extends JpaRepository<Address, Long>{

	Address findByUser(User user);
}
