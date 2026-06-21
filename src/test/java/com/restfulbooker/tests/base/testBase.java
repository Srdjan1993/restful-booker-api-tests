package com.restfulbooker.tests.base;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestBase {
    
    private static final Logger logger = LoggerFactory.getLogger(TestBase.class);
    
    public static final String BASE_URL = "https://restful-booker.herokuapp.com";
    public static final String AUTH_USERNAME = "admin";
    public static final String AUTH_PASSWORD = "password123";
    
    protected static String authToken;
    protected static int bookingId;
    protected static int bookingIdToDelete;
    
    @BeforeClass
    public void setup() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        logger.info("Test setup initialized - Base URL: " + BASE_URL);
    }
    
    /**
     * Get authentication token
     */
    public static String getAuthToken() {
        if (authToken == null || authToken.isEmpty()) {
            logger.info("Fetching authentication token...");
            Response response = RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body("{\"username\":\"" + AUTH_USERNAME + "\",\"password\":\"" + AUTH_PASSWORD + "\"}")
                    .when()
                    .post("/auth")
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();
            
            authToken = response.jsonPath().getString("token");
            logger.info("Token obtained: " + authToken);
        }
        return authToken;
    }
    
    /**
     * Refresh token
     */
    public static void refreshAuthToken() {
        authToken = null;
        getAuthToken();
    }
    
    /**
     * Log test result
     */
    protected void logTestResult(String testName, String status) {
        logger.info(String.format("TEST: %s | STATUS: %s", testName, status));
    }
}
