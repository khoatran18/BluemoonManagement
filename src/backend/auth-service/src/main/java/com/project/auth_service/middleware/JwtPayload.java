package com.project.auth_service.middleware;

import com.project.auth_service.entity.Account;

public class JwtPayload {

    public String accountId;
    Account.RoleEnum role;

    public JwtPayload(String accountId, Account.RoleEnum role) {
        this.accountId = accountId;
        this.role = role;
    }

}
