package de.uol.swp.server.usermanagement;

import java.util.TimerTask;

public class TimerActivUserListTask extends TimerTask {
    @Override
    public void run() {
        ActivUserList.checkActivUser();
    }
}