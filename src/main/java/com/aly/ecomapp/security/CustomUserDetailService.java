package com.aly.ecomapp.security;
import com.aly.ecomapp.exception.UserException;
import com.aly.ecomapp.exception.UserExceptionMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class CustomUserDetailService implements UserDetailsService {
    // UserRepo to be added here
    //private final UserRepo userRepo;

    //Constructor to be added here once userrepo is created
//    @Autowired
//    public CustomUserDetailService(
//            TestUserRepo testUserRepo) {
//        this.testUserRepo = testUserRepo;
//    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        User appUser = testUserRepo.findByUsername(username);
        //Change name from static to appUser/User iteslef once userepo and user entity are cretaed
        //TODO IMPLEMENT USER FETCHING LOGIC
        String userName = "testUser"; // Replace with actual logic to fetch user from database
        if (userName == null) {
            throw new UserException(UserExceptionMessages.USER_NOT_FOUND);
        }

        //TODO IMPLEMENT PASSWORD USERNAME AND ROLE FETCHING
        return new org.springframework.security.core.userdetails.User(
                userName,
                "password", // Replace with actual password logic
                List.of(new SimpleGrantedAuthority(
                        "ROLE_USER" // Replace with actual roles logic
                )));
    }
}

