package api;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Feature("Создание пользователя")
public class CreateUserTest extends BaseTest {

    private User user;
    private String accessToken;

    @Before
    @Step("Создание тестовых данных для пользователя")
    public void setUpData() {
        user = new User("testuser_" + System.currentTimeMillis() + "@yandex.ru", "password123", "TestUser");
    }

    @After
    @Step("Очистка тестовых данных: удаление пользователя")
    public void cleanUp() {
        if (accessToken != null) {
            String cleanToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
            given()
                    .header("Authorization", "Bearer " + cleanToken)
                    .when()
                    .delete("/api/auth/user");
        }
    }

    @Test
    @DisplayName("Создание уникального пользователя - проверка статуса 200")
    public void createUniqueUserShouldReturn200Test() {
        Response response = given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post("/api/auth/register");

        response.then().statusCode(200);
        accessToken = response.jsonPath().getString("accessToken");
    }

    @Test
    @DisplayName("Создание уникального пользователя - проверка поля success")
    public void createUniqueUserShouldReturnSuccessTrueTest() {
        Response response = given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post("/api/auth/register");

        response.then().body("success", equalTo(true));
        accessToken = response.jsonPath().getString("accessToken");
    }

    @Test
    @DisplayName("Создание уникального пользователя - проверка email")
    public void createUniqueUserShouldReturnCorrectEmailTest() {
        Response response = given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post("/api/auth/register");

        response.then().body("user.email", equalTo(user.getEmail().toLowerCase()));
        accessToken = response.jsonPath().getString("accessToken");
    }

    @Test
    @DisplayName("Создание уникального пользователя - проверка имени")
    public void createUniqueUserShouldReturnCorrectNameTest() {
        Response response = given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post("/api/auth/register");

        response.then().body("user.name", equalTo(user.getName()));
        accessToken = response.jsonPath().getString("accessToken");
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован - проверка статуса 403")
    public void createExistingUserShouldReturn403Test() {
        given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post("/api/auth/register");

        Response response = given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post("/api/auth/register");

        response.then().statusCode(403);
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован - проверка поля success")
    public void createExistingUserShouldReturnSuccessFalseTest() {
        given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post("/api/auth/register");

        Response response = given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post("/api/auth/register");

        response.then().body("success", equalTo(false));
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован - проверка сообщения")
    public void createExistingUserShouldReturnErrorMessageTest() {
        given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post("/api/auth/register");

        Response response = given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post("/api/auth/register");

        response.then().body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без email - проверка статуса 403")
    public void createUserWithoutEmailShouldReturn403Test() {
        User userWithoutEmail = new User(null, "password123", "TestUser");

        Response response = given()
                .header("Content-type", "application/json")
                .body(userWithoutEmail)
                .when()
                .post("/api/auth/register");

        response.then().statusCode(403);
    }

    @Test
    @DisplayName("Создание пользователя без email - проверка поля success")
    public void createUserWithoutEmailShouldReturnSuccessFalseTest() {
        User userWithoutEmail = new User(null, "password123", "TestUser");

        Response response = given()
                .header("Content-type", "application/json")
                .body(userWithoutEmail)
                .when()
                .post("/api/auth/register");

        response.then().body("success", equalTo(false));
    }

    @Test
    @DisplayName("Создание пользователя без email - проверка сообщения")
    public void createUserWithoutEmailShouldReturnErrorMessageTest() {
        User userWithoutEmail = new User(null, "password123", "TestUser");

        Response response = given()
                .header("Content-type", "application/json")
                .body(userWithoutEmail)
                .when()
                .post("/api/auth/register");

        response.then().body("message", equalTo("Email, password and name are required fields"));
    }
}