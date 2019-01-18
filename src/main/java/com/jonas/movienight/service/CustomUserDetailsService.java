package com.jonas.movienight.service;

import com.jonas.movienight.entity.UserEntity;
import com.jonas.movienight.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Created by Jonas Karlsson on 2019-01-18.
 */

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        UserEntity user = userRepository.findByUsername(s);

        if (user == null) throw new UsernameNotFoundException("User not found");

        return user;
    }

}