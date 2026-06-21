-  Bug Report - Bug 1

Date : June 21, 2024  
Status :  New  
Severity: HIGH  


Summary

Test case `testIntentionalFailure()` is failing because the API returns firstname "John" instead of the expected "INTENTIONAL_FAIL_VALUE".

---

Steps to reproduce :

1. Run test suite: `mvn clean test`
2. Test `NegativeAndWorkflowTests.testIntentionalFailure()` will fail
3. Fails at: `GET /booking/1` endpoint

---

- Expected vs Actual

Expected:
- API Response: firstname = "INTENTIONAL_FAIL_VALUE"
- Test Result:  PASSED

Actual:
- API Response: firstname = "John"
- Test Result: FAILED

- Root cause :
Booking ID 1 in the database has firstname "John", but the test is checking for "INTENTIONAL_FAIL_VALUE". The test assertion is using the wrong expected value.