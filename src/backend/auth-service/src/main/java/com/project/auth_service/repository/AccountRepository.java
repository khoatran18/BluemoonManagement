package com.project.auth_service.repository;

import com.project.auth_service.entity.Account;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AccountRepository implements PanacheRepository<Account> {

    public Account findByUsername(String username) {
        return find("username", username).firstResult();
    }

    public Account findByEmail(String email) {
        return find("email", email).firstResult();
    }

    public boolean existsUsername(String username) {
        return count("username", username) > 0;
    }

    public boolean existsEmail(String email) {
        return count("email", email) > 0;
    }
}
