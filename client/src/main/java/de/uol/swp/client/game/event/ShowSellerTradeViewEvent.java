package de.uol.swp.client.game.event;

import de.uol.swp.common.user.UserDTO;

public class ShowSellerTradeViewEvent {//TODO JavaDoc

    private String gameName;
    private UserDTO user;
    public ShowSellerTradeViewEvent(UserDTO user, String gameName) {
        this.user = user;
        this.gameName = gameName;
    }

    public String getGameName() {
        return gameName;
    }

    public UserDTO getUser() {
        return user;
    }
}
