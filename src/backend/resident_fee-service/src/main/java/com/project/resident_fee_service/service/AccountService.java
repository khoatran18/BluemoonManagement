package com.project.resident_fee_service.service;

import com.project.resident_fee_service.dto.AccountDTO;
import com.project.resident_fee_service.entity.Account;
import com.project.resident_fee_service.entity.Resident;
import com.project.resident_fee_service.mapper.AccountMapper;
import com.project.resident_fee_service.repository.AccountRepository;
import com.project.common_package.exception.BadRequestException;
import com.project.common_package.exception.NotFoundException;
import com.project.common_package.exception.UnauthorizedException;
import com.project.common_package.exception.InternalServerException;
import com.project.resident_fee_service.repository.ResidentRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class AccountService {

    private static final Logger log =
            LoggerFactory.getLogger(AccountService.class);

    @Inject
    AccountRepository accountRepository;

    @Inject
    ResidentRepository residentRepository;

    @Inject
    JwtProvider jwtProvider;

    AccountMapper accountMapper = new AccountMapper();

    ////////////////////////////////////////
    // 1. REGISTER (void)
    ////////////////////////////////////////

    @Transactional
    public void register(AccountDTO.RegisterRequestDTO dto) {

        log.info("[Account] [Service] getPayHistoriesByFilter Start");
        log.info(
                "Input: RegisterRequestDTO={}", dto
        );

        if (accountRepository.existsUsername(dto.Username)) {
            log.error("[Account] [Service] register Error: Username already exists");
            throw new BadRequestException("Username already exists");
        }


        // Convert DTO -> Entity
        Account newAccount = accountMapper.RegisterRequestDTOToEntity(dto);

        // Hash password (Quarkus bcrypt)
        String hashed = BcryptUtil.bcryptHash(dto.Password);
        newAccount.setPassword(hashed);

        try {
            Resident r = residentRepository.find("email", dto.Email).firstResult();
            if (r == null) {
                log.error("[Account] [Service] register Error: Not found email " + dto.Email);
                throw new NotFoundException("Account delete not found with email: " + dto.Email);
            }
            newAccount.setResident(r);
            accountRepository.persist(newAccount);

            log.info("[Account] [Service] register End");
            log.info("Output: None");
        } catch (Exception e) {
            log.error("[Account] [Service] register Error: Error creating account");
            throw new InternalServerException("Error creating account: " + e.getMessage());
        }
    }


    ////////////////////////////////////////
    // 2. LOGIN (return LoginResponseDTO)
    ////////////////////////////////////////

    public AccountDTO.LoginResponseDTO login(AccountDTO.LoginRequestDTO dto) {

        log.info("[Account] [Service] login Start");
        log.info("Input: LoginRequestDTO={}", dto);

        Account entity = accountRepository.findByUsername(dto.Username);
        if (entity == null) {
            log.error("[Account] [Service] login Error: Invalid username or password");
            throw new UnauthorizedException("Invalid username or password");
        }

        if (!BcryptUtil.matches(dto.Password, entity.getPassword())) {
            log.error("[Account] [Service] login Error: Invalid username or password");
            throw new UnauthorizedException("Invalid username or password");
        }

        // Generate tokens
        String accessToken = jwtProvider.generateAccessToken(entity);
        String refreshToken = jwtProvider.generateRefreshToken(entity);

        log.info("[Account] [Service] login End");
        log.info("Output: accessToken, refreshToken");

        return accountMapper.EntityToLoginResponseDTO(entity, accessToken, refreshToken);
    }


    ////////////////////////////////////////
    // 3. CHANGE PASSWORD (void)
    ////////////////////////////////////////

    @Transactional
    public void changePassword(AccountDTO.ChangePasswordRequestDTO dto) {

        log.info("[Account] [Service] changePassword Start");
        log.info("Input: ChangePasswordRequestDTO={}", dto);

        Account account = accountRepository.findByUsername(dto.Username);
        if (account == null) {
            log.error("[Account] [Service] changePassword Error: Account not found");
            throw new NotFoundException("Account not found");
        }

        // dto.Password = old password
        if (!BcryptUtil.matches(dto.OldPassword, account.getPassword())) {
            log.error("[Account] [Service] changePassword Error: Incorrect current password");
            throw new UnauthorizedException("Incorrect current password");
        }

        // Hash new password
        String newHashed = BcryptUtil.bcryptHash(dto.NewPassword);
        account.setPassword(newHashed);

        log.info("[Account] [Service] changePassword End");
        log.info("Output: None");
    }


    ////////////////////////////////////////
    // 4. REFRESH TOKEN (return RefreshTokenResponseDTO)
    ////////////////////////////////////////

    public AccountDTO.RefreshTokenResponseDTO refresh(AccountDTO.RefreshTokenRequestDTO dto) {

        log.info("[Account] [Service] refresh Start");
        log.info("Input: RefreshTokenRequestDTO={}", dto);

        // Validate old refresh token
        String username = jwtProvider.validateRefreshToken(dto.RefreshToken);
        if (username == null) {
            log.error("[Account] [Service] refresh Error: Invalid refresh token");
            throw new UnauthorizedException("Invalid refresh token");
        }

        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            log.error("[Account] [Service] refresh Error: Account not found");
            throw new NotFoundException("Account not found");
        }

        // Generate new tokens
        String newAccess = jwtProvider.generateAccessToken(account);
        String newRefresh = jwtProvider.generateRefreshToken(account);

        AccountDTO.RefreshTokenResponseDTO response = new AccountDTO.RefreshTokenResponseDTO();
        response.AccessToken = newAccess;
        response.RefreshToken = newRefresh;

        log.info("[Account] [Service] refresh End");
        log.info("Output: RefreshTokenResponseDTO " + response);

        return response;
    }

}
