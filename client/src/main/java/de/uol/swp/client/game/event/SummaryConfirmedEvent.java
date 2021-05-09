package de.uol.swp.client.game.event;


import de.uol.swp.common.user.User;

public class SummaryConfirmedEvent {
    private String gameName;
    private User user;

    /**
     * Constructor
     * <p>
     *
     * @param gameName name of the game
     * @author René Meyer, Sergej Tulnev
     * @since 2021-05-01
     */
    public SummaryConfirmedEvent(String gameName, User user) {
        this.gameName = gameName;
        this.user = user;
    }

    /**
     * Getter for the gameName
     * <p>
     *
     * @return gameName
     * @author René Meyer, Sergej Tulnev
     * @since 2021-05-01
     */
    public String getGameName() {
        return this.gameName;
    }

    /**
     * Getter for the user
     * <p>
     *
     * @return user
     * @author René Meyer, Sergej Tulnev
     * @since 2021-05-08
     */
    public User getUser() {
        return this.user;
    }
}
