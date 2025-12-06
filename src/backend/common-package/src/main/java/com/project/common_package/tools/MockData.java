package com.project.common_package.tools;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;

public class MockData {

    private static final String BASE_URL = "http://localhost:8080";
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    public static void main(String[] args) throws Exception {

        // -----------------------------
        // 1. Fee Types (3 fixed)
        // -----------------------------
//        post("/api/v1/fee-types", "{\"name\":\"Định kỳ\"}");
//        post("/api/v1/fee-types", "{\"name\":\"Đột xuất\"}");
//        post("/api/v1/fee-types", "{\"name\":\"Tự nguyện\"}");

        // -----------------------------
        // 2. Fee Categories (10 categories)
        // -----------------------------
        for (int i = 1; i <= 10; i++) {
            String body = "{" +
                    "\"fee_type_id\":1," +
                    "\"name\":\"Danh mục " + i + "\"," +
                    "\"description\":\"Mô tả danh mục " + i + "\"" +
                    "}";
            post("/api/v1/fee-categories", body);
        }

        // -----------------------------
        // 3. Apartments (10 apartments)
        // -----------------------------
        for (int i = 1; i <= 10; i++) {
            String room = "10" + i;
            String body = "{\"building\":\"A\",\"room_number\":\"" + room + "\"}";
            post("/api/v1/apartments", body);
        }

        // -----------------------------
        // 4. Residents (20 – 2 each apartment)
        // -----------------------------
        int residentId = 1;
        for (int apt = 1; apt <= 10; apt++) {
            String r1 = "{" +
                    "\"full_name\":\"Resident " + residentId + "\"," +
                    "\"email\":\"r" + residentId + "@test.com\"," +
                    "\"phone_number\":\"0900000" + residentId + "\"," +
                    "\"apartment_id\":" + apt + "," +
                    "\"is_head\":true" +
                    "}";
            post("/api/v1/residents", r1);
            residentId++;

            String r2 = "{" +
                    "\"full_name\":\"Resident " + residentId + "\"," +
                    "\"email\":\"r" + residentId + "@test.com\"," +
                    "\"phone_number\":\"0900000" + residentId + "\"," +
                    "\"apartment_id\":" + apt + "," +
                    "\"is_head\":false" +
                    "}";
            post("/api/v1/residents", r2);
            residentId++;
        }

        // -----------------------------
        // 5. Fees (10 fees)
        // -----------------------------
        for (int i = 1; i <= 9; i++) {
            String body = "{" +
                    "\"fee_type_id\":1," +
                    "\"fee_category_id\":1," +
                    "\"fee_name\":\"Fee name" + i + "/2025\"," +
                    "\"fee_amount\":" + (100000 + i * 10000) + "," +
                    "\"fee_description\":\"Mô tả fee " + i + "\"," +
                    "\"applicable_month\":\"2025\"," +  // phải 4 ký tự
                    "\"effective_date\":\"2025-0" + i + "-01\"," +
                    "\"expiry_date\":\"2025-0" + i + "-28\"," +
                    "\"status\":\"ACTIVE\"" +           // enum chuẩn
                    "}";
            post("/api/v1/fees", body);
        }


        // -----------------------------
        // 6. Adjustments (10 adjustments)
        // -----------------------------
        for (int i = 1; i <= 5; i++) {
            // global adjustments (fee_id = i)
            String body = "{" +
                    "\"fee_id\":" + i + "," +
                    "\"adjustment_amount\":5000," +
                    "\"adjustment_type\":\"decrease\"," +
                    "\"reason\":\"Điều chỉnh chung " + i + "\"," +
                    "\"effective_date\":\"2025-10-15\"," +
                    "\"expiry_date\":\"2025-10-30\"" +
                    "}";
            post("/api/v1/adjustments", body);
        }

        for (int i = 6; i <= 10; i++) {
            // apartment-specific (fee_id = -1)
            String body = "{" +
                    "\"fee_id\":-1," +
                    "\"adjustment_amount\":3000," +
                    "\"adjustment_type\":\"decrease\"," +
                    "\"reason\":\"Điều chỉnh riêng " + i + "\"," +
                    "\"effective_date\":\"2025-11-01\"," +
                    "\"expiry_date\":\"2025-12-31\"" +
                    "}";
            post("/api/v1/adjustments", body);
        }

        // -----------------------------
        // 7. Assign adjustments to each apartment (IDs 1..10)
        // -----------------------------
        for (int apt = 1; apt <= 10; apt++) {
            int a1 = apt;
            int a2 = Math.min(apt + 1, 10);   // tránh tạo id 11

            String body = "{\"adjustment_ids\": [" + a1 + ", " + a2 + "]}";
            put("/api/v1/apartments/apartment_specific_adjustments/" + apt, body);
        }

        // -----------------------------
        // 8. Apartment Fee Status
        // -----------------------------

        for (int apt = 1; apt <= 10; apt++) {

//            // 8.1 – CREATE initial apartment fee status (KHÔNG ĐỘNG VÀO LOGIC CŨ)
//            String createBody = "{\"apartment_id\":" + apt + "}";
//            post("/api/v1/apartment-fee-statuses", createBody);

            // 8.2 – UPDATE fee status (giữ nguyên như bạn viết)
            String updateBody = "{" +
                    "\"total_paid\":50000," +
                    "\"balance\":150000," +
                    "\"paid_fees\":[{\"fee_id\":1}], " +
                    "\"unpaid_fees\":[{\"fee_id\":2}]" +
                    "}";
            put("/api/v1/apartment-fee-statuses/" + apt, updateBody);
        }

        System.out.println("DONE");
    }

    private static void post(String path, String json) throws IOException, InterruptedException {
        send("POST", path, json);
    }

    private static void put(String path, String json) throws IOException, InterruptedException {
        send("PUT", path, json);
    }

    private static void send(String method, String path, String json) throws IOException, InterruptedException {
        HttpRequest.Builder b = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json");

        if (method.equals("POST"))
            b.POST(HttpRequest.BodyPublishers.ofString(json));
        else if (method.equals("PUT"))
            b.PUT(HttpRequest.BodyPublishers.ofString(json));

        HttpResponse<String> res = CLIENT.send(b.build(), HttpResponse.BodyHandlers.ofString());
        System.out.println(method + " " + path + " -> " + res.statusCode());
        System.out.println(res.body());
    }
}
