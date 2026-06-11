package api;

import io.restassured.response.Response;
import models.Order;

import static io.restassured.RestAssured.given;

public class OrderClient {
    private static final String BASE_URL = "https://stellarburgers.education-services.ru";

    public Response createOrder(Order order, String accessToken) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .body(order)
                .when()
                .post(BASE_URL + "/api/orders");
    }
}
