package com.project.resident_fee_service.mapper;

import com.project.resident_fee_service.dto.AccountDTO;
import com.project.resident_fee_service.entity.Account;

public class AccountMapper {

    ///////////////////////////// Register /////////////////////////////

    public Account RegisterRequestDTOToEntity(AccountDTO.RegisterRequestDTO dto) {
        Account entity = new Account();
        entity.setUsername(dto.Username);
        entity.setPassword(dto.Password);
        entity.setEmail(dto.Email);
        entity.setIdentityNumber(dto.IdentityNumber);
        entity.setRole(dto.Role);
        return entity;
    }

    ///////////////////////////// Login /////////////////////////////

    public AccountDTO.LoginResponseDTO EntityToLoginResponseDTO(
            Account entity,
            String accessToken,
            String refreshToken) {
        AccountDTO.LoginResponseDTO dto = new AccountDTO.LoginResponseDTO();

        dto.Username = entity.getUsername();
        dto.Email = entity.getEmail();
        dto.IdentityNumber = entity.getIdentityNumber();
        dto.Role = entity.getRole();
        dto.AccessToken = accessToken;
        dto.RefreshToken = refreshToken;

        return dto;
    }

    ///////////////////////////// GET ACCOUNT /////////////////////////////

    public AccountDTO.GetAccountResponseDTO EntityToGetAccountResponseDTO(Account entity) {
        AccountDTO.GetAccountResponseDTO dto = new AccountDTO.GetAccountResponseDTO();

        dto.AccountId = entity.getAccountId();
        dto.Username = entity.getUsername();
        dto.Email = entity.getEmail();
        dto.IdentityNumber = entity.getIdentityNumber();
        dto.Role = entity.getRole();

        if (entity.getResident() != null)
            dto.ResidentId = entity.getResident().getResidentId();
        else
            dto.ResidentId = null;

        return dto;
    }

    ///////////////////////////// ME /////////////////////////////

    public AccountDTO.MeResponseDTO EntityToMeResponseDTO(Account entity) {
        AccountDTO.MeResponseDTO dto = new AccountDTO.MeResponseDTO();

        dto.AccountId = entity.getAccountId();
        dto.Username = entity.getUsername();
        dto.Email = entity.getEmail();
        dto.IdentityNumber = entity.getIdentityNumber();
        dto.Role = entity.getRole();

        if (entity.getResident() != null) {
            dto.ResidentId = entity.getResident().getResidentId();
            if (entity.getResident().getApartment() != null) {
                dto.ApartmentId = entity.getResident().getApartment().getApartmentId();
            } else {
                dto.ApartmentId = null;
            }
        } else {
            dto.ResidentId = null;
            dto.ApartmentId = null;
        }

        return dto;
    }

}
