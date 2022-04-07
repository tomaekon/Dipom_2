import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

public class UserLoginTest {
    public UserClient userClient;
    public User user;
    public String refreshToken;
    public UserLogout userLogout;
    Response response;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = User.getRandom();
        userClient.create(user);
    }

    @After
    public void tearDown() {
        refreshToken = response.then().extract().path("refreshToken");
        boolean refreshNotNullAndIsEmpty = (refreshToken != null) && (refreshToken.isEmpty());
        if (refreshNotNullAndIsEmpty) userClient.logout(userLogout);
    }

    @Test
    @DisplayName("Проверка авторизации пользователя с заполненными обязательными полями")
    public void testUserLogin() {
        response = userClient.login(user);
        response.then().assertThat().body("success", equalTo(true)).and().statusCode(200);
    }

    @Test
    @DisplayName("Проверка авторизации пользователя с некорректным паролем")
    public void testUserLoginWithIncorrectPassword() {
        user.setPassword("test");
        response = userClient.login(user);
        response.then().assertThat().body("success", equalTo(false)).and().statusCode(401);
    }

    @Test
    @DisplayName("Проверка авторизации пользователя с некорректным логином")
    public void testUserLoginWithIncorrectEmail() {
        user.setEmail("test");
        response = userClient.login(user);
        response.then().assertThat().body("success", equalTo(false)).and().statusCode(401);
    }
}
