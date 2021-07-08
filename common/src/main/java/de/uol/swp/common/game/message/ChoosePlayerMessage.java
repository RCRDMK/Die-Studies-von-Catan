package de.uol.swp.common.game.message;

import java.util.List;

import de.uol.swp.common.user.UserDTO;

public class ChoosePlayerMessage extends AbstractGameMessage {
    private final List<String> userList;

    public ChoosePlayerMessage(String name, UserDTO user, List<String> userList) {
        super(name, user);
        this.userList = userList;
    }

    public List<String> getUserList() {
        return userList;
    }
}
