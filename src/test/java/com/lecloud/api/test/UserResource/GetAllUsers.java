package com.lecloud.api.test.UserResource;

import com.jayway.restassured.response.Response;
import com.sun.xml.internal.bind.v2.TODO;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.given;

/**
 * Created by hongyuechi on 4/8/16.
 */
public class GetAllUsers {
    static String adminToken = null;
    static String userToken = null;

    @BeforeClass
    public static void userLogin() {
        String url = "http://api.ffauto.us:8080/lecloud/api/pc/authenticate";
        String body = "username=admin&password=admin&vin=09876543211234567";
        Response adminResp = given().body(body).contentType("application/x-www-form-urlencoded").when().post(url);
        JSONObject cpuObj = new JSONObject(adminResp.body().asString());
        adminToken = cpuObj.getString("token");

        Response userResp = given().body("username=test&password=test1")
                .contentType("application/x-www-form-urlencoded").when().post(url);
        JSONObject userObj = new JSONObject(userResp.body().asString());
        userToken = userObj.getString("token");
    }

    @Test
    //4.1 uat login with admin
    public void getAllUsers() throws JSONException {
        given().header("x-auth-token", adminToken).when().get("http://api.ffauto.us:8080/lecloud/api/users")
                .then().assertThat().statusCode(200);
    }
    @Test
    //4.2 user role cannot fetch users information
    public void getAllUsers2() throws JSONException {
        given().header("x-auth-token", userToken).when().get("http://api.ffauto.us:8080/lecloud/api/users")
                .then().assertThat().statusCode(403);
    }
    //TODO: admin can access users from pc, mobile, vehicle
    //TODO: other role(sub, system, user, primary, admin, car) can or not access users information
}
