package com.project.common_package.tools;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.Random;

public class MockData {

    private static final String BASE_URL = "http://localhost:8080";
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final Random RANDOM = new Random();

    public static void main(String[] args) throws Exception {

        System.out.println("🚀 STARTING LARGE MOCK DATA GENERATION...");

        // 1. Fee Categories (Tạo 10 danh mục khác nhau)
        String[] catNames = {"Phí Quản Lý", "Phí Gửi Xe Máy", "Phí Gửi Ô Tô", "Phí Vệ Sinh", "Phí Bảo Trì", "Tiền Nước", "Internet", "Gym & Pool", "An Ninh", "Tiền rác"};
        for (int i = 0; i < catNames.length; i++) {
            String body = "{" +
                    "\"fee_type_id\":" + (i < 5 ? 1 : 2) + "," + // 5 cái đầu là bắt buộc, sau là tự nguyện
                    "\"name\":\"" + catNames[i] + "\"," +
                    "\"description\":\"Chi phí hàng tháng cho " + catNames[i] + "\"" +
                    "}";
            post("/api/v1/fee-categories", body);
        }

        // 2. Apartments (Tạo 50 căn hộ ở 2 tòa nhà A và B)
        for (int i = 1; i <= 50; i++) {
            String building = (i <= 25) ? "A" : "B";
            int floor = (i % 5 == 0) ? (i / 5) : (i / 5 + 1);
            String room = building + floor + (i < 10 ? "0" + i : i);
            String body = "{" +
                    "\"building\":\"" + building + "\"," +
                    "\"room_number\":\"" + room + "\"" +
                    "}";
            post("/api/v1/apartments", body);
        }

        // 3. Residents (150 cư dân - trung bình 3 người/căn)
        for (int i = 1; i <= 150; i++) {
            int aptId = (i % 50 == 0) ? 50 : (i % 50);
            boolean isHead = (i <= 50); // 50 người đầu tiên là chủ hộ
            String body = "{" +
                    "\"apartment_id\":" + aptId + "," +
                    "\"full_name\":\"Nguyễn Văn " + i + "\"," +
                    "\"phone_number\":\"0912345" + (100 + i) + "\"," +
                    "\"email\":\"user" + i + "@gmail.com\"," +
                    "\"is_head\":" + isHead +
                    "}";
            post("/api/v1/residents", body);
            Thread.sleep(100);
        }
        Thread.sleep(5000);

        // 4. Fees (Tạo 20 loại phí phát sinh trong các tháng khác nhau)
        String[] months = {"2025-06", "2025-07", "2025-08"};
        for (int i = 1; i <= 20; i++) {
            int catId = (i % 10 == 0) ? 10 : (i % 10);
            String month = months[i % 3];
            String status = (i < 15) ? "ACTIVE" : "CLOSED";
            String body = "{" +
                    "\"fee_type_id\":" + (catId <= 5 ? 1 : 2) + "," +
                    "\"fee_category_id\":" + catId + "," +
                    "\"fee_name\":\"" + catNames[catId-1] + " tháng " + month + "\"," +
                    "\"fee_description\":\"Thông báo phí định kỳ\"," +
                    "\"fee_amount\":" + (50000 + RANDOM.nextInt(200000)) + "," +
                    "\"applicable_month\":\"" + month + "\"," +
                    "\"effective_date\":\"" + month + "-01\"," +
                    "\"expiry_date\":\"" + month + "-28\"," +
                    "\"status\":\"" + status + "\"" +
                    "}";
            post("/api/v1/fees", body);
            Thread.sleep(200);
        }

        // 5. Adjustments (Tạo 30 chính sách giảm trừ/tăng thêm)
        for (int i = 1; i <= 30; i++) {
            int feeId = (i % 20 == 0) ? 20 : (i % 20);
            String type = (i % 3 == 0) ? "increase" : "decrease";
            String body = "{" +
                    "\"fee_id\":" + feeId + "," +
                    "\"adjustment_amount\":" + (10000 + RANDOM.nextInt(30000)) + "," +
                    "\"adjustment_type\":\"" + type + "\"," +
                    "\"reason\":\"Ưu đãi/Phụ phí đợt " + i + "\"," +
                    "\"effective_date\":\"2025-06-01\"," +
                    "\"expiry_date\":\"2025-12-31\"" +
                    "}";
            post("/api/v1/adjustments", body);
            Thread.sleep(200);
        }

        // 6. Apartment Fee Status (Cập nhật trạng thái cho 50 căn hộ)
        // Tạo sự khác biệt: một số căn đã trả hết, một số căn nợ
        for (int apt = 1; apt <= 50; apt++) {
            int paidFee = (apt % 3 == 0) ? 1 : 2; // Căn chia hết cho 3 thì trả ít hơn
            int unpaidFee = (apt % 3 == 0) ? 3 : 4;

            String body = "{" +
                    "\"total_paid\":" + (100000 * paidFee) + "," +
                    "\"balance\":" + (50000 * unpaidFee) + "," +
                    "\"paid_fees\":[{\"fee_id\":" + paidFee + "}, {\"fee_id\":" + (paidFee + 5) + "}]," +
                    "\"unpaid_fees\":[{\"fee_id\":" + unpaidFee + "}, {\"fee_id\":" + (unpaidFee + 1) + "}]," +
                    "\"adjustments\":[{\"adjustment_id\":" + (apt % 30 == 0 ? 30 : apt % 30) + "}]" +
                    "}";
            put("/api/v1/apartment-fee-statuses/" + apt, body);
            Thread.sleep(200);
        }

        System.out.println("✅ MOCK DATA GENERATION COMPLETED SUCCESSFULLY!");
    }

    // ================= HTTP HELPERS =================

    private static void post(String path, String json) throws IOException, InterruptedException {
        send("POST", path, json);
    }

    private static void put(String path, String json) throws IOException, InterruptedException {
        send("PUT", path, json);
    }

    private static void send(String method, String path, String json) throws IOException, InterruptedException {
        HttpRequest.Builder b = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json");

        if ("POST".equals(method)) b.POST(HttpRequest.BodyPublishers.ofString(json));
        else b.PUT(HttpRequest.BodyPublishers.ofString(json));

        HttpResponse<String> res = CLIENT.send(b.build(), HttpResponse.BodyHandlers.ofString());

        if (res.statusCode() >= 400) {
            System.err.println("❌ FAILED " + method + " " + path + " | Status: " + res.statusCode());
            System.err.println("Body: " + res.body());
        } else {
            System.out.println("✅ SUCCESS " + method + " " + path + " | Status: " + res.statusCode());
        }
    }
}