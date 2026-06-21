package com.restfulbooker.tests.negativeandworkflow;

import com.restfulbooker.tests.base.TestBase;
import com.restfulbooker.tests.utils.BookingData;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NegativeAndWorkflowTests extends TestBase {
    
    private static final Logger logger = LoggerFactory.getLogger(NegativeAndWorkflowTests.class);
    
    // ===== NEGATIVE TESTS =====
    
    /**
     * TC_NEG_001: Invalid Authentication Token
     */
    @Test(priority = 50, description = "Reject request with invalid token")
    public void testInvalidToken() {
        logger.info("TEST: TC_NEG_001 - Invalid Authentication Token");
        
        String invalidToken = "invalidtoken123xyz";
        
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .cookie("token", invalidToken)
                .body(BookingData.createDefaultBooking().toJson())
                .when()
                .put("/booking/1")
                .then()
                .extract()
                .response();
        
        int statusCode = response.getStatusCode();
        Assert.assertEquals(statusCode, 403, "Should return 403 Forbidden for invalid token");
        
        logger.info("Invalid token rejected with status: " + statusCode);
        logTestResult("TC_NEG_001", "PASSED");
    }
    
    /**
     * TC_NEG_002: Non-existent Booking ID
     */
    @Test(priority = 51, description = "Handle request for non-existent booking")
    public void testNonExistentBookingId() {
        logger.info("TEST: TC_NEG_002 - Non-existent Booking ID");
        
        int nonExistentId = 999999;
        
        Response response = RestAssured
                .given()
                .when()
                .get("/booking/" + nonExistentId)
                .then()
                .extract()
                .response();
        
        int statusCode = response.getStatusCode();
        Assert.assertEquals(statusCode, 404, "Should return 404 for non-existent booking");
        
        logger.info("Non-existent booking returns 404 status");
        logTestResult("TC_NEG_002", "PASSED");
    }
    
    /**
     * TC_NEG_003: Malformed JSON in Request
     */
    @Test(priority = 52, description = "Handle malformed JSON")
    public void testMalformedJson() {
        logger.info("TEST: TC_NEG_003 - Malformed JSON");
        
        String malformedJson = "{\"firstname\": \"John\", \"lastname\": \"Smith\"";
        
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(malformedJson)
                .when()
                .post("/booking")
                .then()
                .extract()
                .response();
        
        int statusCode = response.getStatusCode();
        Assert.assertNotEquals(statusCode, 200, "Malformed JSON should not be accepted");
        
        logger.info("Malformed JSON rejected with status: " + statusCode);
        logTestResult("TC_NEG_003", "PASSED");
    }
    
    // ===== WORKFLOW TESTS =====
    
    /**
     * TC_WF_001: Complete Booking Lifecycle
     */
    @Test(priority = 60, description = "Complete booking lifecycle workflow")
    public void testCompleteBookingWorkflow() {
        logger.info("TEST: TC_WF_001 - Complete Booking Lifecycle");
        // ===== STEP 1: CREATE =====
        logger.info("STEP 1  - CREATE Booking");
        BookingData originalData = BookingData.createDefaultBooking();
        
        Response createResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(originalData.toJson())
                .when()
                .post("/booking")
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        int bookingId = createResponse.jsonPath().getInt("bookingid");
        Assert.assertTrue(bookingId > 0, "Should have created booking with valid ID");
        logger.info("Booking created - ID: " + bookingId);
        
        // ===== STEP 2: READ (Verify Creation) =====
        logger.info("STEP 2 - READ & VERIFY Creation");
        
        Response readResponse = RestAssured
                .given()
                .when()
                .get("/booking/" + bookingId)
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        String createdFirstname = readResponse.jsonPath().getString("firstname");
        Assert.assertEquals(createdFirstname, originalData.firstname, "Created data should match");
        logger.info("Booking verified - Name: " + createdFirstname + " " + 
                   readResponse.jsonPath().getString("lastname"));
        
        // ===== STEP 3: UPDATE =====
        logger.info("STEP 3  - UPDATE Booking");
        String token = getAuthToken();
        BookingData updateData = BookingData.createUpdateBooking();
        
        Response updateResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .body(updateData.toJson())
                .when()
                .put("/booking/" + bookingId)
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        String updatedFirstname = updateResponse.jsonPath().getString("firstname");
        Assert.assertEquals(updatedFirstname, updateData.firstname, "Updated data should match");
        logger.info("Booking updated - New name: " + updatedFirstname + " " + 
                   updateResponse.jsonPath().getString("lastname"));
        
        // ===== STEP 4: READ (Verify Update) =====
        logger.info("STEP 4  - READ & VERIFY Update");
        
        Response verifyResponse = RestAssured
                .given()
                .when()
                .get("/booking/" + bookingId)
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        String verifiedFirstname = verifyResponse.jsonPath().getString("firstname");
        Assert.assertEquals(verifiedFirstname, updateData.firstname, "Updated data should persist");
        logger.info("Update verified - Data persisted correctly");
        
        // ===== STEP 5: DELETE =====
        logger.info("STEP 5  - DELETE Booking");
        
        Response deleteResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .when()
                .delete("/booking/" + bookingId)
                .then()
                .statusCode(201)
                .extract()
                .response();
        
        String deleteMessage = deleteResponse.asString();
        Assert.assertTrue(deleteMessage.contains("OK"), "Delete should return OK");
        logger.info("Booking deleted successfully");
        
        // ===== STEP 6: VERIFY DELETION =====
        logger.info("STEP 6  - VERIFY Deletion");
        
        Response deletedCheckResponse = RestAssured
                .given()
                .when()
                .get("/booking/" + bookingId)
                .then()
                .extract()
                .response();
        
        Assert.assertEquals(deletedCheckResponse.getStatusCode(), 404, 
                          "Deleted booking should not be found");
        logger.info(" Deletion verified - Booking no longer exists");
        
        logger.info("================================================");
        logger.info(" COMPLETE WORKFLOW TEST PASSED");
        logTestResult("TC_WF_001", "PASSED");
    }

/**
     * TC_FAIL_001: INTENTIONAL FAILING TEST (For Bug Report Demonstration)
     */
    @Test(priority = 70, description = "Intentional failing test - Bug")
    public void testIntentionalFailure() {
        logger.info("TEST: TC_FAIL_001 - Intentional Failing Test");
        
        // This test is intentionally failing to demonstrate bug reporting
        Response response = RestAssured
                .given()
                .when()
                .get("/booking/1")
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        String firstname = response.jsonPath().getString("firstname");
        
        // This assertion will FAIL because we expect wrong value
        Assert.assertEquals(firstname, "INTENTIONAL_FAIL_VALUE", 
            "This test fails intentionally to demonstrate bug reporting");
        
        logTestResult("TC_FAIL_001", "FAILED");
    }






}
