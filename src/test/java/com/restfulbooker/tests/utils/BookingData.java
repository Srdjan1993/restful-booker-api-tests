package com.restfulbooker.tests.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class BookingData {
    
    public String firstname;
    public String lastname;
    public int totalprice;
    public boolean depositpaid;
    public BookingDates bookingdates;
    public String additionalneeds;
    
    public BookingData(String firstname, String lastname, int totalprice, 
                      boolean depositpaid, String checkin, String checkout, 
                      String additionalneeds) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.totalprice = totalprice;
        this.depositpaid = depositpaid;
        this.bookingdates = new BookingDates(checkin, checkout);
        this.additionalneeds = additionalneeds;
    }
    
    public static class BookingDates {
        public String checkin;
        public String checkout;
        
        public BookingDates(String checkin, String checkout) {
            this.checkin = checkin;
            this.checkout = checkout;
        }
    }
    
    /**
     * Convert to JSON string
     */
    public String toJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }
    
    /**
     * Create test booking with default values
     */
    public static BookingData createDefaultBooking() {
        return new BookingData(
            "John",
            "Smith",
            150,
            true,
            "2024-02-01",
            "2024-02-05",
            "Breakfast"
        );
    }
    
    /**
     * Create booking for update test
     */
    public static BookingData createUpdateBooking() {
        return new BookingData(
            "James",
            "Brown",
            250,
            true,
            "2024-03-01",
            "2024-03-10",
            "Breakfast and Lunch"
        );
    }
    
    /**
     * Create partial update (only firstname and lastname)
     */
    public static String createPartialUpdateJson(String firstname, String lastname) {
        return "{\"firstname\":\"" + firstname + "\",\"lastname\":\"" + lastname + "\"}";
    }
}