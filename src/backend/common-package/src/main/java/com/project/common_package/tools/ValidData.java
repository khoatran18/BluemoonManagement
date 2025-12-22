package com.project.common_package.tools;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.*;
import java.time.Duration;

/**
 * ValidData - Phiên bản đầy đủ logic tạo/check dữ liệu
 * Đã sửa lỗi: bọc field data, định dạng ngày tháng (fix 400), tên trường theo API Doc.
 */
public class ValidData {

    private static final String BASE_URL = "http://localhost:8080";
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static void main(String[] args) throws Exception {

        // =========================================================
        // 1. Fee Categories (POST + GET)
        // =========================================================
        System.out.println("Testing Fee Categories...");
        for (int i = 1; i <= 3; i++) {
            post("/api/v1/fee-categories", """
                {
                  "fee_type_id": 1,
                  "name": "Danh mục %d",
                  "description": "Mô tả danh mục %d"
                }
            """.formatted(i, i));
        }

        JSONObject feeCategoriesData = getData("/api/v1/fee-categories");
        assertInt(feeCategoriesData, "page");
        assertInt(feeCategoriesData, "limit");
        assertInt(feeCategoriesData, "total_items");

        // Tên trường trong API Doc là "fee-categories" (có gạch ngang)
        JSONArray feeCategories = assertArray(feeCategoriesData, "fee-categories");
        JSONObject fc = feeCategories.getJSONObject(0);
        assertLong(fc, "fee_category_id");
        assertString(fc, "name");
        assertString(fc, "fee_type_name");

        // =========================================================
        // 2. Apartments (POST + GET)
        // =========================================================
        System.out.println("Testing Apartments...");
        for (int i = 1; i <= 3; i++) {
            post("/api/v1/apartments", """
                {
                  "building": "A",
                  "room_number": "10%d"
                }
            """.formatted(i));
        }

        JSONObject apartmentsData = getData("/api/v1/apartments");
        assertPagination(apartmentsData);
        JSONArray apartments = assertArray(apartmentsData, "items");
        JSONObject apt = apartments.getJSONObject(0);
        assertLong(apt, "apartment_id");
        assertString(apt, "building");
        assertString(apt, "room_number");

        // =========================================================
        // 3. Residents (POST)
        // =========================================================
        System.out.println("Testing Residents...");
        int residentIdCounter = 1;
        for (int aptId = 1; aptId <= 3; aptId++) {
            for (int j = 0; j < 2; j++) {
                post("/api/v1/residents", """
                    {
                      "full_name": "Resident %d",
                      "email": "res%d@example.com",
                      "phone_number": "0900000%d",
                      "apartment_id": %d
                    }
                """.formatted(residentIdCounter, residentIdCounter, residentIdCounter, aptId));
                residentIdCounter++;
            }
        }

        // =========================================================
        // 4. Update Apartment (Head Resident & Resident List)
        // =========================================================
        System.out.println("Testing Update Apartment...");
        int rId = 1;
        for (int aptId = 1; aptId <= 3; aptId++) {
            put("/api/v1/apartments/" + aptId, """
                {
                  "apartment_id": %d,
                  "building": "A",
                  "room_number": "10%d",
                  "head_resident_id": %d,
                  "residents": [
                    {"id": %d},
                    {"id": %d}
                  ]
                }
            """.formatted(aptId, aptId, rId, rId, rId + 1));
            rId += 2;
        }

        JSONObject apartmentDetail = getData("/api/v1/apartments/1");
        assertLong(apartmentDetail, "apartment_id");
        assertString(apartmentDetail, "building");

        JSONObject head = apartmentDetail.optJSONObject("head_resident");
        if (head != null) {
            assertLong(head, "id");
            assertString(head, "full_name");
        }

        JSONArray resList = assertArray(apartmentDetail, "residents");
        for (int i = 0; i < resList.length(); i++) {
            JSONObject res = resList.getJSONObject(i);
            assertLong(res, "resident_id");
            assertString(res, "full_name");
        }

        // =========================================================
        // 5. Fees (POST + GET) - FIXED 400
        // =========================================================
        System.out.println("Testing Fees...");
        for (int i = 1; i <= 3; i++) {
            // applicable_month: max 4 chars (Entity Fee.java)
            // effective_date/expiry_date: yyyy-MM-dd (LocalDateMapper)
            post("/api/v1/fees", """
                {
                  "fee_type_id": 1,
                  "fee_category_id": 1,
                  "fee_name": "Phí tháng %d/2025",
                  "fee_description": "Mô tả phí dịch vụ",
                  "fee_amount": 450000,
                  "applicable_month": "2025",
                  "effective_date": "2025-10-0%d",
                  "expiry_date": "2025-10-28",
                  "status": "ACTIVE"
                }
            """.formatted(i, i));
        }

        JSONObject feesData = getData("/api/v1/fees");
        assertPagination(feesData);
        JSONArray fees = assertArray(feesData, "fees");
        JSONObject fee = fees.getJSONObject(0);
        assertLong(fee, "fee_id");
        assertString(fee, "fee_name");
        assertNumber(fee, "fee_amount");
        assertString(fee, "status");

        // =========================================================
        // 6. Apartment Fee Status (GET + PUT)
        // =========================================================
        System.out.println("Testing Fee Status...");
        put("/api/v1/apartment-fee-statuses/1", """
            {
              "total_paid": 450000,
              "balance": 450000,
              "paid_fees": [{"fee_id": 1}],
              "unpaid_fees": [{"fee_id": 2}]
            }
        """);

        JSONObject afs = getData("/api/v1/apartment-fee-statuses/1");
        assertLong(afs, "apartment_id");
        assertNumber(afs, "total_paid");
        assertNumber(afs, "balance");

//        JSONArray paidFees = assertArray(afs, "paid_fees");
//        if (paidFees.length() > 0) assertLong(paidFees.getJSONObject(0), "fee_id");

        JSONArray unpaidFees = assertArray(afs, "unpaid_fees");
        if (unpaidFees.length() > 0) assertLong(unpaidFees.getJSONObject(0), "fee_id");

        // =========================================================
        // 7. Adjustments (POST + GET)
        // =========================================================
        System.out.println("Testing Adjustments...");
        post("/api/v1/adjustments", """
            {
              "fee_id": 1,
              "adjustment_amount": 20000,
              "adjustment_type": "decrease",
              "reason": "Giảm trừ đặc biệt",
              "effective_date": "2025-10-15", 
              "expiry_date": "2025-10-15"
            }
        """);

        JSONObject adjData = getData("/api/v1/adjustments");
        assertPagination(adjData);
        JSONArray adjs = assertArray(adjData, "adjustments");
        JSONObject adj = adjs.getJSONObject(0);
        assertLong(adj, "adjustment_id");
        assertString(adj, "adjustment_type");
        assertNumber(adj, "adjustment_amount");

        System.out.println("\n>>> ALL API CONTRACT TESTS PASSED SUCCESSFULLY");
    }

