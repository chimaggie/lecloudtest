package com.lecloud.api.test.AccountResource;

import com.jayway.restassured.response.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.given;

/**
 * Created by hongyuechi on 4/13/16.
 */
public class RegisterAccount {
    static String adminToken = null;

    @BeforeClass
    public static void ensureNoExistUser() {
        Response resp = given().body("username=admin&password=admin")
                .contentType("application/x-www-form-urlencoded").when().post("http://api.ffauto.us:8080/lecloud/api/pc/authenticate");
        JSONObject obj = new JSONObject(resp.body().asString());
        adminToken = obj.getString("token");
    }

    @Before
    public void deleteUser() {
        given().header("x-auth-token", adminToken)
                .when().delete("http://api.ffauto.us:8080/lecloud/api/users/faraday");
    }

    @Test
    // user acceptance test
    public void userRegister() throws JSONException {
        String body = "{\n" +
                "    \"login\" : \"faraday\",\n" +
                "    \"password\" : \"faraday\",\n" +
                "    \"email\" : \"faraday@localhost\"\n" +
                "}";
        given().body(body).contentType("application/JSON")
                .when().post("http://api.ffauto.us:8080/lecloud/api/register")
                .then().assertThat().statusCode(201);
    }

    @Test
    // cannot register with duplicated user name
    public void userRegister2() throws JSONException {
        String body = "{\n" +
                "    \"login\" : \"faraday\",\n" +
                "    \"password\" : \"faraday\",\n" +
                "    \"email\" : \"faraday@localhost\"\n" +
                "}";
        given().body(body).contentType("application/JSON")
                .when().post("http://api.ffauto.us:8080/lecloud/api/register");
        given().body(body).contentType("application/JSON")
                .when().post("http://api.ffauto.us:8080/lecloud/api/register")
                .then().assertThat().statusCode(400);
    }
    @Test
    // cannot register with duplicated email address
    public void userRegister3() throws JSONException {
        String body = "{\n" +
                "    \"login\" : \"lecar\",\n" +
                "    \"password\" : \"lecar\",\n" +
                "    \"email\" : \"system@localhost\"\n" +
                "}";
        given().body(body).contentType("application/JSON")
                .when().post("http://api.ffauto.us:8080/lecloud/api/register")
                .then().assertThat().statusCode(400);
    }
    @Test
    // user cannot register without entering an email address
    public void userRegister4() throws JSONException {
        String body = "{\n" +
                "    \"login\" : \"faraday\",\n" +
                "    \"password\" : \"faraday\"\n" +
                "}";
        given().body(body).contentType("application/JSON")
                .when().post("http://api.ffauto.us:8080/lecloud/api/register")
                .then().assertThat().statusCode(400);
    }
}
