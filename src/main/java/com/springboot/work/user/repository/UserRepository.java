package com.springboot.work.user.repository;

import com.springboot.work.quiz.entity.QuizResult;
import com.springboot.work.user.dto.UserInfoResponseDTO;
import com.springboot.work.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
   List<Users> findByNameIgnoreCase(String name);
   Users findByUsername(String username);
   Users findByUsernameIgnoreCase(String username);
   Users findByEmail(String email);
   Optional<Users> findByEmailIgnoreCase(String email);


}
