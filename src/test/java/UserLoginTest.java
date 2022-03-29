import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

public class UserLoginTest {
    public UserClient userClient;
    public User user;
    private String refreshToken;
    public UserLogout userLogout;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = User.getRandom();
        userClient.create(user);
    }

    @After
    public void tearDown() {
        try {
            if (refreshToken.isEmpty()) userClient.logout(userLogout);
        } catch (NullPointerException e) {
            System.out.println("Passed with Exception");
        }
    }

    // Проверка авторизации пользователя с заполненными обязательными полями
    @Test
    public void testUserLogin() {
        Response response = userClient.login(user);
        response.then().assertThat().body("success", equalTo(true)).and().statusCode(200);
        refreshToken = response.then().extract().path("refreshToken");
    }

    // Проверка авторизации пользователя с некорректным паролем
    @Test
    public void testUserLoginWithIncorrectPassword() {
        user.setPassword("test");
        Response response = userClient.login(user);
        response.then().assertThat().body("success", equalTo(false)).and().statusCode(401);
        refreshToken = response.then().extract().path("refreshToken");
    }

    // Проверка авторизации пользователя с некорректным логином
    @Test
    public void testUserLoginWithIncorrectEmail() {
        user.setEmail("test");
        Response response = userClient.login(user);
        response.then().assertThat().body("success", equalTo(false)).and().statusCode(401);
        refreshToken = response.then().extract().path("refreshToken");
    }
}
