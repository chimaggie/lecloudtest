package com.lecloud.api.test;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.given;

/**
 * Created by hongyuechi on 4/6/16.
 */
public class RefreshToken {
    static String cpuRefreshToken = null;
    static String pcRefreshToken = null;
    static String mobileRefreshToken = null;
    static String cpuToken = null;
    static String pcToken = null;
    static String mobileToken = null;

    @BeforeClass
    public static void fetchData() throws JSONException,InterruptedException {
        //cpu login
        String cpuUrl = "http://api.ffauto.us:8080/lecloud/api/cpu/authenticate";
        String cpuBody = "username=admin&password=admin&vin=09876543211234567";
        Response cpuResp = given().body(cpuBody).contentType("application/x-www-form-urlencoded").when().post(cpuUrl);
        JSONObject cpuObj = new JSONObject(cpuResp.body().asString());
        cpuToken =  cpuObj.getString("token");
        cpuRefreshToken =  cpuObj.getString("refreshToken");

        //pc login
        String pcUrl = "http://api.ffauto.us:8080/lecloud/api/pc/authenticate";
        String pcBody = "username=admin&password=admin&vin=09876543211234567";
        Response pcResp = given().body(pcBody).contentType("application/x-www-form-urlencoded").when().post(pcUrl);
        JSONObject pcObj = new JSONObject(pcResp.body().asString());
        pcToken =  pcObj.getString("token");
        pcRefreshToken =  pcObj.getString("refreshToken");

        //mobile login
        String mobileUrl = "http://api.ffauto.us:8080/lecloud/api/mobile/authenticate";
        String mobileBody = "username=admin&password=admin&vin=09876543211234567";
        Response mobileResp = given().body(mobileBody).contentType("application/x-www-form-urlencoded").when().post(mobileUrl);
        JSONObject mobileObj = new JSONObject(mobileResp.body().asString());
        mobileToken =  mobileObj.getString("token");
        mobileRefreshToken =  mobileObj.getString("refreshToken");

    }

    @Test
    // user acceptance test with cpu refresh token
    public void cpuRefreshToken() throws JSONException {
        String url = "http://api.ffauto.us:8080/lecloud/api/account/refreshToken/" + cpuRefreshToken;
        given().header("x-auth-token", cpuToken).when().get(url).then().assertThat().statusCode(200);
    }
    @Test
    //uat with mobile refresh token
    public void mobileRefreshToken() throws JSONException {
        String url = "http://api.ffauto.us:8080/lecloud/api/account/refreshToken/" + mobileRefreshToken;
        given().header("x-auth-token", mobileToken).when().get(url).then().assertThat().statusCode(200);
    }
    @Test
    //uat with pc refresh token
    public void pcRefreshToken() throws JSONException {
        String url = "http://api.ffauto.us:8080/lecloud/api/account/refreshToken/" + pcRefreshToken;
        given().header("x-auth-token", pcToken).when().get(url).then().assertThat().statusCode(200);
    }
    @Test
    //verify cpu refresh token combine with pc or mobile token
    public void cpuRefreshTokenErr() throws JSONException {
        String url = "http://api.ffauto.us:8080/lecloud/api/account/refreshToken/" + cpuRefreshToken;
        given().header("x-auth-token", pcToken).when().get(url).then().assertThat().statusCode(401);
    }
    @Test
    //verify pc refresh token combine cpu token
    public void pcRefreshTokenErr() throws JSONException {
        String url = "http://api.ffauto.us:8080/lecloud/api/account/refreshToken/" + pcRefreshToken;
        given().header("x-auth-token", cpuToken).when().get(url).then().assertThat().statusCode(401);
    }
    @Test
    //verify refreshToken can only be used for once. that is, it will expire/not useful after refreshing
    public void cpuRefreshTokenErr2() throws JSONException {
        String url = "http://api.ffauto.us:8080/lecloud/api/account/refreshToken/" + cpuRefreshToken;
        given().header("x-auth-token", cpuToken).when().get(url);
        given().header("x-auth-token", cpuToken).when().get(url).then().assertThat().statusCode(401);
    }
    @Test
    // verify refresh token related only to its own token, no other token will be applied which means that cpu token cannot be applied on this cpu token
    public void cpuRefreshTokenErr3() throws JSONException {
        String url = "http://api.ffauto.us:8080/lecloud/api/account/refreshToken/" + cpuRefreshToken;
        String cpuToken = "r6EJmhhb5kE=1.eyJhbGciOiJkaXIiLCJlbmMiOiJBMTI4R0NNIn0..aTGunXTDTBgs0Uv_.WuiXhScONa8NrY8LAc31GSrdTjA.hpbS8EGIvM1ffQ8RVQrF3A";
        given().header("x-auth-token",cpuToken).when().get(url).then().assertThat().statusCode(401);
    }
    }
