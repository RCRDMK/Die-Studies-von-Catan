package de.uol.swp.common.game.request;

import de.uol.swp.common.user.UserDTO;

import java.util.UUID;

/**
 * Message to ask for a construction at a certain MapGraphNode in the MapGraph.
 *
 * @author Pieter Vogt
 * @since 2021-04-14
 */
public class ConstructionRequest extends AbstractGameRequest {

    UUID uuid;
    String typeOfNode;

    /**
     * Constructor for creating a new ConstructionMessage.
     *
     * @param user The player who wants to start construction
     * @param game The game, the user wants to start construction in
     * @param uuid the UUID corresponding to the node at wich the player wants to construct something
     *
     * @author Pieter Vogt
     * @since 2021-04-14
     */
    public ConstructionRequest(UserDTO user, String game, UUID uuid, String typeOfNode) {
        super(game, user);
        this.uuid = uuid;
        this.typeOfNode = typeOfNode;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getTypeOfNode() {
        return typeOfNode;
    }
}
