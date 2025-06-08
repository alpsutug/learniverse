package com.springboot.work.security;

import com.springboot.work.user.entity.Users;
import com.springboot.work.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users u = userRepository.findByEmail(email);
        if (u == null) {
            throw new UsernameNotFoundException("Kullanıcı bulunamadı: " + email);
        }
        return User.builder()
                .username(u.getEmail())
                .password(u.getPassword())
                .authorities("USER")
                .disabled(!u.isEnabled())   // 👈 aktif değilse Spring login’i reddeder
                .build();
    }
}
