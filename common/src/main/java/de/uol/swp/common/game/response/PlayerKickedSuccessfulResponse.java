package de.uol.swp.common.game.response;

import java.util.Objects;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;

/**
 * Response that is sent to the User who sent request to kick other player from the game.
 * <p>
 * Contains the user (lobby/game owner) who from the request initially
 * as well as the name of the game and name of the player that was kicked.
 *
 * @author Iskander Yusupov
 * @since 2021-06-24
 */

public class PlayerKickedSuccessfulResponse extends AbstractResponseMessage {

    private static final long serialVersionUID = -2343451823425219873L;

    private final String kickedPlayer;

    private final User owner;

    private final String name;

    /**
     * Constructor
     * <p>
     *
     * @param name         name of the game
     * @param owner        lobby/game owner
     * @param kickedPlayer name of the player that is kicked
     * @author Iskander Yusupov
     * @since 2021-06-25
     */
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

    public String getKickedPlayer() {
        return kickedPlayer;
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        PlayerKickedSuccessfulResponse that =
                (PlayerKickedSuccessfulResponse) o;
        return Objects.equals(owner, that.owner);
    }

}
