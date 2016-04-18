package com.lecloud.api.test.UserResource;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.internal.ValidatableResponseImpl;
import com.jayway.restassured.response.Response;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.given;

/**
 * Created by hongyuechi on 4/8/16.
 */
public class DeleteUser {
    static String adminToken = null;
    static String userToken = null;

    @BeforeClass
    public static void userLogin() {
        String url = "http://api.ffauto.us:8080/lecloud/api/pc/authenticate";
        String adminBody = "username=admin&password=admin&vin=09876543211234567";
        String userBody = "username=test&password=test1";

        Response adminResp = given().body(adminBody).contentType("application/x-www-form-urlencoded").when().post(url);
        JSONObject adminObj = new JSONObject(adminResp.body().asString());
        adminToken = adminObj.getString("token");

        Response userResp = given().body(userBody).contentType("application/x-www-form-urlencoded").when().post(url);
        JSONObject userObj = new JSONObject(userResp.body().asString());
        userToken = userObj.getString("token");
    }
    @Before
    public void ensureUser() {
        String body = "{" +
                "  \"email\": \"le@gmail.com\"," +
                "  \"login\": \"le\"," +
                "  \"password\": \"le\"" +
                "}";
        given().header("x-auth-token", adminToken).body(body).post("http://api.ffauto.us:8080/lecloud/api/users");
    }
    @Test
    // 3.1 user do not have the right to delete the user
    public void deleteUser2() {
        String url = "http://api.ffauto.us:8080/lecloud/api/users/le";
        given().header("x-auth-token", userToken).when().delete(url).then().assertThat().statusCode(403);

    }

    @Test
    // 3.2 admin can delete a user
    public void deleteUser() {
        String url = "http://api.ffauto.us:8080/lecloud/api/users/le";
        given().header("x-auth-token", adminToken).when().delete(url).then().assertThat().statusCode(200);

        given().header("x-auth-token", adminToken).when()
                .get("http://api.ffauto.us:8080/lecloud/api/users/le").then().assertThat().statusCode(404);
    }
}
