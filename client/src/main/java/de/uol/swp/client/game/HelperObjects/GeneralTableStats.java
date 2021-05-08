package de.uol.swp.client.game.HelperObjects;

/**
 * The type General table stats.
 *
 * @author René Meyer, Sergej Tulnev
 * @see de.uol.swp.client.game.SummaryPresenter
 * @since 2021-05-08
 */
public class GeneralTableStats {
    private String user;
    private String achievement;


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
     *
     * @return the user
     * @author René Meyer, Sergej Tulnev
     * @since 2021-05-08
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets user.
     *
     * @param user the user
     * @author René Meyer, Sergej Tulnev
     * @since 2021-05-08
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * Gets achievement.
     *
     * @return the achievement
     * @author René Meyer, Sergej Tulnev
     * @since 2021-05-08
     */
    public String getAchievement() {
        return achievement;
    }

    /**
     * Sets achievement.
     *
     * @param achievement the achievement
     * @author René Meyer, Sergej Tulnev
     * @since 2021-05-08
     */
    public void setAchievement(String achievement) {
        this.achievement = achievement;
    }
}
