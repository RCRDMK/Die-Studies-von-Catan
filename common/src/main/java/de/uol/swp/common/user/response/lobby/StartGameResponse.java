package de.uol.swp.common.user.response.lobby;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;

import java.util.Objects;

/**
 * Response that is sent to the User who requested to leave the lobby.
 * <p>
 * Contains the user who sent the request initially
 * as well as the name of the lobby that was successfully left
 *
 * @author Marc Hermes
 * @since 2020-12-10
 */

public class StartGameResponse extends AbstractResponseMessage {

    private static final long serialVersionUID = -2343451823425219873L;

    private final User user;

    private String name;

    public StartGameResponse(User user) {
        this.user = user;
    }

    public StartGameResponse(String name, User user) {
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
        StartGameResponse that = (StartGameResponse) o;
        return Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }

}
