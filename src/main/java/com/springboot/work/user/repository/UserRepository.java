package com.springboot.work.user.repository;

import com.springboot.work.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
   List<Users> findByNameIgnoreCase(String name);
   Users findByUsername(String username);
   Users findByUsernameIgnoreCase(String username);
   Users findByEmail(String email);



}
