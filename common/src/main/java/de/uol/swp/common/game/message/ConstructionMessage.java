package de.uol.swp.common.game.message;

import de.uol.swp.common.game.MapGraph;
import de.uol.swp.common.game.request.AbstractGameRequest;
import de.uol.swp.common.user.UserDTO;

/**
 * Message to ask for a construction at a certain MapGraphNode in the MapGraph.
 *
 * @author Pieter Vogt
 * @since 2021-04-14
 */
public class ConstructionMessage extends AbstractGameRequest {

    //Fields

    UserDTO user;
    String game;
    MapGraph.MapGraphNode node;

    //Constructor

    /**
     * Constructor for creating a new ConstructionMessage.
     *
     * @param user The player who wants to start construction
     * @param game The game, the user wants to start construction in
     * @param node the point at wich the player wants to construct something
     *
     * @author Pieter Vogt
     * @since 2021-04-14
     */
    public ConstructionMessage(UserDTO user, String game, MapGraph.MapGraphNode node) {
        this.user = user;
        this.game = game;
        this.node = node;
    }

    //Getter Setter

    @Override
    public UserDTO getUser() {
        return user;
    }

    public String getGame() {
        return game;
    }

    public MapGraph.MapGraphNode getNode() {
        return node;
    }
}
