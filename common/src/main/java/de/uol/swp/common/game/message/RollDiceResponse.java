package de.uol.swp.common.game.message;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;


public class RollDiceResponse extends AbstractGameMessage{

    private final int eyes;
    private final String name;
    private final User user;


    /**
     * Constructor
     * @since 2019-10-08
     */
    public RollDiceResponse(String name, User user, int eyes) {
        this.name = name;
        this.user = user;
        this.eyes = eyes;
    }

    public String getName() {
        return name;
    }

    public User getUser() {
        return user;
    }

    public int getEyes() {return eyes; }
}
