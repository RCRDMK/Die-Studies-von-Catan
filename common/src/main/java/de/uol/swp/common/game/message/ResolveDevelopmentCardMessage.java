package de.uol.swp.common.game.message;

import de.uol.swp.common.user.UserDTO;

/**
 * Message sent from the server when a developmentCard was successfully resolved
 *
 * @author Marc Hermes
 * @since 2021-05-05
 */
public class ResolveDevelopmentCardMessage extends AbstractGameMessage{
    final private String devCard;

    /**
     * Constructor used for serialization
     *
     * @author Marc Hermes
     * @since 2021-05-05
     */
    public ResolveDevelopmentCardMessage() {
        this.devCard = null;
    }

    /**
     * Constructor
     *
     * @param devCard the String name of the developmentCard
     * @param user the user whose developmentCard was resolved
     * @param gameName the name of the game in which the developmentCard was resolved
     * @author Marc Hermes
     * @since 2021-05-05
     */
    public ResolveDevelopmentCardMessage(String devCard, UserDTO user, String gameName) {
        super(gameName,(UserDTO) user.getWithoutPassword());
        this.devCard = devCard;
    }

    /**
     * Getter for the name of the developmentCard
     *
     * @return the String name of the developmentCard
     * @author Marc Hermes
     * @since 2021-05-05
     */
    public String getDevCard() {
        return devCard;
    }
}
