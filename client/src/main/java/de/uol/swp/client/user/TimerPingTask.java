package de.uol.swp.client.user;

import java.util.TimerTask;

abstract class TimerPingTask extends TimerTask {

    public static TimerTask run(String username) {
        UserService.ping(username);
        return null;
    }
}