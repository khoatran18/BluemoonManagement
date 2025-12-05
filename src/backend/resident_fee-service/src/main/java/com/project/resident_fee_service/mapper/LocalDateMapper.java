package com.project.resident_fee_service.mapper;

import com.project.common_package.exception.ConflictException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static LocalDate StringToLocalDate(String date){
        if (date == null || date.isBlank()){
            return null;
        }

        try {
            return LocalDate.parse(date, FORMATTER);
        } catch (DateTimeParseException e) {
            throw new ConflictException(e.getMessage());
        }
    }

    public static String LocalDateToString(LocalDate localDate){
        if (localDate == null) {
            return null;
        }

        try {
            return FORMATTER.format(localDate);
        } catch (DateTimeParseException e) {
            throw new ConflictException(e.getMessage());
        }
    }

}
