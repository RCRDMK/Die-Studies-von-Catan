package de.uol.swp.common.lobby.request;

import de.uol.swp.common.user.UserDTO;

/**
 * Request used to try to join a game that has already started
 *
 * @author Marc Hermes
 * @since 2021-05-27
 */
public class JoinOnGoingGameRequest extends AbstractLobbyRequest {

    /**
     * Constructor used for serialization
     *
     * @author Marc Hermes
     * @since 2021-05-027
     */
    public JoinOnGoingGameRequest() {

    }

    /**
     * Constructor
     *
     * @param lobbyName the name of the game the user wants to join
     * @param user      the name of the user who wants to join the game
     * @author Marc Hermes
     * @since 2021-05-27
     */
    public JoinOnGoingGameRequest(String lobbyName, UserDTO user) {
        super(lobbyName, user);
    }

}
