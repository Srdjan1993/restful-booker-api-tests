package com.restfulbooker.tests.delete;

import com.restfulbooker.tests.base.TestBase;
import com.restfulbooker.tests.utils.BookingData;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteBookingTests extends TestBase {
    
    private static final Logger logger = LoggerFactory.getLogger(DeleteBookingTests.class);
    
    /**
     * TC_DELETE_001: Delete Existing Booking
     */
    @Test(priority = 40, description = "Delete existing booking")
    public void testDeleteBooking() {
        logger.info("TEST: TC_DELETE_001 - Delete Booking");
        
        BookingData bookingData = BookingData.createDefaultBooking();
        Response createResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(bookingData.toJson())
                .when()
                .post("/booking")
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        int bookingIdToDelete = createResponse.jsonPath().getInt("bookingid");
        logger.info("   Created booking for deletion - ID: " + bookingIdToDelete);
        
        String token = getAuthToken();
        
        Response deleteResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .when()
                .delete("/booking/" + bookingIdToDelete)
                .then()
                .statusCode(201)
                .extract()
                .response();
        
        String responseBody = deleteResponse.asString();
        Assert.assertTrue(responseBody.contains("OK"), "Response should contain 'OK'");
        
        Response getDeletedResponse = RestAssured
                .given()
                .when()
                .get("/booking/" + bookingIdToDelete)
                .then()
                .extract()
                .response();
        
        int statusCode = getDeletedResponse.getStatusCode();
        Assert.assertEquals(statusCode, 404, "Deleted booking should not be found (404)");
        
        logger.info("Booking deleted successfully - ID: " + bookingIdToDelete);
        logger.info("   Verification: GET returns 404 (Not Found)");
        logTestResult("TC_DELETE_001", "PASSED");
    }
    
    /**
     * TC_DELETE_002: Delete Multiple Bookings
     */
    @Test(priority = 41, description = "Delete multiple bookings")
    public void testDeleteMultipleBookings() {
        logger.info("TEST: TC_DELETE_002 - Delete Multiple Bookings");
        
        String token = getAuthToken();
        int deletedCount = 0;
        
        for (int i = 0; i < 3; i++) {
            BookingData bookingData = new BookingData(
                "Guest" + i,
                "Test",
                100,
                true,
                "2024-02-01",
                "2024-02-05",
                "Test"
            );
            
            Response createResponse = RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(bookingData.toJson())
                    .when()
                    .post("/booking")
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();
            
            int bookingId = createResponse.jsonPath().getInt("bookingid");
            
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
            
            String responseBody = deleteResponse.asString();
            if (responseBody.contains("OK")) {
                deletedCount++;
                logger.info("Booking " + (i+1) + " deleted - ID: " + bookingId);
            }
        }
        
        Assert.assertEquals(deletedCount, 3, "All 3 bookings should be deleted");
        logger.info("Successfully deleted " + deletedCount + " bookings");
        logTestResult("TC_DELETE_002", "PASSED");
    }
}