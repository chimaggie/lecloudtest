package com.lecloud.api.test.CarUserResource;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;

import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.given;
import static junit.framework.TestCase.assertEquals;

/**
 * Created by hongyuechi on 4/15/16.
 */
public class CarBind {

    String bindUrl = "http://api.ffauto.us:8080/lecloud/api/carUsers/bind";
    String body = "token=i3RwG6c%2BOjH7%2FU%2FuYpVKPfTV3NnilYRCdLUQSSNE0do%3D5.eyJhbGciOiJkaXIiLCJlbmMiOiJBMTI4R0NNIn0..Fc6NY_Snj_k8s0yU.Oa1OUmYk9O1ROJLVwg.enhFtzpMO4G0urr8KBhGiw";
    static String adminToken = null;

    static String userToken = null;

    @BeforeClass
    public static void getUserToken() {
        String url = "http://api.ffauto.us:8080/lecloud/api/pc/authenticate";
        String adminBody = "username=admin&password=admin";

        Response adminResp = given().body(adminBody).contentType("application/x-www-form-urlencoded").when().post(url);
        JSONObject adminObj = new JSONObject(adminResp.body().asString());
        adminToken = adminObj.getString("token");

        String userBody = "username=test&password=test1";

        Response userResp = given().body(userBody).contentType("application/x-www-form-urlencoded").when().post(url);
        JSONObject userObj = new JSONObject(userResp.body().asString());
        userToken = userObj.getString("token");
    }

    @Before
    public void unBind() {
        String carId = "570fdb6767d0930762cb58b2";
        given().header("x-auth-token",adminToken).get("http://api.ffauto.us:8080/lecloud/api/carUsers/unbind/" + carId);
        given().header("x-auth-token",userToken).get("http://api.ffauto.us:8080/lecloud/api/carUsers/unbind/" + carId);
    }
    @Test
    // admin bind vin for the first time, admin is a primary account for this car
    public void carBind() {

        Response bindResp = given().body(body).contentType("application/x-www-form-urlencoded").header("x-auth-token", adminToken).post(bindUrl);
        bindResp.then().assertThat().statusCode(201);
        JSONObject bindObj = new JSONObject(bindResp.body().asString());
        assertEquals("1", bindObj.get("status").toString());
    }
    @Test
    //admin bind the same vin, cannot bind the same vin twice
    public void carBind2() {
        given().body(body).contentType("application/x-www-form-urlencoded").header("x-auth-token", adminToken).post(bindUrl);
        //twice bind should fail
        Response bindResp = given().body(body).contentType("application/x-www-form-urlencoded").header("x-auth-token", adminToken).post(bindUrl);
        bindResp.then().assertThat().statusCode(400);
    }

    @Test
    // user bind the vin as a sub account
    public void carBind3() {
        given().body(body).contentType("application/x-www-form-urlencoded").header("x-auth-token", adminToken).post(bindUrl);

        Response bindResp = given().body(body).contentType("application/x-www-form-urlencoded").header("x-auth-token", userToken).post(bindUrl);
        bindResp.then().assertThat().statusCode(201);
        JSONObject bindObj = new JSONObject(bindResp.body().asString());
        assertEquals(2, bindObj.get("status").toString());
    }

    @Test
    // user bind the vin as a sub account
    public void carBind4() {
        given().body(body).contentType("application/x-www-form-urlencoded").header("x-auth-token", adminToken).post(bindUrl);

        given().body(body).contentType("application/x-www-form-urlencoded").header("x-auth-token", userToken).post(bindUrl);
        Response bindResp = given().body(body).contentType("application/x-www-form-urlencoded").header("x-auth-token", userToken).post(bindUrl);
        bindResp.then().assertThat().statusCode(400);
    }

}
