# Conclusion for TICKET-101

## Overview

This document summarizes the work completed under TICKET-101, including the development and testing of the decision engine (frontend). The goal was to implement MVP scope of the decision engine.

## Validation Steps

1. **Testing**:
    - **Unit Tests**: Focused on individual components to verify their correctness in isolation.
    - **Integration Tests**: Ensured that the API works with the frontend as expected.

2. **Code Review**:
    - A thorough review was conducted to ensure code quality and adherence to our coding standards.

## Results

- **Unit Tests**: All unit tests passed successfully, confirming that the individual components function as designed.
- **Code Review**: The code is clean and well-structured, with no major issues identified. However, the widget tree issue should be addressed to prevent potential bugs in the future. Also, there are too many comments of self-explanatory code, which should be removed to improve readability.

## What Was Done Well

1. **Modular Design**: The decision engine was implemented with a modular design, making it easier to maintain and extend in the future.
2. **Exception Handling**: The use of custom exceptions like makes error handling more specific and informative for the end user, improving debugging and user experience.
3. **Fail-Fast Principle**: The code follows the fail-fast principle, throwing exceptions early when validation fails, which helps in identifying issues quickly and preventing further processing.
4. **Documentation**: The intern provided comprehensive JavaDoc comments for each method, describing its purpose, parameters, and exceptions.
5. **Constants Utilization**: The use of a separate constants class (DecisionEngineConstants) helps in managing magic numbers and making the code cleaner and more configurable.

## Areas for Improvement

1. **Single Responsibility Principle (SRP)**: The `calculateApprovedLoan` method is doing too much by handling validation, calculation, and decision logic all in one place. This could be refactored to separate these concerns into distinct methods or classes, adhering more closely to SRP.
2. **Dependency Injection**: Enhance testability and modularity by using dependency injection, particularly for the `EstonianPersonalCodeValidator` and any other service components. This approach decouples the classes from their dependencies. 
3. **Magic Numbers and Constants**: Even though constants are used, there are still some magic numbers present in the code (for example the ranges of `getCreditModifier`) that could be replaced with constants for better readability and maintainability.
4. **Requirements Expectations**: The requirements for the decision engine clearly defined that the engine should consider calculating a credit score and then considering it as a factor in the decision. However, the current implementation does not include this feature.
5. **Error Messages**: On the developer side, the error messages are clear and informative. However, on the user side, the error messages could be more user-friendly and provide guidance on how to correct the issue.

## What Was Fixed

1. **Credit Score Calculation**: The credit score calculation was implemented as part of the MVP scope.
2. **Find Suitable Loan Amount**: The `findHighestApprovableLoanAmount` method was added to the `DecisionEngine` class to calculate the suitable loan amount if credit score is less than 1.
3. **Test Coverage**: Some additional unit tests for the new functionality as well as some edge cases were added to improve test coverage. 

## Conclusion

While the work completed under TICKET-101 has implemented the backend side of the decision engine, one of the critical features, the credit score calculation, was not included in the initial implementation.

The code is well-structured and modular, making it easier to maintain and extend in the future. However, there are areas for improvement, such as adhering more closely to the Single Responsibility Principle, enhancing testability through dependency injection, and improving error messages for end users.

Overall, the work done under TICKET-101 lays a solid foundation for future enhancements and improvements to the decision engine.
