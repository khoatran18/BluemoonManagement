package com.project.common_package.tools;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MockData {

    private static final String BASE_URL = "http://localhost:8080";
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static String ADMIN_TOKEN = "";
    private static final Random RANDOM = new Random();

    private static final List<String> RESIDENT_NAME =
            List.of("Vũ Khuê",
                    "Khương Anh Tài",
                    "Nguyễn Văn Lợi",
                    "Trần Minh Khoa",
                    "Võ Hoàng Bảo",
                    "Võ Hữu Trí Dũng",
                    "Nguyễn Kiêm Khang",
                    "Phạm Thị Hoài Thu",
                    "Trần Văn Phong",
                    "Nguyễn Đình Vũ"
            );

    public static void main(String[] args) throws Exception {

        System.out.println("Checking if admin account existed and get ADMIN TOKEN");
        handleAuthentication();

        System.out.println("🚀 STARTING LARGE MOCK DATA GENERATION...");

        // 1. Fee Categories (Tạo 10 danh mục khác nhau)
        String[] catNames = {"Phí Quản Lý", "Phí Gửi Xe Máy", "Phí Gửi Ô Tô", "Phí Vệ Sinh", "Phí Bảo Trì", "Tiền Nước", "Internet", "Gym & Pool", "An Ninh", "Tiền rác"};
        for (int i = 0; i < catNames.length; i++) {
            String body = "{" +
                    "\"name\":\"" + catNames[i] + "\"," +
                    "\"description\":\"Chi phí hàng tháng cho " + catNames[i] + "\"" +
                    "}";
            post("/api/v1/fee-categories", body, i);
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
            post("/api/v1/apartments", body, i);
        }
        Thread.sleep(5000);

        // 3. Residents (150 cư dân - trung bình 3 người/căn)
        for (int i = 1; i <= 150; i++) {
            int aptId = (i % 50 == 0) ? 50 : (i % 50);
            boolean isHead = (i <= 50); // 50 người đầu tiên là chủ hộ

            String residentName = (i <= RESIDENT_NAME.size())
                    ? RESIDENT_NAME.get(i - 1)
                    : ("Nguyễn Văn " + i);
//            String residentName = "Nguyễn Văn " + i;
            String phone = String.format("091%07d", i);  // luôn đủ 10 số

            String body = """
                {
                  "apartment_id": %d,
                  "full_name": "%s",
                  "phone_number": "%s",
                  "email": "user%d@gmail.com",
                  "is_head": %b
                }
                """.formatted(
                            aptId,
                            residentName,
                            phone,
                            i,
                            isHead
                    );
            post("/api/v1/residents", body, i);
            Thread.sleep(50);
        }
        Thread.sleep(5000);

        // PUT Method for head resident
        for (int apt = 1; apt <= 50; apt++) {
            int headResidentId = (apt - 1) * 3 + 1;
            int resident2 = headResidentId + 1;
            int resident3 = headResidentId + 2;

            String building = (apt <= 25) ? "A" : "B";
            int floor = (apt % 5 == 0) ? (apt / 5) : (apt / 5 + 1);
            String room = building + floor + (apt < 10 ? "0" + apt : apt);

            String body = "{" +
                    "\"apartment_id\":" + apt + "," +
                    "\"building\":\"" + building + "\"," +
                    "\"room_number\":\"" + room + "\"," +
                    "\"head_resident_id\":" + headResidentId + "," +
                    "\"residents\":[" +
                    "{\"id\":" + headResidentId + "}," +
                    "{\"id\":" + resident2 + "}," +
                    "{\"id\":" + resident3 + "}" +
                    "]" +
                    "}";
            put("/api/v1/apartments/" + apt, body, apt);
            Thread.sleep(50);
        }

        Thread.sleep(5000);

        // 4. Fees (Tạo 45 loại phí phát sinh trong các tháng khác nhau)
        String[] months = {"2025-12", "2026-01", "2026-02"};
        for (int i = 1; i <= 45; i++) {
            int catId = (i % 10 == 0) ? 10 : (i % 10);
            String month = months[i % 3];
            String status = (i % 4 != 0) ? "ACTIVE" : "CLOSED";
            String body = "{" +
                    "\"fee_type_id\":" + (catId <= 5 ? 1 : 2) + "," +
                    "\"fee_category_id\":" + catId + "," +
                    "\"fee_name\":\"" + catNames[catId-1] + " tháng " + month + "\"," +
                    "\"fee_description\":\"Thông báo phí định kỳ\"," +
                    "\"fee_amount\":" + (50000 + RANDOM.nextInt(200000)) + "," +
                    "\"applicable_month\":\"" + month + "\"," +
                    "\"effective_date\":\"" + months[0] + "-01\"," +
                    "\"expiry_date\":\"" + months[2] + "-28\"," +
                    "\"status\":\"" + status + "\"" +
                    "}";
            post("/api/v1/fees", body, i);
            Thread.sleep(100);
        }
        Thread.sleep(5000);

        // 5. Adjustments (Tạo 30 chính sách giảm trừ/tăng thêm)
        for (int i = 1; i <= 30; i++) {
            int feeId = (i % 20 == 0) ? 20 : (i % 20);
            String type = (i % 3 == 0) ? "increase" : "decrease";
            String body = "{" +
                    "\"fee_id\":" + feeId + "," +
                    "\"adjustment_amount\":" + (10000 + RANDOM.nextInt(30000)) + "," +
                    "\"adjustment_type\":\"" + type + "\"," +
                    "\"reason\":\"Ưu đãi/Phụ phí đợt " + i + "\"," +
                    "\"effective_date\":\"2026-01-12\"," +
                    "\"expiry_date\":\"2026-02-25\"" +
                    "}";
            post("/api/v1/adjustments", body, i);
            Thread.sleep(50);
        }
        Thread.sleep(5000);

        // 6. Apartment Fee Status (Cập nhật trạng thái cho 50 căn hộ)
        // Tạo sự khác biệt: một số căn đã trả hết, một số căn nợ
        for (int apt = 1; apt <= 50; apt++) {
            int paidFee = (apt % 3 == 0) ? 1 : 2; // Căn chia hết cho 3 thì trả ít hơn
            int unpaidFee = (apt % 3 == 0) ? 3 : 4;

            double amountPerFee = 50000.0;
            String body = "{" +
                    "\"total_paid\":" + (5000 * paidFee) + "," +
                    "\"balance\":" + (25000 * unpaidFee) + "," +
                    "\"paid_fees\":[" +
                        "{\"fee_id\":" + paidFee + ", \"pay_amount\":" + amountPerFee + 5000 * paidFee + "}," +
                        "{\"fee_id\":" + (paidFee + 5) + ", \"pay_amount\":" + amountPerFee + 5000 * paidFee + "}," +
                        "{\"fee_id\":" + (paidFee + 10) + ", \"pay_amount\":" + amountPerFee + 5000 * paidFee + "}" +
                    "]," +
                    "\"unpaid_fees\":[]," +
                    "\"adjustments\":[{\"adjustment_id\":" + (apt % 30 == 0 ? 30 : apt % 30) + "}]" +
                    "}";
            put("/api/v1/apartment-fee-statuses/" + apt, body, apt);
            Thread.sleep(100);
        }

        // 7. Bổ sung vào phương thức main
        System.out.println("🔐 GENERATING ACCOUNTS...");

        // Tạo tài khoản cho mỗi Resident (Mật khẩu: 123456)
        for (int i = 1; i <= 150; i++) {
            String body = "{" +
                    "\"username\":\"resident_user_" + i + "\"," +
                    "\"password\":\"123456\"," +
                    "\"email\":\"user" + i + "@gmail.com\"," +
                    "\"identity_number\":\"" + String.format("%012d", 100000000000L + i) + "\"," +
                    "\"role\":\"Citizen\"" +
                    "}";
            post("/api/v1/auth_service/register", body, i);
        }

        // Tạo 5 tài khoản Admin (Mật khẩu: admin)
        for (int i = 1; i <= 5; i++) {
            String body = "{" +
                    "\"username\":\"admin_" + i + "\"," +
                    "\"password\":\"admin\"," +
                    "\"email\":null," +
                    "\"identity_number\":\"" + String.format("%012d", 200000000000L + i) + "\"," +
                    "\"role\":\"Admin\"" +
                    "}";
            post("/api/v1/auth_service/register", body, i);
        }

        // Tạo 5 tài khoản FeeCollector (Mật khẩu: feecollector)
        for (int i = 1; i <= 5; i++) {
            String body = "{" +
                    "\"username\":\"collector_" + i + "\"," +
                    "\"password\":\"feecollector\"," +
                    "\"email\":null," +
                    "\"identity_number\":\"" + String.format("%012d", 300000000000L + i) + "\"," +
                    "\"role\":\"FeeCollector\"" +
                    "}";
            post("/api/v1/auth_service/register", body, i);
        }


        System.out.println("✅ MOCK DATA GENERATION COMPLETED SUCCESSFULLY!");
    }

    // ================= HTTP HELPERS =================

    private static void post(String path, String json, int i) throws IOException, InterruptedException {
        send("POST", path, json, i);
    }

    private static void put(String path, String json, int i) throws IOException, InterruptedException {
        send("PUT", path, json, i);
    }

    private static void send(String method, String path, String json, int i) throws IOException, InterruptedException {
        HttpRequest.Builder b = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json");

        // Nếu đã có token thì đính kèm vào Header
        if (!ADMIN_TOKEN.isEmpty()) {
            b.header("Authorization", "Bearer " + ADMIN_TOKEN);
        }

        if ("POST".equals(method)) b.POST(HttpRequest.BodyPublishers.ofString(json));
        else b.PUT(HttpRequest.BodyPublishers.ofString(json));

        HttpResponse<String> res = CLIENT.send(b.build(), HttpResponse.BodyHandlers.ofString());

        if (res.statusCode() >= 400) {
            System.err.println("❌ FAILED " + method + " " + path + " | Status: " + res.statusCode() + " " + i);
            System.err.println("Body: " + res.body());
        } else {
            System.out.println("✅ SUCCESS " + method + " " + path + " | Status: " + res.statusCode() + " " + i);
        }
    }

    // Get Admin token
    private static void handleAuthentication() throws Exception {
        System.out.println("🔑 Authenticating admin_master...");
        String loginBody = "{\"username\":\"admin_master\", \"password\":\"admin\"}";

        // Thử login
        HttpResponse<String> loginRes = sendRaw("PUT", "/api/v1/auth_service/login", loginBody, null);

        if (loginRes.statusCode() != 200) {
            System.out.println("⚠️ Login failed. Registering admin_master...");
            String regBody = "{" +
                    "\"username\":\"admin_master\"," +
                    "\"password\":\"admin\"," +
                    "\"email\":null," +
                    "\"identity_number\":\"000000000000\"," +
                    "\"role\":\"Admin\"" +
                    "}";
            sendRaw("POST", "/api/v1/auth_service/register", regBody, null);

            // Login lại sau khi register
            loginRes = sendRaw("POST", "/api/v1/auth_service/login", loginBody, null);
        }

        ADMIN_TOKEN = extractToken(loginRes.body());
        System.out.println(ADMIN_TOKEN);
        System.out.println("🔑 Token acquired.");
    }
    private static String extractToken(String json) {
        Matcher m = Pattern.compile("\"access_token\":\"(.*?)\"").matcher(json);
        return m.find() ? m.group(1) : "";
    }
    private static HttpResponse<String> sendRaw(String method, String path, String json, String token) throws Exception {
        HttpRequest.Builder b = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json");

        if (token != null && !token.isEmpty()) b.header("Authorization", "Bearer " + token);

        if ("POST".equals(method)) b.POST(HttpRequest.BodyPublishers.ofString(json));
        else if ("PUT".equals(method)) b.PUT(HttpRequest.BodyPublishers.ofString(json));
        else b.GET();

        return CLIENT.send(b.build(), HttpResponse.BodyHandlers.ofString());
    }
}