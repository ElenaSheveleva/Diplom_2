package api;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.Credentials;
import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Feature("Логин пользователя")
public class LoginUserTest extends BaseTest {

    private User user;
    private String accessToken;

    @Before
    @Step("Создание тестового пользователя для логина")
    public void setUpData() {
        user = new User("loginuser_" + System.currentTimeMillis() + "@yandex.ru", "password123", "LoginUser");
        Response registerResponse = given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post("/api/auth/register");

        accessToken = registerResponse.jsonPath().getString("accessToken");
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
    @DisplayName("Вход под существующим пользователем - проверка статуса 200")
    public void loginExistingUserShouldReturn200Test() {
        Credentials credentials = new Credentials(user.getEmail(), user.getPassword());

        given()
                .header("Content-type", "application/json")
                .body(credentials)
                .when()
                .post("/api/auth/login")
                .then().statusCode(200);
    }

    @Test
    @DisplayName("Вход под существующим пользователем - проверка поля success")
    public void loginExistingUserShouldReturnSuccessTrueTest() {
        Credentials credentials = new Credentials(user.getEmail(), user.getPassword());

        given()
                .header("Content-type", "application/json")
                .body(credentials)
                .when()
                .post("/api/auth/login")
                .then().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Вход под существующим пользователем - проверка email")
    public void loginExistingUserShouldReturnCorrectEmailTest() {
        Credentials credentials = new Credentials(user.getEmail(), user.getPassword());

        given()
                .header("Content-type", "application/json")
                .body(credentials)
                .when()
                .post("/api/auth/login")
                .then().body("user.email", equalTo(user.getEmail().toLowerCase()));
    }

    @Test
    @DisplayName("Вход под существующим пользователем - проверка имени")
    public void loginExistingUserShouldReturnCorrectNameTest() {
        Credentials credentials = new Credentials(user.getEmail(), user.getPassword());

        given()
                .header("Content-type", "application/json")
                .body(credentials)
                .when()
                .post("/api/auth/login")
                .then().body("user.name", equalTo(user.getName()));
    }

    @Test
    @DisplayName("Вход с неверным логином и паролем - проверка статуса 401")
    public void loginWithInvalidCredentialsShouldReturn401Test() {
        Credentials invalidCredentials = new Credentials("wrong@yandex.ru", "wrongpassword");

        given()
                .header("Content-type", "application/json")
                .body(invalidCredentials)
                .when()
                .post("/api/auth/login")
                .then().statusCode(401);
    }

    @Test
    @DisplayName("Вход с неверным логином и паролем - проверка поля success")
    public void loginWithInvalidCredentialsShouldReturnSuccessFalseTest() {
        Credentials invalidCredentials = new Credentials("wrong@yandex.ru", "wrongpassword");

        given()
                .header("Content-type", "application/json")
                .body(invalidCredentials)
                .when()
                .post("/api/auth/login")
                .then().body("success", equalTo(false));
    }

    @Test
    @DisplayName("Вход с неверным логином и паролем - проверка сообщения")
    public void loginWithInvalidCredentialsShouldReturnErrorMessageTest() {
        Credentials invalidCredentials = new Credentials("wrong@yandex.ru", "wrongpassword");

        given()
                .header("Content-type", "application/json")
                .body(invalidCredentials)
                .when()
                .post("/api/auth/login")
                .then().body("message", equalTo("email or password are incorrect"));
    }
}