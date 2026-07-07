package com.bookmysport.backend.security.service;

import com.bookmysport.backend.exception.ResourseNotFoundException;
import com.bookmysport.backend.security.models.SecurityUser;
import com.bookmysport.backend.user.entity.UserEntity;
import com.bookmysport.backend.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomerDetailService implements UserDetailsService{

    @Autowired
    UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {


        return userRepository.findByEmail(email)
                .map(SecurityUser::new)
                .orElseThrow(() ->
                        new ResourseNotFoundException(
                                "User not found with email " + email
                        )
                );
    }
}

