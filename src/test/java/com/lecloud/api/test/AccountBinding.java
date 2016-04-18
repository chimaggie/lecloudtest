package com.lecloud.api.test;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.browserlaunchers.locators.FirefoxLocator;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created by hongyuechi on 4/6/16.
 */
public class AccountBinding {
    static String cpuToken = null;
    static String sso_tk = null;
    @BeforeClass
    public static void cpuLogin() throws JSONException,InterruptedException {
        // get cpuToken
        String APIUrl = "http://api.ffauto.us:8080/lecloud/api/cpu/authenticate";
        String apiFormBody = "username=admin&password=admin&vin=09876543211234567";
        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.setBody(apiFormBody);
        builder.setContentType("application/x-www-form-urlencoded");
        RequestSpecification requestSpec = builder.build();
        Response response = given().authentication().preemptive().basic("", "").spec(requestSpec).when().post(APIUrl);
        JSONObject jsonResp = new JSONObject(response.body().asString());
        cpuToken =  jsonResp.getString("token");

        //get sso_tk from le account login
        WebDriver driver = new FirefoxDriver();
        driver.get("http://sso.le.com");
        driver.findElement(By.id("loginname")).sendKeys("0014086378425");
        driver.findElement(By.id("password")).sendKeys("4086378425");
        driver.findElement(By.id("submitLogin")).click();
        //// TODO: 4/6/16 let driver login in background silently
        Thread.sleep(2000);
        String tmp = driver.manage().getCookieNamed("sso_tk").toString();
        Thread.sleep(2000);
        sso_tk = tmp.substring(7, tmp.indexOf(";"));
        driver.close();
        driver.quit();
    }
    @Before
    public void unBind() throws JSONException {
        given().header("x-auth-token", cpuToken).when()
                .get("http://api.ffauto.us:8080/lecloud/api/account/unBinding");
    }

    public String getSsoToken() throws Exception{
        String sso_tk = null;
        WebDriver driver = new FirefoxDriver();
        driver.get("http://sso.le.com");
        driver.findElement(By.id("loginname")).sendKeys("0014086378425");
        driver.findElement(By.id("password")).sendKeys("4086378425");
        driver.findElement(By.id("submitLogin")).click();
        //// TODO: 4/6/16 let driver login in background silently
        Thread.sleep(1000);
        String tmp = driver.manage().getCookieNamed("sso_tk").toString();
        Thread.sleep(1000);
        sso_tk = tmp.substring(7, tmp.indexOf(";"));
        driver.close();
        driver.quit();
        return sso_tk;
    }

    @Test
    public void unBinding() throws JSONException {
        given().header("x-auth-token", cpuToken).when()
                .get("http://api.ffauto.us:8080/lecloud/api/account/unBinding").then().assertThat().statusCode(200);

        System.out.println("Unbinding successfully.");
    }
    @Test
    public void unBindingErr() throws JSONException {
        String wrongToken = "Yfq3rTmaHDk=3.eyJhbGciOiJkaXIiLCJlbmMiOiJBMTI4R0NNIn0..zTtIi2BOtZmjyymV.gladi9vFhPqYh1MUaT6UnIZyeL0.mYJft4FBVTTirbc_Kr4qsg";
        given().header("x-auth-token", wrongToken).when()
                .get("http://api.ffauto.us:8080/lecloud/api/account/unBinding").then().assertThat().statusCode(401);
    }
    @Test
    public void binding() throws Exception {
//        String sso_tk = getSsoToken();
        System.out.println("sso_tk is: " + sso_tk + "\n" + "cpuToken is: " + cpuToken);
        String url = "http://api.ffauto.us:8080/lecloud/api/account/binding/"+sso_tk;
        given().header("x-auth-token", cpuToken).when()
                .get(url).then().assertThat().statusCode(200);
    }
    @Test
    //binding with wrong sso_tk for the same string length
    public void bindingWrong() throws JSONException {
        String wrongSso_tk = "3023806998DGzt1J0m3RqR9Js3jIm2Z138zw2vNxfVKHzYKSaqdPeHxLphLQ5cwst5fsrKVME8A";
//        String sso_tk = getSsoToken();
        System.out.println("wrong sso_tk is: " + wrongSso_tk + "\n" + "correct sso_tk is: " + sso_tk);

        String url = "http://api.ffauto.us:8080/lecloud/api/account/binding/"+wrongSso_tk;
        given().header("x-auth-token", cpuToken).when()
                .get(url).then().assertThat().statusCode(401);
    }

    @Test
    //binding with wrong sso_tk for the diff string length
    public void bindingWrong2() throws Exception {
        String wrongSso_tk = "333023806998DGzt1J0m3RqR9Js3jIm2Z138zw2vNxfVKHzYKSaqdPeHxLphLQ5cwst5fsrKVME8A";
//        String sso_tk = getSsoToken();
        System.out.println("wrong sso_tk is: " + wrongSso_tk + "\n" + "correct sso_tk is: " + sso_tk);

        String url = "http://api.ffauto.us:8080/lecloud/api/account/binding/"+wrongSso_tk;
        given().header("x-auth-token", cpuToken).when()
                .get(url).then().assertThat().statusCode(401);
    }

    @Test
    //binding twice should fail
    public void bindingTwice() throws Exception {
//        String sso_tk = getSsoToken();
        String url = "http://api.ffauto.us:8080/lecloud/api/account/binding/"+ sso_tk;
        given().header("x-auth-token", cpuToken).when().get(url);
        given().header("x-auth-token",cpuToken).when().get(url).then().assertThat().statusCode(409);
    }
}
