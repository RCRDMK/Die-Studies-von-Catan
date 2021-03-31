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
    //TODO Hier fehlt die private final developmentCard

    /**
     * Default Constructor
     *
     * @implNote this constructor is needed for serialization
     * @since 2021-03-31
     */
    public BuyDevelopmentCardMessage() {
    }

    //TODO Mit dem entsprechenden Konstruktor mit Karte.
}
