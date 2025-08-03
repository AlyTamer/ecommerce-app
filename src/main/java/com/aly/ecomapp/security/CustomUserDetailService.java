package com.aly.ecomapp.security;

import com.aly.ecomapp.UserRepo;
import com.aly.ecomapp.exceptions.UserException;
import com.aly.ecomapp.exceptions.UserExceptionMessages;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepo userRepo;

    @Autowired
    public CustomUserDetailService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User appUser = userRepo.findByUserName(username);
        if (appUser == null) {
            throw new UserException(UserExceptionMessages.userNotFound);
        }


        return new org.springframework.security.core.userdetails.User(
                appUser.getUsername(),
                appUser.getPassword(),
                List.of(new SimpleGrantedAuthority(appUser.getRoles().toString())));
    }
}

