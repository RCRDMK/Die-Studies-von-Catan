package de.uol.swp.common.game.request;

import de.uol.swp.common.user.UserDTO;

/**
 * request send to the server to start a Trade
 * @author Alexander Losse, Ricardo Mok
 * @since 2021-04-25
 */
public class TradeStartRequest extends AbstractGameRequest {


    private String tradeCode;

    /**
     * constructor
     *
     * @param user      userDTO
     * @param game      String
     * @param tradeCode String
     * @author Alexander Losse, Ricardo Mok
     * @since 2021-04-25
     */
    public TradeStartRequest(UserDTO user, String game, String tradeCode) {
        this.user = new UserDTO(user.getUsername(), "", "");
        this.name = game;
        this.tradeCode = tradeCode;
    }

    /**
     * getter for the tradeCode
     *
     * @return String tradeCode
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-25
     */
    public String getTradeCode() {
        return tradeCode;
    }
}

