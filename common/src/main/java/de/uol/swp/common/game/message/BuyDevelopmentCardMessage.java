package de.uol.swp.common.game.message;

/**
 * Message for the BuyDevelopmentCardMessage
 * <p>
 * This message gets sent to the client that sent an BuyDevelopmentCardRequest.
 * It contains the number of the development cards that was left in the deck.
 * <p>
 * enhanced by Anton Nikiforov
 *
 * @author Marius Birk
 * @see de.uol.swp.common.game.request.BuyDevelopmentCardRequest
 * @since 2021-03-31
 * @since 2021-06-13
 */
public class BuyDevelopmentCardMessage extends AbstractGameMessage {
    private int devCardsNumber;

    /**
     * Default Constructor
     *
     * @implNote this constructor is needed for serialization
     * @since 2021-03-31
     */
    public BuyDevelopmentCardMessage() {
    }

    public BuyDevelopmentCardMessage(int devCardsNumber) {
        this.devCardsNumber = devCardsNumber;
    }

    public int getDevCardsNumber() {
        return devCardsNumber;
    }
}
