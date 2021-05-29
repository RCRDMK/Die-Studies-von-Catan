package de.uol.swp.common.game.message;

/**
 * Message for the BuyDevelopmentCardMessage
 * <p>
 * This message gets sent to the client that sent an BuyDevelopmentCardRequest.
 * It contains a development card and the user, which send the request.
 *
 * @author Marius Birk
 * @see de.uol.swp.common.game.request.BuyDevelopmentCardRequest
 * @since 2021-03-31
 */
public class BuyDevelopmentCardMessage extends AbstractGameMessage {
    private String devCard = null;

    /**
     * Default Constructor
     *
     * @implNote this constructor is needed for serialization
     * @since 2021-03-31
     */
    public BuyDevelopmentCardMessage() {
    }

    public BuyDevelopmentCardMessage(String devCard) {
        this.devCard = devCard;
    }

    public String getDevCard() {
        return devCard;
    }
}
