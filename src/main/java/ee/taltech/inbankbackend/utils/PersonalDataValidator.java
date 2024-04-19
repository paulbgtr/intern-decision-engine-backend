package ee.taltech.inbankbackend.utils;

import com.github.vladislavgoltjajev.personalcode.locale.estonia.EstonianPersonalCodeValidator;
import ee.taltech.inbankbackend.config.DecisionEngineConstants;

import java.time.Period;
import java.time.LocalDate;

/**
 * A util class for validating personal data.
 * Supports validation of Estonian personal codes and checking if a person can receive a loan based on their age.
 */
public class PersonalDataValidator {
    EstonianPersonalCodeValidator estonianPersonalCodeValidator = new EstonianPersonalCodeValidator();

    /**
     * Checks if the provided personal code is a valid Estonian personal code.
     *
     * @param personalCode The personal code to validate
     * @return True if the personal code is valid, false otherwise
     */
    public boolean isValidPersonalCode(String personalCode) {
        return estonianPersonalCodeValidator.isValid(personalCode);
    }

    /**
     * Checks if a person can receive a loan based on their age.
     * The person must be at least 18 years old and no older than the maximum age for receiving a loan.
     *
     * @param personalCode The personal code of the person
     * @return True if the person can receive a loan, false otherwise
     */
    public boolean canReceiveLoan(String personalCode) {
        int age = calculateAge(personalCode);
        int maxAge = getMaxAgeForLoan();
        return age >= DecisionEngineConstants.MINIMUM_AGE && age <= maxAge;
    }

    /**
     * Calculates the maximum age for receiving a loan.
     * The maximum age is calculated based on the expected lifetime of a person and the maximum loan period.
     *
     * @return The maximum age for receiving a loan
     */
    private int getMaxAgeForLoan() {
        int expectedLifetime = 80;
        int maxLoanPeriodInYears = DecisionEngineConstants.MAXIMUM_LOAN_PERIOD / 12;
        return expectedLifetime - maxLoanPeriodInYears;
    }

    /**
     * Calculates the age of a person based on their personal code.
     * Calculates the age according to the format of Estonian and Lithuanian personal codes.
     *
     * @param personalCode The personal code of the person
     * @return The age of the person
     */
    private int calculateAge(String personalCode) {
        int centuryIndicator = Integer.parseInt(personalCode.substring(0, 1));
        int year = Integer.parseInt(personalCode.substring(1, 3));
        int month = Integer.parseInt(personalCode.substring(3, 5));
        int day = Integer.parseInt(personalCode.substring(5, 7));

        if (centuryIndicator == 3 || centuryIndicator == 4) {
            year += 1900;
        } else if (centuryIndicator == 5 || centuryIndicator == 6) {
            year += 2000;
        }

        LocalDate birthDate = LocalDate.of(year, month, day);
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(birthDate, currentDate);

        return period.getYears();
    }
}
