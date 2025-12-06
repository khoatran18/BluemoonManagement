package com.project.auth_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.auth_service.entity.Account;

public class AccountDTO {

    ///////////////////////////// Login /////////////////////////////

    public static class LoginRequestDTO {
        @JsonProperty("username")
        public String Username;

        @JsonProperty("password")
        public String Password;
    }

    public static class LoginResponseDTO {
        @JsonProperty("username")
        public String Username;

        @JsonProperty("email")
        public String Email;

        @JsonProperty("identity_number")
        public String IdentityNumber;

        @JsonProperty("role")
        public Account.RoleEnum Role;

        @JsonProperty("access_token")
        public String AccessToken;

        @JsonProperty("refresh_token")
        public String RefreshToken;
    }

    ///////////////////////////// Register /////////////////////////////

    public static class RegisterRequestDTO {
        @JsonProperty("username")
        public String Username;

        @JsonProperty("password")
        public String Password;

        @JsonProperty("email")
        public String Email;

        @JsonProperty("identity_number")
        public String IdentityNumber;

        @JsonProperty("role")
        public Account.RoleEnum Role;
    }

    ///////////////////////////// Change Password /////////////////////////////

    public static class ChangePasswordRequestDTO {
        @JsonProperty("username")
        public String Username;

        @JsonProperty("password")
        public String Password;
    }

    ///////////////////////////// Refresh Token /////////////////////////////

    public static class RefreshTokenRequestDTO {
        @JsonProperty("access_token")
        public String AccessToken;

        @JsonProperty("refresh_token")
        public String RefreshToken;
    }

    public static class RefreshTokenResponseDTO {
        @JsonProperty("access_token")
        public String AccessToken;

        @JsonProperty("refresh_token")
        public String RefreshToken;
    }

    ///////////////////////////// GET ACCOUNT /////////////////////////////

    public static class GetAccountResponseDTO {

        @JsonProperty("account_id")
        public Long AccountId;

        @JsonProperty("username")
        public String Username;

        @JsonProperty("email")
        public String Email;

        @JsonProperty("identity_number")
        public String IdentityNumber;

        @JsonProperty("role")
        public Account.RoleEnum Role;

        @JsonProperty("resident_id")
        public Long ResidentId;
    }

}
