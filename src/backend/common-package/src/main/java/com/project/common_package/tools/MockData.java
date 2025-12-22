package com.project.common_package.tools;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;

public class MockData {

    private static final String BASE_URL = "http://localhost:8080";
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    public static void main(String[] args) throws Exception {

        // =========================================================
        // 1. Fee Types (fixed, giả sử đã có hoặc seed riêng)
        // =========================================================
        // KHÔNG POST ở đây nếu hệ thống đã seed sẵn

        // =========================================================
        // 2. Fee Categories
        // =========================================================
        for (int i = 1; i <= 3; i++) {
            String body = "{" +
                    "\"fee_type_id\":1," +
                    "\"name\":\"Danh mục " + i + "\"," +
                    "\"description\":\"Mô tả danh mục " + i + "\"" +
                    "}";
            post("/api/v1/fee-categories", body);
        }

        // =========================================================
        // 3. Apartments (POST – chỉ building, room_number)
        // =========================================================
        for (int i = 1; i <= 5; i++) {
            String body = "{" +
                    "\"building\":\"A\"," +
                    "\"room_number\":\"10" + i + "\"" +
                    "}";
            post("/api/v1/apartments", body);
        }

        // =========================================================
        // 4. Residents (POST – không is_head)
        // =========================================================
        int residentId = 1;
        for (int apt = 1; apt <= 5; apt++) {

            for (int j = 0; j < 2; j++) {
                String body = "{" +
                        "\"full_name\":\"Resident " + residentId + "\"," +
                        "\"email\":\"r" + residentId + "@test.com\"," +
                        "\"phone_number\":\"0900000" + residentId + "\"," +
                        "\"apartment_id\":" + apt +
                        "}";
                post("/api/v1/residents", body);
                residentId++;
            }
        }

        // =========================================================
        // 5. UPDATE Apartment (set head_resident + residents)
        // =========================================================
        int currentResident = 1;
        for (int apt = 1; apt <= 5; apt++) {

            String body = "{" +
                    "\"apartment_id\":" + apt + "," +
                    "\"building\":\"A\"," +
                    "\"room_number\":\"10" + apt + "\"," +
                    "\"head_resident_id\":" + currentResident + "," +
                    "\"residents\":[" +
                    "{\"id\":" + currentResident + "}," +
                    "{\"id\":" + (currentResident + 1) + "}" +
                    "]" +
                    "}";

            put("/api/v1/apartments/" + apt, body);
            currentResident += 2;
        }

        // =========================================================
        // 6. Fees (POST /fees)
        // =========================================================
        for (int i = 1; i <= 3; i++) {
            String body = "{" +
                    "\"fee_type_id\":1," +
                    "\"fee_category_id\":1," +
                    "\"fee_name\":\"Phí điện tháng " + i + "/2025\"," +
                    "\"fee_description\":\"Mô tả phí\"," +
                    "\"fee_amount\":400000," +
                    "\"applicable_month\":\"2025\"," +
                    "\"effective_date\":\"2025-0" + i + "-01\"," +
                    "\"expiry_date\":\"2025-0" + i + "-28\"," +
                    "\"status\":\"Active\"" +
                    "}";
            post("/api/v1/fees", body);
        }

        // =========================================================
        // 7. Adjustments (global + apartment-specific)
        // =========================================================

        // 7.1 Global adjustments (fee_id > 0)
        for (int i = 1; i <= 2; i++) {
            String body = "{" +
                    "\"fee_id\":" + i + "," +
                    "\"adjustment_amount\":50000," +
                    "\"adjustment_type\":\"decrease\"," +
                    "\"reason\":\"Giảm phí chung\"," +
                    "\"effective_date\":\"2025-10-01\"," +
                    "\"expiry_date\":\"2025-10-31\"" +
                    "}";
            post("/api/v1/adjustments", body);
        }

        // 7.2 Apartment-specific adjustments (fee_id = -1)
        for (int i = 1; i <= 5; i++) {
            String body = "{" +
                    "\"fee_id\":-1," +
                    "\"adjustment_amount\":30000," +
                    "\"adjustment_type\":\"decrease\"," +
                    "\"reason\":\"Gia đình chính sách\"," +
                    "\"effective_date\":\"2025-11-01\"," +
                    "\"expiry_date\":\"2025-12-31\"" +
                    "}";
            post("/api/v1/adjustments", body);
        }

        // =========================================================
        // 8. Assign apartment-specific adjustments
        // =========================================================
        for (int apt = 1; apt <= 5; apt++) {
            String body = "{\"adjustment_ids\":[" + apt + "]}";
            put("/api/v1/apartments/apartment_specific_adjustments/" + apt, body);
        }

        // =========================================================
        // 9. UPDATE ApartmentFeeStatus (PUT only)
        // =========================================================
        for (int apt = 1; apt <= 5; apt++) {
            String body = "{" +
                    "\"total_paid\":200000," +
                    "\"balance\":200000," +
                    "\"paid_fees\":[{\"fee_id\":1}]," +
                    "\"unpaid_fees\":[{\"fee_id\":2}]" +
                    "}";
            put("/api/v1/apartment-fee-statuses/" + apt, body);
        }

        System.out.println("MOCK DATA DONE");
    }

    // ================= HTTP HELPERS =================

    private static void post(String path, String json) throws IOException, InterruptedException {
        send("POST", path, json);
    }

    private static void put(String path, String json) throws IOException, InterruptedException {
        send("PUT", path, json);
    }

    private static void send(String method, String path, String json)
            throws IOException, InterruptedException {

        HttpRequest.Builder b = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json");

        if ("POST".equals(method))
            b.POST(HttpRequest.BodyPublishers.ofString(json));
        else
            b.PUT(HttpRequest.BodyPublishers.ofString(json));

        HttpResponse<String> res =
                CLIENT.send(b.build(), HttpResponse.BodyHandlers.ofString());

        System.out.println(method + " " + path + " -> " + res.statusCode());
    }
}
