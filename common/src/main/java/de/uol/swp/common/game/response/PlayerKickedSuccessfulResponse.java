package de.uol.swp.common.game.response;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;

import java.util.Objects;

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

    /**
     * getter for User owner
     *
     * @return User owner
     */
    public User getOwner() {
        return owner;
    }

    /**
     * getter for String name
     *
     * @return String name
     */
    public String getName() {
        return name;
    }

    /**
     * getter for String kickedPlayer
     *
     * @return String kickedPlayer
     */
    public String getKickedPlayer() {
        return kickedPlayer;
    }

    /**
     * The default hashCode method to get the objects hash code
     *
     * @return the hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(owner);
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
        PlayerKickedSuccessfulResponse that =
                (PlayerKickedSuccessfulResponse) o;
        return Objects.equals(owner, that.owner);
    }

}
