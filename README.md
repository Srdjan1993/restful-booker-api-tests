# Restful Booker API - Test Suite Documentation

**Version:** 1.0 | **Date:** June 2026 | **Status:** Active

---

## Executive Summary

This document provides instructions for testing the Restful Booker API using Postman. The test suite covers CRUD operations (Create, Read, Update, Delete) for hotel bookings with automated token management and comprehensive test cases.

---

## Test Strategy

### Scope & Testing Approach

**API Endpoints Covered:**
- Authentication (POST /auth)
- GET /booking - All, filtered, specific ID
- POST /booking - Create new booking
- PUT /booking/:id - Full and partial update
- DELETE /booking/:id - Delete booking

**Testing Types:**
- Functional testing (positive and negative)
- Integration testing (multi-step workflows)
- End-to-end scenarios

**Quality Target:** 95% pass rate, Response time < 2000ms

---

## Tool Selection & Justification

### Why Postman?

| Aspect | Postman Advantage |
|--------|-------------------|
| **HTTP Testing** | Native REST API support, no extra setup |
| **Automation** | Pre-request scripts auto-generate tokens (expire every 15 min) |
| **Variables** | Dynamic data flow: POST response → stored ID → used in PUT/DELETE |
| **Assertions** | Built-in testing framework (pm.test) for response validation |
| **Workflows** | Execute complete CRUD cycle in sequence |
| **Cost** | Free tier covers all functional testing needs |
| **Learning** | No coding required, GUI-based, suitable for junior developers |

**Why NOT alternatives:**
- Selenium: Browser automation tool, overkill for API testing
- JMeter: Performance testing focused, complex setup
- REST Client CLI: No assertions, manual response verification

---

## Environment Setup

### Prerequisites
1. Postman installed (latest version)
2. Internet connection
3. Base URL: `https://restful-booker.herokuapp.com`

### Create Environment in Postman

**Variables:**
```
api_base_url     → https://restful-booker.herokuapp.com
auth_username    → admin
auth_password    → password123
token            → (auto-populated after /auth)
booking_id       → (auto-populated after POST /booking)
```

---

## Test Execution

### Quick Start (Manual)

**1. Get Token:**
```
POST /auth
Body: {"username": "admin", "password": "password123"}
→ Copy token from response
```

**2. Run Test:**
```
Select endpoint from collection
Add token to Headers: Cookie: token={{token}}
Click Send
Check "Tests" tab for results
```

### Automated Execution (Collection Runner)

```
Click Runner → Select Collection → Select Environment
Set Iterations: 5 → Click "Run"
View summary results
```

---

## Test Cases

### 1. TC_AUTH_001: Obtain Token
- **Request:** POST /auth with admin credentials
- **Expected:** Status 200, valid token returned
- **Assertion:** Response contains "token" field

### 2. TC_GET_001: Retrieve All Bookings
- **Request:** GET /booking (no filters)
- **Expected:** Status 200, array of bookings
- **Assertion:** Response is array, each item has "bookingid"

### 3. TC_GET_002: Filter by Firstname
- **Request:** GET /booking?firstname=John
- **Expected:** Status 200, filtered results
- **Assertion:** Results match filter or empty array

### 4. TC_GET_003: Filter by Date Range
- **Request:** GET /booking?checkin=2024-01-01&checkout=2024-01-31
- **Expected:** Status 200, bookings in date range
- **Assertion:** Response is valid array

### 5. TC_GET_004: Get Specific Booking
- **Request:** GET /booking/1
- **Expected:** Status 200, booking object
- **Assertion:** Contains firstname, lastname, totalprice, bookingdates

### 6. TC_POST_001: Create Booking
- **Request:** POST /booking with complete data
- **Body:**
```json
{
  "firstname": "John",
  "lastname": "Smith",
  "totalprice": 150,
  "depositpaid": true,
  "bookingdates": {
    "checkin": "2024-02-01",
    "checkout": "2024-02-05"
  },
  "additionalneeds": "Breakfast"
}
```
- **Expected:** Status 200, bookingid returned
- **Assertion:** Response contains bookingid > 0

### 7. TC_PUT_001: Full Booking Update
- **Request:** PUT /booking/{{booking_id}} with token
- **Body:** All fields updated
- **Expected:** Status 200, all fields changed
- **Assertion:** Firstname, lastname, price updated correctly

### 8. TC_PUT_002: Partial Update
- **Request:** PUT /booking/{{booking_id}} with token
- **Body:**
```json
{
  "firstname": "James",
  "lastname": "Brown"
}
```
- **Expected:** Status 200, only specified fields changed
- **Assertion:** Other fields (price, dates) unchanged

### 9. TC_DELETE_001: Delete Booking
- **Request:** DELETE /booking/{{booking_id}} with token
- **Pre-request Script:** Auto-generates fresh token
- **Expected:** Status 201, "OK" message
- **Assertion:** Response contains "OK"

### 10. TC_NEG_001: Invalid Token
- **Request:** PUT/DELETE with invalid token
- **Expected:** Status 403 Forbidden
- **Assertion:** Error response returned

### 11. TC_WF_001: Complete Workflow
- **Flow:** POST (create) → GET (verify) → PUT (update) → DELETE (remove)
- **Expected:** All steps succeed
- **Assertion:** Each step returns correct status and data

---

## Pre-request Script (Automatic Token)

Add this to any PUT/DELETE request to auto-generate token:

```javascript
const loginRequest = {
  url: 'https://restful-booker.herokuapp.com/auth',
  method: 'POST',
  header: {'Content-Type': 'application/json'},
  body: {
    mode: 'raw',
    raw: JSON.stringify({
      username: 'admin',
      password: 'password123'
    })
  }
};

pm.sendRequest(loginRequest, (error, response) => {
  if (!error) {
    const token = response.json().token;
    pm.environment.set('token', token);
    console.log('Token obtained: ' + token);
  }
});
```

---

## Test Headers

### Authentication Endpoints
```
Content-Type: application/json
```

### Secured Endpoints (PUT, DELETE)
```
Content-Type: application/json
Accept: application/json
Cookie: token={{token}}
```

---

## Expected Results

| Operation | Success Status | Key Validation |
|-----------|---|---|
| GET | 200 | Array/Object returned |
| POST | 200 | bookingid returned |
| PUT | 200 | Fields updated correctly |
| DELETE | 201 | "OK" message |
| Negative Test | 403/404 | Error returned |

---

## Execution Checklist

-  Environment created with variables
-  Collection imported
-  POST /auth test passes (token obtained)
-  GET /booking returns results
-  POST /booking creates new booking
-  PUT /booking updates successfully
-  DELETE /booking removes booking
-  All negative tests handled
-   Complete workflow passes

---

## Troubleshooting

| Error | Cause | Solution |
|-------|-------|----------|
| 403 Forbidden | Invalid/expired token | Re-run /auth endpoint |
| 404 Not Found | Booking ID doesn't exist | Use valid booking ID |
| 400 Bad Request | Malformed JSON/Body | Check request format |
| Timeout | Slow network | Increase timeout to 5000ms |

---

**Version:** 1.0 | **Last Updated:** June 2026
