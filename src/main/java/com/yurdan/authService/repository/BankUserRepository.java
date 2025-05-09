package com.yurdan.authService.repository;

import com.yurdan.authService.model.entity.BankUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankUserRepository extends JpaRepository<BankUser, String> {
    BankUser findByEmail(String email);
}
