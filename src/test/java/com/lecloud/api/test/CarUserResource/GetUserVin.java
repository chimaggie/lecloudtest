package com.lecloud.api.test.CarUserResource;

import com.jayway.restassured.response.Response;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by hongyuechi on 4/15/16.
 */
public class GetUserVin {
    static String adminToken = null;

    @BeforeClass
    public static void login() {
        Response resp = given().body("username=admin&password=admin")
                .contentType("application/x-www-form-urlencoded").when().post("http://api.ffauto.us:8080/lecloud/api/pc/authenticate");
        JSONObject obj = new JSONObject(resp.body().asString());
        adminToken = obj.getString("token");
    }

    @Test
    public void getUserVin() {
        Response resp = given().header("x-auth-token", adminToken).get("http://api.ffauto.us:8080/lecloud/api/vins");
        JSONArray result = new JSONArray(resp.body().asString());
        assertTrue(result.length()!=0);
        assertEquals(200,resp.statusCode());
    }
}
