import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

public class UserRegisterTest {
    public UserClient userClient;
    public User user;
    public String refreshToken;
    public UserLogout userLogout;
    Response response;

    @Before
    public void setUp() {
        userClient = new UserClient();
    }

    @After
    public void tearDown() {

        refreshToken = response.then().extract().path("refreshToken");
        boolean refreshNotNullAndIsEmpty = (refreshToken != null) && (refreshToken.isEmpty());
        if (refreshNotNullAndIsEmpty) userClient.logout(userLogout);
    }

    @Test
    @DisplayName("Проверка создания уникального пользователя")
    public void testUserIsCreated() {

        user = User.getRandom();
        response = userClient.create(user);
        response.then().assertThat().body("success", equalTo(true)).and().statusCode(200);
    }

    @Test
    @DisplayName("Проверка создания пользователя, который уже зарегистрирован")
    public void testUserIsAlreadyRegister() {

        user = User.getRandom();
        userClient.create(user);
        response = userClient.create(user);
        response.then().assertThat().body("success", equalTo(false)).and().statusCode(403);
    }

    @Test
    @DisplayName("Проверка создания пользователя без заполнения поля email")
    public void testUserIsCreatedWithoutEmail() {

        user = User.getRandom();
        user.setEmail(null);
        userClient.create(user);
        response = userClient.create(user);
        response.then().assertThat().body("success", equalTo(false)).and().statusCode(403);
    }

    @Test
    @DisplayName("Проверка создания пользователя без заполнения поля password")
    public void testUserIsCreatedWithoutPassword() {

        user = User.getRandom();
        user.setPassword(null);
        userClient.create(user);
        response = userClient.create(user);
        response.then().assertThat().body("success", equalTo(false)).and().statusCode(403);
    }

    @Test
    @DisplayName("Проверка создания пользователя без заполнения поля name")
    public void testUserIsCreatedWithoutName() {

        user = User.getRandom();
        user.setName(null);
        userClient.create(user);
        response = userClient.create(user);
        response.then().assertThat().body("success", equalTo(false)).and().statusCode(403);
    }
}
