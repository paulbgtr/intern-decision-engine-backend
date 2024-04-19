package ee.taltech.inbankbackend.utils;

import com.github.vladislavgoltjajev.personalcode.locale.estonia.EstonianPersonalCodeValidator;
import ee.taltech.inbankbackend.config.DecisionEngineConstants;

import java.time.Period;
import java.time.LocalDate;

public class PersonalDataValidator {
    EstonianPersonalCodeValidator estonianPersonalCodeValidator = new EstonianPersonalCodeValidator();

    public boolean isValidPersonalCode(String personalCode) {
        return estonianPersonalCodeValidator.isValid(personalCode);
    }

    public boolean canReceiveLoan(String personalCode) {
        int age = calculateAge(personalCode);
        int maxAge = getMaxAgeForLoan();
        return age >= DecisionEngineConstants.MINIMUM_AGE && age <= maxAge;
    }

    private int getMaxAgeForLoan() {
        int expectedLifetime = 80;
        int maxLoanPeriodInYears = DecisionEngineConstants.MAXIMUM_LOAN_PERIOD / 12;
        return expectedLifetime - maxLoanPeriodInYears;
    }

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
