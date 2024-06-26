package ee.taltech.inbankbackend.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PersonalDataValidatorTest {

    @InjectMocks
    private PersonalDataValidator personalDataValidator;

    private String validPersonalCode;
    private String overagePersonalCode;
    private String underagePersonalCode;

    @BeforeEach
    void setUp() {
        validPersonalCode = "50307172740"; // Age 20
        overagePersonalCode = "34204150099"; // Age 82
        underagePersonalCode = "50610150099"; // Age 17
    }

    @Test
    void testValidAge() {
        assertTrue(personalDataValidator.canReceiveLoan(validPersonalCode));
    }

    @Test
    void testOverage() {
        assertFalse(personalDataValidator.canReceiveLoan(overagePersonalCode));
    }

    @Test
     void testUnderage() {
        assertFalse(personalDataValidator.canReceiveLoan(underagePersonalCode));
    }
}

