package de.uol.swp.common.game.message;

import de.uol.swp.common.user.UserDTO;

/**
 * send if user wants to trade the wrong amount of cards.
 * <p>
 * Is a reaction to TradeItemRequest
 *
 * @author Alexander Losse, Ricardo Mook
 * @see de.uol.swp.common.game.request.TradeItemRequest
 * @since 2021-04-25
 */
public class TradeCardErrorMessage extends AbstractGameMessage {

    private String tradeCode;

    /**
     * send if user wants to trade the wrong amount of cards.
     * <p>
     * Is a reaction to TradeItemRequest. creates a new user without a password
     *
     * @param user      UserDTO
     * @param gameName  String
     * @param tradeCode String
     * @author Alexander Losse, Ricardo Mook
     * @see de.uol.swp.common.game.request.TradeItemRequest
     * @since 2021-04-25
     */
    public TradeCardErrorMessage(UserDTO user, String gameName, String tradeCode) {
        this.user = new UserDTO(user.getUsername(), "", "");
        this.name = gameName;
        this.tradeCode = tradeCode;
    }

    public TradeCardErrorMessage() {
    }

    /**
     * getter for String tradeCode
     *
     * @return tradeCode
     * @author Alexander Losse, RIcardo Mook
     * @since 2021-04-25
     */
    public String getTradeCode() {
        return tradeCode;
    }
}
