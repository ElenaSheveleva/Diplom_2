package api;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;

@Feature("Создание пользователя")
public class CreateUserTest extends BaseTest {

    private User user;
    private String accessToken;
    private UserClient userClient;

    @Before
    public void setUpData() {
        userClient = new UserClient();
        user = new User("testuser_" + System.currentTimeMillis() + "@yandex.ru", "password123", "TestUser");
    }

    @After
    public void cleanUp() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Создание уникального пользователя - проверка статуса и success")
    @Description("Проверка успешного создания нового уникального пользователя")
    public void createUniqueUserShouldReturnSuccessTest() {
        Response response = userClient.register(user);
        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true));
        accessToken = response.jsonPath().getString("accessToken");
    }

    @Test
    @DisplayName("Создание уникального пользователя - проверка email")
    @Description("Проверка email созданного пользователя")
    public void createUniqueUserShouldReturnCorrectEmailTest() {
        Response response = userClient.register(user);
        response.then()
                .statusCode(SC_OK)
                .body("user.email", equalTo(user.getEmail().toLowerCase()));
        accessToken = response.jsonPath().getString("accessToken");
    }

    @Test
    @DisplayName("Создание уникального пользователя - проверка имени")
    @Description("Проверка имени созданного пользователя")
    public void createUniqueUserShouldReturnCorrectNameTest() {
        Response response = userClient.register(user);
        response.then()
                .statusCode(SC_OK)
                .body("user.name", equalTo(user.getName()));
        accessToken = response.jsonPath().getString("accessToken");
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован - проверка статуса и success")
    @Description("Проверка ошибки при попытке создать пользователя с уже существующим email")
    public void createExistingUserShouldReturnErrorTest() {
        userClient.register(user);
        Response response = userClient.register(user);
        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false));
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован - проверка сообщения")
    @Description("Проверка сообщения об ошибке")
    public void createExistingUserShouldReturnErrorMessageTest() {
        userClient.register(user);
        Response response = userClient.register(user);
        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без email - проверка статуса и success")
    @Description("Проверка ошибки при регистрации без email")
    public void createUserWithoutEmailShouldReturnErrorTest() {
        User userWithoutEmail = new User(null, "password123", "TestUser");
        Response response = userClient.register(userWithoutEmail);
        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false));
    }

    @Test
    @DisplayName("Создание пользователя без email - проверка сообщения")
    @Description("Проверка сообщения об ошибке")
    public void createUserWithoutEmailShouldReturnErrorMessageTest() {
        User userWithoutEmail = new User(null, "password123", "TestUser");
        Response response = userClient.register(userWithoutEmail);
        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }
}