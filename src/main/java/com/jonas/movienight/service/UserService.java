package com.jonas.movienight.service;

import com.jonas.movienight.entity.UserEntity;
import com.jonas.movienight.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

/**
 * Created by Jonas Karlsson on 2019-01-18.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public UserEntity saveUser(UserEntity user) {
        try {
            user.setUsername(user.getUsername());
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            return userRepository.save(user);
        } catch (Exception e) {
            throw new EntityNotFoundException();
        }
    }

    public UserEntity getUser(Long id){
        UserEntity user = userRepository.getById(id);

        if (user == null){
            throw new EntityNotFoundException();
        }

        return user;
    }

}