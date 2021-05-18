package de.uol.swp.common.game.message;

import de.uol.swp.common.user.UserDTO;

import java.util.List;

public class ChoosePlayerMessage extends AbstractGameMessage {
    private List<String> userList;

    public ChoosePlayerMessage(String name, UserDTO user, List<String> userList) {
        super(name, user);
        this.userList = userList;
    }

    public List<String> getUserList() {
        return userList;
    }
}
