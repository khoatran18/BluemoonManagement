package com.project.service;

import com.project.entity.Fee;
import com.project.exception.NotFoundException;
import com.project.repository.FeeRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static io.restassured.RestAssured.when;
import static org.gradle.internal.impldep.com.google.common.base.Verify.verify;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
public class FeeServiceTest {

    @InjectMock
    FeeRepository repository; // Mock repository

    @Inject
    FeeService service; // Service thật

    @Test
    void testGetFeeById_NotFound() {
        // Giả lập: không tìm thấy fee
        when(repository.findById(1L)).thenReturn(null);

        // Kiểm tra ném đúng exception
        assertThrows(NotFoundException.class, () -> service.getFeeById(1L));

        // Xác nhận repository.findById đã được gọi đúng 1 lần
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void testCalculateFeeBalance_Logic() {
        // Giả lập 1 Fee trong DB
        Fee fee = new Fee();
        fee.setId(2L);
        fee.setFeeName("Internet");
        fee.setFeeAmount(new BigDecimal("200000"));
        fee.setEffectiveDate(LocalDate.now());

        // Giả lập trả về Fee thật
        when(repository.findById(2L)).thenReturn(fee);

        // Giả lập nghiệp vụ (ví dụ logic bạn thêm trong FeeService)
        BigDecimal result = service.calculateFeeBalance(2L, new BigDecimal("150000"));

        assertEquals(new BigDecimal("50000"), result); // 200000 - 150000 = 50000
        verify(repository, times(1)).findById(2L);
    }
}
