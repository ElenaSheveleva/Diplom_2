package api;

import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.Credentials;
import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.equalTo;

@Feature("Логин пользователя")
public class LoginUserTest extends BaseTest {

    private User user;
    private String accessToken;
    private UserClient userClient;

    @Before
    public void setUpData() {
        userClient = new UserClient();
        user = new User("loginuser_" + System.currentTimeMillis() + "@yandex.ru", "password123", "LoginUser");
        Response registerResponse = userClient.register(user);
        accessToken = registerResponse.jsonPath().getString("accessToken");
    }

    @After
    public void cleanUp() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Вход под существующим пользователем - проверка статуса и success")
    public void loginExistingUserShouldReturnSuccessTest() {
        Credentials credentials = new Credentials(user.getEmail(), user.getPassword());
        Response response = userClient.login(credentials);
        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Вход под существующим пользователем - проверка email")
    public void loginExistingUserShouldReturnCorrectEmailTest() {
        Credentials credentials = new Credentials(user.getEmail(), user.getPassword());
        Response response = userClient.login(credentials);
        response.then()
                .statusCode(SC_OK)
                .body("user.email", equalTo(user.getEmail().toLowerCase()));
    }

    @Test
    @DisplayName("Вход под существующим пользователем - проверка имени")
    public void loginExistingUserShouldReturnCorrectNameTest() {
        Credentials credentials = new Credentials(user.getEmail(), user.getPassword());
        Response response = userClient.login(credentials);
        response.then()
                .statusCode(SC_OK)
                .body("user.name", equalTo(user.getName()));
    }

    @Test
    @DisplayName("Вход с неверным логином и паролем - проверка статуса и success")
    public void loginWithInvalidCredentialsShouldReturnErrorTest() {
        Credentials invalidCredentials = new Credentials("wrong@yandex.ru", "wrongpassword");
        Response response = userClient.login(invalidCredentials);
        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false));
    }

    @Test
    @DisplayName("Вход с неверным логином и паролем - проверка сообщения")
    public void loginWithInvalidCredentialsShouldReturnErrorMessageTest() {
        Credentials invalidCredentials = new Credentials("wrong@yandex.ru", "wrongpassword");
        Response response = userClient.login(invalidCredentials);
        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("message", equalTo("email or password are incorrect"));
    }
}