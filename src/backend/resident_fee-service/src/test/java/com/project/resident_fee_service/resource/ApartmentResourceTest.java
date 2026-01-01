package com.project.resident_fee_service.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class ApartmentResourceTest {

    /**
     * Helper: Tạo căn hộ -> Tìm kiếm lại trong danh sách để lấy ID
     * Vì API POST không trả về ID trực tiếp.
     */
    private Long createAndFindId(String building, String room) {
        given()
                .contentType("application/json")
                .body(String.format("{\"building\": \"%s\", \"room_number\": \"%s\"}", building, room))
                .post("/api/v1/apartments")
                .then()
                .statusCode(201);

        Response response = given()
                .queryParam("building", building)
                .queryParam("room_number", room)
                .get("/api/v1/apartments")
                .then()
                .statusCode(200)
                .extract().response();

        return response.jsonPath().getLong("data.items.find { it.room_number == '" + room + "' }.apartment_id");
    }

    @Test
    void testCreateApartment() {
        given()
                .contentType("application/json")
                .body("{\"building\": \"A\", \"room_number\": \"888\"}")
                .when()
                .post("/api/v1/apartments")
                .then()
                .statusCode(201)
                .body("success", is(true))
                .body("code", is(201));
    }

    @Test
    void testGetAllApartments() {
        // Đảm bảo có ít nhất 1 căn hộ trong DB để test list
        createAndFindId("LIST", "999");

        given()
                .queryParam("page", 1)
                .queryParam("limit", 10)
                .when()
                .get("/api/v1/apartments")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("data.items", is(not(empty()))) // Kiểm tra mảng items không rỗng
                .body("data.total_items", greaterThanOrEqualTo(1)) // Kiểm tra tổng số lượng
                .body("data.page", is(1));
    }

    @Test
    void testGetApartmentById() {
        Long id = createAndFindId("GET", "777");
        given()
                .when()
                .get("/api/v1/apartments/" + id)
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("data.apartment_id", is(id.intValue()));
    }

    @Test
    void testUpdateApartment() {
        Long id = createAndFindId("UPDATE", "666");
        String updateBody = String.format(
                "{\"apartment_id\": %d, \"building\": \"UP\", \"room_number\": \"666-B\"}", id
        );

        given()
                .contentType("application/json")
                .body(updateBody)
                .when()
                .put("/api/v1/apartments/" + id)
                .then()
                .statusCode(200)
                .body("success", is(true));
    }

    @Test
    void testDeleteApartment() {
        Long id = createAndFindId("DELETE", "555");
        given()
                .when()
                .delete("/api/v1/apartments/" + id)
                .then()
                .statusCode(200)
                .body("success", is(true));
    }
}