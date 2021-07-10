package de.uol.swp.client.account.event;

/**
 * Event used to show the UserSettingsError alert
 * <p>
 * In order to show the UserSettingsError alert using this event, post an instance of it
 * onto the eventBus the SceneManager is subscribed to.
 *
 * @author Carsten Dekker
 * @see de.uol.swp.client.SceneManager
 * @since 2021-04-03
 */
public class UserSettingsErrorEvent {
    private final String message;

    /**
     * Constructor
     *
     * @param message Message containing the cause of the Error
     * @since 2021-04-03
     */
    public UserSettingsErrorEvent(String message) {
        this.message = message;
    }

    /**
     * Gets the error message
     *
     * @return A String containing the error message
     * @since 2021-04-03
     */
    public String getMessage() {
        return message;
    }
}
