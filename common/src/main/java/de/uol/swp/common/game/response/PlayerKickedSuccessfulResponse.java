package de.uol.swp.common.game.response;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;

import java.util.Objects;

/**
 * Response that is sent to the User who was requested to be kicked from the game.
 * <p>
 * Contains the user who from the request initially
 * as well as the name of the game
 *
 * @author Iskander Yusupov
 * @since 2021-06-24
 */

public class PlayerKickedSuccessfulResponse extends AbstractResponseMessage {

    private static final long serialVersionUID = -2343451823425219873L;

    private final String kickedPlayer;

    private final User owner;

    private final String name;


    public PlayerKickedSuccessfulResponse(String name, User owner, String kickedPlayer) {
        this.owner = owner;
        this.name = name;
        this.kickedPlayer = kickedPlayer;
    }


    public User getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public String getKickedPlayer(){
        return kickedPlayer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerKickedSuccessfulResponse that =
                (PlayerKickedSuccessfulResponse) o;
        return Objects.equals(owner, that.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner);
    }

}
