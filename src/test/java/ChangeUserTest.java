import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

public class ChangeUserTest {
    Faker faker = new Faker();
    public UserClient userClient;
    public User user;
    private String refreshToken;
    private String accessToken;
    public UserLogout userLogout;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = User.getRandom();
        Response response = userClient.create(user);
        refreshToken = response.then().extract().path("refreshToken");
        accessToken = response.then().extract().path("accessToken");
    }

    @After
    public void tearDown() {
        boolean refreshNotNullAndIsEmpty = (refreshToken != null) && (refreshToken.isEmpty());
        if (refreshNotNullAndIsEmpty) userClient.logout(userLogout);
    }

    @Test
    @DisplayName("Проверка изменения данных логина для авторизированного пользователя")
    public void UserAuthorizationChangeEmailTest() {
        userClient.getDataUser(accessToken);
        user.setEmail(faker.internet().emailAddress());
        Response response = userClient.setDataUserWithToken(accessToken, new User(user.getEmail(), user.getName()));
        response.then().assertThat().body("success", equalTo(true)).and().statusCode(200);
    }

    @Test
    @DisplayName("Проверка изменения данных имени для авторизированного пользователя")
    public void UserAuthorizationChangeNameTest() {
        userClient.getDataUser(accessToken);
        user.setName(faker.name().firstName());
        Response response = userClient.setDataUserWithToken(accessToken, new User(user.getEmail(), user.getName()));
        response.then().assertThat().body("success", equalTo(true)).and().statusCode(200);
    }

    @Test
    @DisplayName("Проверка изменения данных логина для неавторизированного пользователя")
    public void UserNoAuthorizationChangeEmailTest() {
        userClient.getDataUser(accessToken);
        user.setEmail(faker.internet().emailAddress());
        Response response = userClient.setDataUserWithoutToken(new User(user.getEmail(), user.getName()));
        response.then().assertThat().body("success", equalTo(false)).and().statusCode(401);
    }

    @Test
    @DisplayName("Проверка изменения данных имени для неавторизированного пользователя")
    public void UserNoAuthorizationChangeNameTest() {
        userClient.getDataUser(accessToken);
        user.setName(faker.name().firstName());
        Response response = userClient.setDataUserWithoutToken(new User(user.getEmail(), user.getName()));
        response.then().assertThat().body("success", equalTo(false)).and().statusCode(401);
    }
}
