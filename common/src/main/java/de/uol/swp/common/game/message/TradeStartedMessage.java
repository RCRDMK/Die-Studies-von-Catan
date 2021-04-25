package de.uol.swp.common.game.message;

import de.uol.swp.common.user.UserDTO;

/**
 * used to open and initialize the trade window
 *
 * @author Alexander Losse, Ricardo Mook
 * @since 2021-04-25
 */
public class TradeStartedMessage extends AbstractGameMessage {
    private UserDTO user;
    private String game;
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
    public TradeStartedMessage(UserDTO user, String game, String tradeCode) {
        this.user = new UserDTO(user.getUsername(), "", "");
        this.game = game;
        this.tradeCode = tradeCode;
    }

    /**
     * getter for the user
     *
     * @return UserDTO user
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-25
     */
    public UserDTO getUser() {
        return user;
    }

    /**
     * getter for the game name
     *
     * @return String game
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-25
     */
    public String getGame() {
        return game;
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
