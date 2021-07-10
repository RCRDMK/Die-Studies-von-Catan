package de.uol.swp.common.user.response.lobby;

import java.util.Objects;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;

/**
 * Response that is sent to the User who requested to join the lobby.
 * <p>
 * Contains the user who sent the request initially
 * as well as the name of the lobby that was successfully joined
 *
 * @author Marc Hermes
 * @since 2020-12-10
 */

public class LobbyJoinedSuccessfulResponse extends AbstractResponseMessage {

    private static final long serialVersionUID = -2343921823425219873L;

    private final User user;

    private String name;

    public LobbyJoinedSuccessfulResponse(User user) {
        this.user = user;
    }

    public LobbyJoinedSuccessfulResponse(String name, User user) {
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
        LobbyJoinedSuccessfulResponse that = (LobbyJoinedSuccessfulResponse) o;
        return Objects.equals(user, that.user);
    }
}
