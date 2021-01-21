package de.uol.swp.common.user.response.game;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;

import java.util.Objects;

/**
 * Response that is sent to the User who requested to create a game.
 * <p>
 * Contains the user who sent the request initially (he is also the owner)
 * as well as the name of the game that was successfully created
 *
 * @author Iskander Yusupov
 * @since 2021-01-15
 */


public class GameCreatedSuccessfulResponse extends AbstractResponseMessage {

    private static final long serialVersionUID = -8113921823425219873L;

    private final User user;

    private String name;

    /**
     * Constructor
     * <p>
     *
     * @author Iskander Yusupov
     * @since 2021-01-15
     */

    public GameCreatedSuccessfulResponse(User user) {
        this.user = user;
    }

    public GameCreatedSuccessfulResponse(String name, User user) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        de.uol.swp.common.user.response.game.GameCreatedSuccessfulResponse that = (de.uol.swp.common.user.response.game.GameCreatedSuccessfulResponse) o;
        return Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }

}
