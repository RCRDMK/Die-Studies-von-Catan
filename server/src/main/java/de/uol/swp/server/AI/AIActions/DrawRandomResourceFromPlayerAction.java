package de.uol.swp.server.AI.AIActions;

import de.uol.swp.common.user.User;

/**
 * The AIAction used for drawing a random resource from a certain player
 *
 * @author Marc Hermes
 * @since 2021-05-25
 */
public class DrawRandomResourceFromPlayerAction extends AIAction {

    private final String playerName;
    private final String resource;

    /**
     * Constructor
     *
     * @param user       the User who this AI represents
     * @param gameName   the name of the Game for which this Action is to be done
     * @param playerName the name of the User from which to draw resources from
     * @param resource   the name of the resource to rob from the player
     * @author Marc Hermes
     * @since 2021-05-25
     */
    public DrawRandomResourceFromPlayerAction(User user, String gameName, String playerName, String resource) {
        super("DrawRandomResourceFromPlayer", user, gameName);
        this.playerName = playerName;
        this.resource = resource;
    }

    /**
     * Getter for the playerName string
     *
     * @return the name of the player from which to draw resources from
     * @author Marc Hermes
     * @since 2021-05-25
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Getter for the resource name
     *
     * @return the String name of the resource to draw
     * @author Marc Hermes
     * @since 2021-05-25
     */
    public String getResource() {
        return resource;
    }
}
