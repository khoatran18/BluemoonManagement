package com.project.resident_fee_service.middleware;

import com.project.resident_fee_service.entity.Account;

public class JwtPayload {

    public String accountId;
    Account.RoleEnum role;

    public JwtPayload(String accountId, Account.RoleEnum role) {
        this.accountId = accountId;
        this.role = role;
    }

}
