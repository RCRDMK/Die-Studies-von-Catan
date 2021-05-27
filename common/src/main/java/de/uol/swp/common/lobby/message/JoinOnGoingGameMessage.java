package de.uol.swp.common.lobby.message;

import de.uol.swp.common.game.message.AbstractGameMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

import java.util.ArrayList;
import java.util.Set;

// used to signal everyone that a new user joined the game
public class JoinOnGoingGameMessage extends AbstractGameMessage {

    private final ArrayList<User> users;
    private final Set<User> humans;

    public JoinOnGoingGameMessage(String gameName, UserDTO user, ArrayList<User> users, Set<User> humans) {
        super(gameName, user);
        this.users = users;
        this.humans = humans;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public Set<User> getHumans() {
        return humans;
    }
}
