package com.aly.ecomapp.security;

import com.aly.ecomapp.testing.TestUserRepo;
import com.aly.ecomapp.exception.UserException;
import com.aly.ecomapp.exception.UserExceptionMessages;
import com.aly.ecomapp.testing.TestUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final TestUserRepo testUserRepo;

    @Autowired
    public CustomUserDetailService(TestUserRepo testUserRepo) {
        this.testUserRepo = testUserRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        TestUser appTestUser = testUserRepo.findByUsername(username);
        if (appTestUser == null) {
            throw new UserException(UserExceptionMessages.USER_NOT_FOUND);
        }


        return new org.springframework.security.core.userdetails.User(
                appTestUser.getUsername(),
                appTestUser.getPassword(),
                List.of(new SimpleGrantedAuthority(appTestUser.getRole().toString())));
    }
}

