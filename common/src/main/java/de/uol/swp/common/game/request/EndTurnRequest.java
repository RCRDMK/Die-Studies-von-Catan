package de.uol.swp.common.game.request;


import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

/**
 * Request sent to the server when the Player wants to end his Turn.
 *
 * @author Pieter Vogt
 * @see AbstractGameRequest
 * @see User
 * @since 2021-03-26
 */
public class EndTurnRequest extends AbstractGameRequest {

    public EndTurnRequest(String gameName, UserDTO user) {
        super(gameName, user);
    }

}
