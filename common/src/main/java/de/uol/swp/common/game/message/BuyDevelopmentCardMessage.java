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
     * @author Marius Birk
     * @since 2021-03-31
     */
    public BuyDevelopmentCardMessage() {
    }

    /**
     * This constructor is needed to buy a developmentcard.
     * <p>
     * The constructor gets an integer as parameter to inform the client how much development cards are left.
     *
     * @param devCardsNumber
     * @author Marius Birk
     * @since 2021-03-31
     */
    public BuyDevelopmentCardMessage(int devCardsNumber) {
        this.devCardsNumber = devCardsNumber;
    }

    /**
     * This method returns the amount of development cards.
     *
     * @return amount of development cards that are left in the game
     * @author Marius Birk
     * @since 2021-03-31
     */
    public int getDevCardsNumber() {
        return devCardsNumber;
    }
}

