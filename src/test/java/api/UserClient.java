package api;

import io.restassured.response.Response;
import models.Credentials;
import models.User;

import static io.restassured.RestAssured.given;

public class UserClient {
    private static final String BASE_URL = "https://stellarburgers.education-services.ru";

    public Response register(User user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(BASE_URL + "/api/auth/register");
    }

    public Response login(Credentials credentials) {
        return given()
                .header("Content-type", "application/json")
                .body(credentials)
                .when()
                .post(BASE_URL + "/api/auth/login");
    }

    public Response deleteUser(String accessToken) {
        String cleanToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        return given()
                .header("Authorization", "Bearer " + cleanToken)
                .when()
                .delete(BASE_URL + "/api/auth/user");
    }
}