    // =========================================================
    // HTTP HELPERS (Bọc data field theo API Doc)
    // =========================================================

    private static void post(String path, String json) throws Exception {
        send("POST", path, json);
    }

    private static void put(String path, String json) throws Exception {
        send("PUT", path, json);
    }

    private static void send(String method, String path, String json) throws Exception {
        HttpRequest.Builder b = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer mock-token");

        if ("POST".equals(method)) b.POST(HttpRequest.BodyPublishers.ofString(json));
        else b.PUT(HttpRequest.BodyPublishers.ofString(json));

        HttpResponse<String> res = CLIENT.send(b.build(), HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() >= 400) {
            System.err.println("Fail details: " + res.body());
            throw new RuntimeException(method + " " + path + " failed with status " + res.statusCode());
        }
    }

    private static JSONObject getData(String path) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .GET()
                .build();

        HttpResponse<String> res = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
        JSONObject root = new JSONObject(res.body());

        if (root.getInt("code") != 200) {
            throw new RuntimeException("GET " + path + " failed: " + root.optString("message"));
        }

        return root.getJSONObject("data");
    }

    // =========================================================
    // ASSERT HELPERS
    // =========================================================

    private static void assertPagination(JSONObject o) {
        assertInt(o, "page");
        assertInt(o, "limit");
        assertInt(o, "total_items");
    }

    private static void assertString(JSONObject o, String k) {
        if (!o.has(k) || o.isNull(k) || !(o.get(k) instanceof String))
            throw new RuntimeException("Invalid/Missing String field: " + k);
    }

    private static void assertInt(JSONObject o, String k) {
        if (!o.has(k) || o.isNull(k) || !(o.get(k) instanceof Number))
            throw new RuntimeException("Invalid/Missing Number field: " + k);
    }

    private static void assertLong(JSONObject o, String k) {
        assertInt(o, k);
    }

    private static void assertNumber(JSONObject o, String k) {
        assertInt(o, k);
    }

    private static JSONArray assertArray(JSONObject o, String k) {
        if (!o.has(k) || o.isNull(k) || !(o.get(k) instanceof JSONArray))
            throw new RuntimeException("Missing or invalid array: " + k);
        return o.getJSONArray(k);
    }
}