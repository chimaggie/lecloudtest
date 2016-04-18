package com.lecloud.api.test.AccountResource;

import com.jayway.restassured.response.Response;

import org.json.JSONObject;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.given;

/**
 * Created by hongyuechi on 4/14/16.
 */
public class ChangePassword {
    static String userToken = null;
    static String adminToken = null;

    @BeforeClass
    public static void userLogin() {
        String url = "http://api.ffauto.us:8080/lecloud/api/pc/authenticate";
        String adminBody = "username=admin&password=admin";

        Response adminResp = given().body(adminBody).contentType("application/x-www-form-urlencoded").when().post(url);
        JSONObject adminObj = new JSONObject(adminResp.body().asString());
        adminToken = adminObj.getString("token");

        String userBody = "username=lecar&password=lecar";

        Response userResp = given().body(userBody).contentType("application/x-www-form-urlencoded").when().post(url);
        JSONObject userObj = new JSONObject(userResp.body().asString());
        userToken = userObj.getString("token");
    }

    @After
    public void setBackPwd() {
        given().header("x-auth-token", adminToken).when().delete("http://api.ffauto.us:8080/lecloud/api/users/lecar").then().assertThat().statusCode(200);

        String body = "{\n" +
                "    \"login\" : \"lecar\",\n" +
                "    \"password\" : \"lecar\",\n" +
                "    \"email\" : \"lecar@localhost\"\n" +
                "}";
        given().body(body).contentType("application/JSON")
                .when().post("http://api.ffauto.us:8080/lecloud/api/register");

    }

    @Test
    //user change password
    public void changePassword() {
        System.out.println(userToken);
        String body = "{\"password\":\"changed\"}";
        given().header("x-auth-token", userToken).body(body)
                .when().post("http://107.155.52.118:8080/lecloud/api/account/change_password")
                .then().assertThat().statusCode(200);
        given().body("username=admin&password=changed").contentType("application/x-www-form-urlencoded")
                .when().post("http://api.ffauto.us:8080/lecloud/api/pc/authenticate")
                .then().assertThat().statusCode(401);

    }
    @Test
    //user change password with none value
    public void changePassword2() {
        String body = "{\"password\":\"\"}";
        given().header("x-auth-token", userToken).body(body)
                .when().post("http://107.155.52.118:8080/lecloud/api/account/change_password")
                .then().assertThat().statusCode(400);
    }
}
