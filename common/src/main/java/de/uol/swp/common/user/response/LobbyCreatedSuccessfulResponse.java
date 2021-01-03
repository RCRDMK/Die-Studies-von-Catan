package de.uol.swp.common.user.response;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;

import java.util.Objects;

/**
 * Response that is sent to the User who requested to create a lobby.
 * <p>
 * Contains the user who sent the request initially (he is also the owner)
 * as well as the name of the lobby that was successfully created
 *
 * @author Marc Hermes
 * @since 2020-11-24
 */


public class LobbyCreatedSuccessfulResponse extends AbstractResponseMessage {

    private static final long serialVersionUID = -8113921823425219873L;

    private final User user;

    private String name;

    /**
     * Constructor
     * <p>
     * @author Marc Hermes
     * @since 2020-11-24
     */

    public LobbyCreatedSuccessfulResponse(User user) {
        this.user = user;
    }

    public LobbyCreatedSuccessfulResponse(String name, User user) {
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
        LobbyCreatedSuccessfulResponse that = (LobbyCreatedSuccessfulResponse) o;
        return Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }

}
