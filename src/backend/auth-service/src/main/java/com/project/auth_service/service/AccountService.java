package com.project.auth_service.service;

import com.project.auth_service.dto.AccountDTO;
import com.project.auth_service.entity.Account;
import com.project.auth_service.mapper.AccountMapper;
import com.project.auth_service.repository.AccountRepository;
import com.project.common_package.exception.BadRequestException;
import com.project.common_package.exception.NotFoundException;
import com.project.common_package.exception.UnauthorizedException;
import com.project.common_package.exception.InternalServerException;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AccountService {

    @Inject
    AccountRepository accountRepository;

    @Inject
    JwtProvider jwtProvider;

    AccountMapper accountMapper = new AccountMapper();

    ////////////////////////////////////////
    // 1. REGISTER (void)
    ////////////////////////////////////////

    @Transactional
    public void register(AccountDTO.RegisterRequestDTO dto) {

        if (accountRepository.existsUsername(dto.Username)) {
            throw new BadRequestException("Username already exists");
        }

        if (accountRepository.existsEmail(dto.Email)) {
            throw new BadRequestException("Email already exists");
        }

        // Convert DTO -> Entity
        Account newAccount = accountMapper.RegisterRequestDTOToEntity(dto);

        // Hash password (Quarkus bcrypt)
        String hashed = BcryptUtil.bcryptHash(dto.Password);
        newAccount.setPassword(hashed);

        try {
            accountRepository.persist(newAccount);
        } catch (Exception e) {
            throw new InternalServerException("Error creating account: " + e.getMessage());
        }
    }


    ////////////////////////////////////////
    // 2. LOGIN (return LoginResponseDTO)
    ////////////////////////////////////////

    public AccountDTO.LoginResponseDTO login(AccountDTO.LoginRequestDTO dto) {

        Account entity = accountRepository.findByUsername(dto.Username);
        if (entity == null) {
            throw new UnauthorizedException("Invalid username or password");
        }

        if (!BcryptUtil.matches(dto.Password, entity.getPassword())) {
            throw new UnauthorizedException("Invalid username or password");
        }

        // Generate tokens
        String accessToken = jwtProvider.generateAccessToken(entity);
        String refreshToken = jwtProvider.generateRefreshToken(entity);

        return accountMapper.EntityToLoginResponseDTO(entity, accessToken, refreshToken);
    }


    ////////////////////////////////////////
    // 3. CHANGE PASSWORD (void)
    ////////////////////////////////////////

    @Transactional
    public void changePassword(AccountDTO.ChangePasswordRequestDTO dto) {

        Account account = accountRepository.findByUsername(dto.Username);
        if (account == null) {
            throw new NotFoundException("Account not found");
        }

        // dto.Password = old password
        if (!BcryptUtil.matches(dto.Password, account.getPassword())) {
            throw new UnauthorizedException("Incorrect current password");
        }

        // Hash new password
        String newHashed = BcryptUtil.bcryptHash(dto.Password);
        account.setPassword(newHashed);
    }


    ////////////////////////////////////////
    // 4. REFRESH TOKEN (return RefreshTokenResponseDTO)
    ////////////////////////////////////////

    public AccountDTO.RefreshTokenResponseDTO refresh(AccountDTO.RefreshTokenRequestDTO dto) {

        // Validate old refresh token
        String username = jwtProvider.validateRefreshToken(dto.RefreshToken);
        if (username == null) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            throw new NotFoundException("Account not found");
        }

        // Generate new tokens
        String newAccess = jwtProvider.generateAccessToken(account);
        String newRefresh = jwtProvider.generateRefreshToken(account);

        AccountDTO.RefreshTokenResponseDTO response = new AccountDTO.RefreshTokenResponseDTO();
        response.AccessToken = newAccess;
        response.RefreshToken = newRefresh;

        return response;
    }

}
