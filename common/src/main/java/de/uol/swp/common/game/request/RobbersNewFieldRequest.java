package de.uol.swp.common.game.request;

import de.uol.swp.common.user.UserDTO;

import java.util.UUID;

/**
 * Class responsible for it's requested to move the robber to a new field
 *
 * @author Marius Birk
 */
public class RobbersNewFieldRequest extends AbstractGameRequest {

    UUID newField;

    /**
     * constructor
     *
     * @param name name of the game
     * @param user name of the user moving the robber
     * @param newField ID of the new field where the robber is being moved to
     * @author Marius Birk
     */
    public RobbersNewFieldRequest(String name, UserDTO user, UUID newField) {
        super(name, user);
        this.newField = newField;
    }

    /**
     * getter method to get the new uuid where the robber is being moved to
     *
     * @return the uuid of the new field
     * @author Marius Birk
     */
    public UUID getNewField() {
        return newField;
    }
}
