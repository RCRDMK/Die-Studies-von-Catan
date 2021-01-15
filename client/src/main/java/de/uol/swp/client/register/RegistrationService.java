package de.uol.swp.client.register;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class RegistrationService {

    /**
     *
     * <p>
     *
     *
     */

    public static boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException e) {
            result = false;
        }
        return result;
    }
}
