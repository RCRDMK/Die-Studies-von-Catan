package de.uol.swp.common.game.message;

import de.uol.swp.common.game.request.AbstractGameRequest;
import de.uol.swp.common.user.UserDTO;

public class DrawRandomResourceFromPlayerMessage extends AbstractGameRequest {
    private final String chosenName;
    private final String resource;

    public DrawRandomResourceFromPlayerMessage(String name, UserDTO user, String chosenName) {
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
    public DrawRandomResourceFromPlayerMessage(String name, UserDTO user, String chosenName, String resource) {
        super(name, user);
        this.chosenName = chosenName;
        this.resource = resource;
    }

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
