package de.uol.swp.client.register;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Test class for the RegistrationService
 * <p>
 * This class tests if we get the right boolean from the isValidEmailAddress method.
 *
 * @author Carsten Dekker
 * @since 2021-01-15
 */


public class RegistrationServiceTest {

    String emailAddress = "test@test.de";
    String emailAddress1 = "test@test.de ";
    String emailAddress2 = "test@test.d";
    String emailAddress3 = "";
    String expectedRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    /**
     * This test checks if an email is recognized as correct if it was entered correctly.
     *
     * @author Carsten Dekker
     * @since 2021-01-15
     */

    @Test
    public void emailIsValidTest() {
        Pattern testPat = Pattern.compile(expectedRegex);
        assertTrue(testPat.matcher(emailAddress).matches());
    }

    /**
     * This test checks if an email is recognized as false if it was entered incorrectly.
     *
     * @author Carsten Dekker
     * @since 2021-01-15
     */

    @Test
    public void emailIsNotValidTest() {
        Pattern testPat1 = Pattern.compile(expectedRegex);
        assertFalse(testPat1.matcher(emailAddress1).matches());
        assertFalse(testPat1.matcher(emailAddress2).matches());
        assertFalse(testPat1.matcher(emailAddress3).matches());
    }
}
