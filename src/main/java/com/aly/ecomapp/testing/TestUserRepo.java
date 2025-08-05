package com.aly.ecomapp.testing;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TestUserRepo extends JpaRepository<TestUser,Long> {

    TestUser findByUsername(String username);

    TestUser findUserById(Long id);
}
