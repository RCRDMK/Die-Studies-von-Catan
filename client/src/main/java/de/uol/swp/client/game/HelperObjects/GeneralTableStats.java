package de.uol.swp.client.game.HelperObjects;

public class GeneralTableStats {
    private String user;
    private String achievement;


    public GeneralTableStats(String user, String achievement) {
        this.user = user;
        this.achievement = achievement;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getAchievement() {
        return achievement;
    }

    public void setAchievement(String achievement) {
        this.achievement = achievement;
    }
}
