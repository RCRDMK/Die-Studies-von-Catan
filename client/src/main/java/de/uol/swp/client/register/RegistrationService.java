package de.uol.swp.client.register;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * Class that manages the RegistrationPresenter
 *
 * @author Carsten Dekker
 * @since 2021-01-15
 */

public class RegistrationService {

    /**
     * Method called after pressing the Register Button
     * <p>
     * If an E-Mail is not valid, this method will return false.
     *
     * @author Carsten Dekker
     * @see de.uol.swp.client.register.RegistrationPresenter
     * @since 2021-01-15
     */

    public static boolean isValidEmailAddress(String email) {
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException e) {
            return false;
        }
        return true;
    }
}
