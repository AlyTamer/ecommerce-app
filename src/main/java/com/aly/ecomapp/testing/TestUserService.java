package com.aly.ecomapp.testing;

import com.aly.ecomapp.exceptions.UserException;
import com.aly.ecomapp.exceptions.UserExceptionMessages;
import com.aly.ecomapp.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class TestUserService {
    private final TestUserRepo testUserRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    public TestUserService(TestUserRepo testUserRepo, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.testUserRepo = testUserRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }
    public String createUser(TestUserDTO inpUser){

       TestUser newTestUser = new TestUser();
       newTestUser.setUsername(inpUser.getUsername());
       newTestUser.setEmail(inpUser.getEmail());
       newTestUser.setPassword(passwordEncoder.encode(inpUser.getPassword()));
       newTestUser.setRole(TestRoles.ROLE_USER);
        testUserRepo.save(newTestUser);
        String token =jwtUtil.generateToken(newTestUser.getUsername());
        System.out.println("New User Created with token: " + token+"\n and username: "+ newTestUser.getUsername() );
        return token;
    }

    public String createAdmin(TestUserDTO inpUser) {
        TestUser newTestUser = new TestUser();
        newTestUser.setUsername(inpUser.getUsername());
        newTestUser.setEmail(inpUser.getEmail());
        newTestUser.setPassword(passwordEncoder.encode(inpUser.getPassword()));
        newTestUser.setRole(TestRoles.ROLE_ADMIN);
        testUserRepo.save(newTestUser);
        var token =jwtUtil.generateToken(newTestUser.getUsername());
        System.out.println("New Admin Created with token: " + token+"\n and username: "+ newTestUser.getUsername() );
        return token;
    }

    public void changePassword(String newPassword,String oldPassword, Long id) {
        TestUser testUser = testUserRepo.findUserById(id);
        if(testUser ==null) throw new UserException(UserExceptionMessages.userNotFound);
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        if(!passwordEncoder.matches(oldPassword, testUser.getPassword())) {
            throw new UserException(UserExceptionMessages.invalidCredentials);
        }
        testUser.setPassword(encodedNewPassword);
        testUserRepo.save(testUser);
    }
}
