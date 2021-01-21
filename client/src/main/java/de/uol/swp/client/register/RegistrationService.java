package de.uol.swp.client.register;

import java.util.regex.Pattern;

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
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if(email == null)
            return false;
        return pat.matcher(email).matches();
    }
}
