package de.uol.swp.common.game.request;

import de.uol.swp.common.game.request.AbstractGameRequest;
import de.uol.swp.common.user.UserDTO;

/**
 * Request sent from a user when the robber was moved and he chose a player to draw a random resource from
 */
public class DrawRandomResourceFromPlayerRequest extends AbstractGameRequest {
    private final String chosenName;
    private final String resource;

    /**
     * Constructor used for serialization
     *
     * @author Marc Hermes
     * @since 2021-05-30
     */
    public DrawRandomResourceFromPlayerRequest() {
        this.chosenName = null;
        this.resource = null;
    }

    /**
     * Constructor
     *
     * @param name the name of the game
     * @param user the user who sent the request
     * @param chosenName the player to draw a random resource from
     * @author Marius Birk
     * @since 2021-05-30
     */
    public DrawRandomResourceFromPlayerRequest(String name, UserDTO user, String chosenName) {
        super(name, user);
        this.chosenName = chosenName;
        this.resource = null;
    }

    /**
     * Constructor for the AI version of the drawRandomResourceFromPlayerMessage
     *
     * @param name       the name of the game
     * @param user       the user who wants to draw a random resource
     * @param chosenName the name of the player from which to draw from
     * @param resource   the name of the resource to draw
     * @author Marc Hermes
     * @since 2021-05-25
     */
    public DrawRandomResourceFromPlayerRequest(String name, UserDTO user, String chosenName, String resource) {
        super(name, user);
        this.chosenName = chosenName;
        this.resource = resource;
    }

    /**
     * Getter for the name of the chosen player
     *
     * @return the String name of the player that was chosen
     * @author Marius Birk
     * @since 2021-05-30
     */
    public String getChosenName() {
        return this.chosenName;
    }

    /**
     * Getter for the resource
     *
     * @return the resource the AI drew from a player
     * @author Marc Hermes
     * @since 2021-05-25
     */
    public String getResource() {
        return this.resource;
    }
}
