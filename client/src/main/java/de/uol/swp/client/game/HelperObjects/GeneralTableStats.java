package de.uol.swp.client.game.HelperObjects;

/**
 * The type General table stats.
 *
 * @author René Meyer, Sergej Tulnev
 * @see de.uol.swp.client.game.SummaryPresenter
 * @since 2021-05-08
 */
public class GeneralTableStats {
    private final String user;
    private final String achievement;


    /**
     * Instantiates a new General table stats.
     *
     * @param user        the user
     * @param achievement the achievement
     * @author René Meyer, Sergej Tulnev
     * @since 2021-05-08
     */
    public GeneralTableStats(String user, String achievement) {
        this.user = user;
        this.achievement = achievement;
    }

    /**
     * Gets user.
     * <p>
     * Used for the PropertyValueFactory!
     *
     * @return the user
     * @author René Meyer, Sergej Tulnev
     * @since 2021-05-08
     */
    public String getUser() {
        return user;
    }

    /**
     * Gets achievement.
     * <p>
     * Used for the PropertyValueFactory!
     *
     * @return the achievement
     * @author René Meyer, Sergej Tulnev
     * @since 2021-05-08
     */
    public String getAchievement() {
        return achievement;
    }

}
