package de.uol.swp.common.game.request;

import de.uol.swp.common.user.UserDTO;

/**
 * Request sent to the server when a lobby owner wants to kick player
 * <p>
 *
 * @author Iskander Yusupov
 * @see de.uol.swp.common.game.request.AbstractGameRequest
 * @see de.uol.swp.common.user.User
 * @since 2021-06-24
 */
public class KickPlayerRequest extends AbstractGameRequest {
    private final String playerToKick;
    private final boolean toBan;

    /**
     * Default constructor
     * <p>
     *
     * @implNote this constructor is needed for serialization
     * @author Iskander Yusupov
     * @since 2021-06-24
     */
    public KickPlayerRequest() {
        playerToKick = "";
        toBan = false;
    }

    /**
     * Constructor
     * <p>
     *
     * @param gameName     name of the game
     * @param user         lobby/game owner
     * @param playerToKick name of the player that will be kicked
     * @param toBan        boolean for true or false (if true, player will be banned from the game, is false player will be kicked.)
     * @author Iskander Yusupov
     * @since 2021-06-24
     */
    public KickPlayerRequest(String gameName, UserDTO user, String playerToKick, boolean toBan) {
        super(gameName, user);
        this.playerToKick = playerToKick;
        this.toBan = toBan;
    }

    /**
     * Getter for the playerToKick
     * <p>
     *
     * @return String playerToKick is the name of the player that will be kicked
     * @author Iskander Yusupov
     * @since 2021-06-25
     */
    public String getPlayerToKick() {
        return playerToKick;
    }

    /**
     * Getter for the toBan
     * <p>
     *
     * @return boolean toBan defines whether the player will be kicked or banned from the game
     * @author Iskander Yusupov
     * @since 2021-06-25
     */
    public boolean isToBan() {
        return toBan;
    }
}
