package ee.taltech.inbankbackend.service;

import com.github.vladislavgoltjajev.personalcode.locale.estonia.EstonianPersonalCodeValidator;
import ee.taltech.inbankbackend.config.DecisionEngineConstants;
import ee.taltech.inbankbackend.exceptions.InvalidLoanAmountException;
import ee.taltech.inbankbackend.exceptions.InvalidLoanPeriodException;
import ee.taltech.inbankbackend.exceptions.InvalidPersonalCodeException;
import ee.taltech.inbankbackend.exceptions.NoValidLoanException;
import org.springframework.stereotype.Service;

/**
 * A service class that provides a method for calculating an approved loan amount and period for a customer.
 * The loan amount is calculated based on the customer's credit modifier,
 * which is determined by the last four digits of their ID code.
 */
@Service
public class DecisionEngine {

    // Used to check for the validity of the presented ID code.
    private final EstonianPersonalCodeValidator validator = new EstonianPersonalCodeValidator();
    private int creditModifier = 0;

    /**
     * Calculates the maximum loan amount and period for the customer based on their ID code,
     * the requested loan amount and the loan period.
     * The loan period must be between 12 and 60 months (inclusive).
     * The loan amount must be between 2000 and 10000€ months (inclusive).
     *
     * @param personalCode ID code of the customer that made the request.
     * @param loanAmount Requested loan amount
     * @param loanPeriod Requested loan period
     * @return A Decision object containing the approved loan amount and period, and an error message (if any)
     * @throws InvalidPersonalCodeException If the provided personal ID code is invalid
     * @throws InvalidLoanAmountException If the requested loan amount is invalid
     * @throws InvalidLoanPeriodException If the requested loan period is invalid
     * @throws NoValidLoanException If there is no valid loan found for the given ID code, loan amount and loan period
     */
    public Decision calculateApprovedLoan(String personalCode, Long loanAmount, int loanPeriod)
            throws InvalidPersonalCodeException, InvalidLoanAmountException, InvalidLoanPeriodException,
            NoValidLoanException {
        try {
            verifyInputs(personalCode, loanAmount, loanPeriod);
        } catch (Exception e) {
            return new Decision(null, null, e.getMessage());
        }

        int outputLoanAmount;
        creditModifier = getCreditModifier(personalCode);

        if (creditModifier == 0) {
            throw new NoValidLoanException("No valid loan found!");
        }

        if (!isValidCreditScore(creditModifier, loanAmount, loanPeriod)) {
            loanAmount = findHighestApprovableLoanAmount(loanAmount, loanPeriod);
            if (loanAmount == null) {
                throw new NoValidLoanException("No valid loan amount found within the allowable loan period range!");
            }
        }

        while (highestValidLoanAmount(loanPeriod) < DecisionEngineConstants.MINIMUM_LOAN_AMOUNT) {
            loanPeriod++;
        }

        if (loanPeriod <= DecisionEngineConstants.MAXIMUM_LOAN_PERIOD) {
            outputLoanAmount = Math.min(DecisionEngineConstants.MAXIMUM_LOAN_AMOUNT, highestValidLoanAmount(loanPeriod));
        } else {
            throw new NoValidLoanException("No valid loan found!");
        }

        return new Decision(outputLoanAmount, loanPeriod, null);
    }

    /**
     * Calculates the largest valid loan for the current credit modifier and loan period.
     *
     * @param loanPeriod Requested loan period
     * @return Largest valid loan amount
     */
    private int highestValidLoanAmount(int loanPeriod) {
        return creditModifier * loanPeriod;
    }

    /**
     * Finds the highest loan amount that can be approved based on the credit score calculation,
     * starting from the requested loan amount and decrementing until a valid loan amount is found
     * or the minimum loan limit is reached. This method ensures that if the initially requested
     * loan amount is too high to be approved, a lower amount that can be approved will be searched
     * for within the acceptable range.
     *
     * @param requestedLoanAmount The loan amount initially requested by the customer.
     * @param loanPeriod The loan period for which the loan amount is being requested.
     * @return The highest approvable loan amount within the range from the requested loan amount.
     */
    private Long findHighestApprovableLoanAmount(Long requestedLoanAmount, int loanPeriod) {
        for (long amount = requestedLoanAmount; amount >= DecisionEngineConstants.MINIMUM_LOAN_AMOUNT; amount--) {
            if (isValidCreditScore(creditModifier, amount, loanPeriod)) {
                return amount;
            }
        }
        return null;
    }

    /**
     * Calculates the credit score of the customer based on the credit modifier, loan amount and loan period.
     *
     * @param creditModifier Credit modifier of the customer
     * @param loanAmount Requested loan amount
     * @param loanPeriod Requested loan period
     * @return Credit score of the customer
     */
    private boolean isValidCreditScore(int creditModifier, long loanAmount, int loanPeriod) {
        float creditScore = (float) (creditModifier / (double) loanAmount) * loanPeriod;
        return creditScore >= DecisionEngineConstants.CREDIT_SCORE_THRESHOLD;
    }

    /**
     * Calculates the credit modifier of the customer to according to the last four digits of their ID code.
     * Debt - 0000...2499
     * Segment 1 - 2500...4999
     * Segment 2 - 5000...7499
     * Segment 3 - 7500...9999
     *
     * @param personalCode ID code of the customer that made the request.
     * @return Segment to which the customer belongs.
     */
    private int getCreditModifier(String personalCode) {
        int segment = Integer.parseInt(personalCode.substring(personalCode.length() - 4));

        if (segment < 2500) {
            return 0;
        } else if (segment < 5000) {
            return DecisionEngineConstants.SEGMENT_1_CREDIT_MODIFIER;
        } else if (segment < 7500) {
            return DecisionEngineConstants.SEGMENT_2_CREDIT_MODIFIER;
        }

        return DecisionEngineConstants.SEGMENT_3_CREDIT_MODIFIER;
    }

    /**
     * Verify that all inputs are valid according to business rules.
     * If inputs are invalid, then throws corresponding exceptions.
     *
     * @param personalCode Provided personal ID code
     * @param loanAmount Requested loan amount
     * @param loanPeriod Requested loan period
     * @throws InvalidPersonalCodeException If the provided personal ID code is invalid
     * @throws InvalidLoanAmountException If the requested loan amount is invalid
     * @throws InvalidLoanPeriodException If the requested loan period is invalid
     */
    private void verifyInputs(String personalCode, Long loanAmount, int loanPeriod)
            throws InvalidPersonalCodeException, InvalidLoanAmountException, InvalidLoanPeriodException {

        if (!validator.isValid(personalCode)) {
            throw new InvalidPersonalCodeException("Invalid personal ID code!");
        }
        if (!(DecisionEngineConstants.MINIMUM_LOAN_AMOUNT <= loanAmount)
                || !(loanAmount <= DecisionEngineConstants.MAXIMUM_LOAN_AMOUNT)) {
            throw new InvalidLoanAmountException("Invalid loan amount!");
        }
        if (!(DecisionEngineConstants.MINIMUM_LOAN_PERIOD <= loanPeriod)
                || !(loanPeriod <= DecisionEngineConstants.MAXIMUM_LOAN_PERIOD)) {
            throw new InvalidLoanPeriodException("Invalid loan period!");
        }

    }
}
