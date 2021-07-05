package de.uol.swp.common.game.response;

import java.util.Objects;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;

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

    public GameLeftSuccessfulResponse(User user) {
        this.user = user;
    }

    public GameLeftSuccessfulResponse(String name, User user) {
        this.user = user;
        this.name = name;
    }


    public User getUser() {
        return user;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        GameLeftSuccessfulResponse that =
                (GameLeftSuccessfulResponse) o;
        return Objects.equals(user, that.user);
    }

}
