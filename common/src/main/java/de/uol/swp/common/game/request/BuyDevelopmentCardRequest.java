package de.uol.swp.common.game.request;

import de.uol.swp.common.user.UserDTO;

/**
 * Request sent to the server when a user wants to buy a development card.
 * <p>
 *
 * @author Marius Birk
 * @see de.uol.swp.common.game.request.AbstractGameRequest
 * @see de.uol.swp.common.user.User
 * @since 2021-03-31
 */
public class BuyDevelopmentCardRequest extends AbstractGameRequest {

    /**
     * Default constructor
     * <p>
     *
     * @implNote this constructor is needed for serialization
     * @author Marius Birk
     * @since 2021-03-31
     */
    public BuyDevelopmentCardRequest() {
    }

    /**
     * Constructor
     * <p>
     *
     * @param user User that wants to buy a development card
     * @author Marius Birk
     * @since 2021-03-31
     */
    public BuyDevelopmentCardRequest(UserDTO user, String gameName) {
        setUser(user);
        setName(gameName);
    }
}
