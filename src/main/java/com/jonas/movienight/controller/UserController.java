package com.jonas.movienight.controller;

import com.jonas.movienight.entity.UserEntity;
import com.jonas.movienight.service.MapValidationErrorService;
import com.jonas.movienight.service.UserService;
import com.jonas.movienight.validator.UserValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Created by Jonas Karlsson on 2019-01-18.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserValidator userValidator;
    private final MapValidationErrorService mapValidationErrorService;

    public UserController(UserService userService, UserValidator userValidator,
                          MapValidationErrorService mapValidationErrorService) {
        this.userService = userService;
        this.userValidator = userValidator;
        this.mapValidationErrorService = mapValidationErrorService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserEntity user, BindingResult bindingResult) {

        userValidator.validate(user, bindingResult);

        ResponseEntity<?> errorMap = mapValidationErrorService.MapValidationService(bindingResult);
        if (errorMap != null) return errorMap;

        UserEntity newUser = userService.saveUser(user);

        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserEntity getUser(@PathVariable(name = "id") Long id){
        return userService.getUser(id);
    }


}