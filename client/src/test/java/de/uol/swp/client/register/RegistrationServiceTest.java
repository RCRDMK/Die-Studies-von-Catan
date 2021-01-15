package de.uol.swp.client.register;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Test Class for the RegistrationService
 *
 * @author Carsten Dekker
 * @since 2021-01-15
 */


public class RegistrationServiceTest {

    String emailAddress = "test@test.de";
    String emailAddress2 = "test";
    String emailAddress3 = "";
    String expectedRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
            "[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
            "A-Z]{2,7}$";

    @Test
    public void emailIsValidTest() {
        assertTrue(emailAddress.matches(expectedRegex));
    }

    @Test
    public void emailIsNotValidTest() {
        assertFalse(emailAddress2.matches(expectedRegex));
        assertFalse(emailAddress3.matches(expectedRegex));
    }
}
