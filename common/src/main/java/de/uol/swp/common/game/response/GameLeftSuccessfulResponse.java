package de.uol.swp.common.game.response;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;

import java.util.Objects;

/**
 * Response that is sent to the User who requested to leave the game.
 * <p>
 * Contains the user who sent the request initially
 * as well as the name of the game that was successfully left
 *
 * @author Iskander Yusupov
 * @since 2021-01-15
 */

public class GameLeftSuccessfulResponse extends AbstractResponseMessage {

    private static final long serialVersionUID = -2343451823425219873L;
    private final User user;
    private String name;

    /**
     * Constructor
     *
     * @param user the user who left the game successful
     * @author Iskander Yusupov
     * @since 2021-01-15
     */
    public GameLeftSuccessfulResponse(User user) {
        this.user = user;
    }

    /**
     * Constructor
     *
     * @param user the user who left the game successful
     * @param name the game which was left
     * @author Iskander Yusupov
     * @since 2021-01-15
     */
    public GameLeftSuccessfulResponse(String name, User user) {
        this.user = user;
        this.name = name;
    }

    /**
     * Getter for the User which left the game
     *
     * @return the user
     * @author Iskander Yusupov
     * @since 2021-01-15
     */
    public User getUser() {
        return user;
    }

    /**
     * Getter for the the game name
     *
     * @return the game name
     * @author Iskander Yusupov
     * @since 2021-01-15
     */
    public String getName() {
        return name;
    }

    /**
     * The default hashCode method to get the objects hash code
     *
     * @return the hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(user);
    }

    /**
     * The default equals method overwritten by Marco
     *
     * @param o the object which is compared with
     * @return true or false if objetcts are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GameLeftSuccessfulResponse that =
                (GameLeftSuccessfulResponse) o;
        return Objects.equals(user, that.user);
    }

}
