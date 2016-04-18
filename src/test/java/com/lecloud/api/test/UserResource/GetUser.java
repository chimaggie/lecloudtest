package com.lecloud.api.test.UserResource;

import com.jayway.restassured.response.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.given;

/**
 * Created by hongyuechi on 4/12/16.
 */
public class GetUser {
    static String adminToken = null;
    static String userToken = null;

    @BeforeClass
    public static void userLogin() {
        String url = "http://api.ffauto.us:8080/lecloud/api/pc/authenticate";
        String body = "username=admin&password=admin&vin=09876543211234567";
        Response adminResp = given().body(body).contentType("application/x-www-form-urlencoded").when().post(url);
        JSONObject cpuObj = new JSONObject(adminResp.body().asString());
        adminToken = cpuObj.getString("token");

        //ensure the test user account existing in db
        String addUserBody = "{" +
                "  \"email\": \"test@localhost\"," +
                "  \"login\": \"test\"," +
                "  \"password\": \"test1\"" +
                "}";
        given().header("x-auth-token", adminToken).body(addUserBody).post("http://api.ffauto.us:8080/lecloud/api/users");

        Response userResp = given().body("username=test&password=test1")
                .contentType("application/x-www-form-urlencoded").when().post(url);
        JSONObject userObj = new JSONObject(userResp.body().asString());
        userToken = userObj.getString("token");

    }

    @Test
    //5.1 uat login with admin
    public void getUser() throws JSONException {
        given().header("x-auth-token", adminToken).when().get("http://api.ffauto.us:8080/lecloud/api/users/test")
                .then().assertThat().statusCode(200);
    }
    @Test
    //5.2 user role cannot fetch other user information
    public void getUser2() throws JSONException {
        given().header("x-auth-token", userToken).when().get("http://api.ffauto.us:8080/lecloud/api/users/test")
                .then().assertThat().statusCode(403);
    }
    @Test
    //5.3 user can access his own information
    public void getUser3() throws JSONException {
        given().header("x-auth-token", userToken).when().get("http://api.ffauto.us:8080/lecloud/api/users/test")
                .then().assertThat().statusCode(200);
    }
}
