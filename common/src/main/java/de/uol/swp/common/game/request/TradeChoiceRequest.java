package de.uol.swp.common.game.request;

import de.uol.swp.common.user.UserDTO;

/**
 * this request informs the server which bidder the seller accepted
 *
 * @author Alexander Losse, Ricardo Mook
 * @since 2021-04-11
 */
public class TradeChoiceRequest extends AbstractGameRequest {

    private String tradeCode;
    private Boolean tradeAccepted;
    //tradePartner is the winning bidder, seller saved in trade, items also saved in trade

    /**
     * constructor
     *
     * @param tradePartner  UserDTO saves which bidder the seller chose
     * @param tradeAccepted Boolean saves if any bid was accepted
     * @param gameName      String the name of the game
     * @param tradeCode     String ID of the trade
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-11
     */
    public TradeChoiceRequest(UserDTO tradePartner, Boolean tradeAccepted, String gameName, String tradeCode) {
        this.user = new UserDTO(tradePartner.getUsername(), "", "");
        this.name = gameName;
        this.tradeCode = tradeCode;
        this.tradeAccepted = tradeAccepted;
    }

    /**
     * default constructor
     *
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-11
     */
    public TradeChoiceRequest() {
    }

    /**
     * getter for String tradeCode
     *
     * @return String tradeCode
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-11
     */
    public String getTradeCode() {
        return tradeCode;
    }

    /**
     * getter for Boolean tradeAccepted
     *
     * @return Boolean tradeAccepted
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-11
     */
    public Boolean getTradeAccepted() {
        return tradeAccepted;
    }

}
