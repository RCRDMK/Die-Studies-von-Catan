package de.uol.swp.server.cheat;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uol.swp.common.chat.RequestChatMessage;
import de.uol.swp.common.game.request.RollDiceRequest;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.game.GameService;
import de.uol.swp.server.usermanagement.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

@SuppressWarnings("UnstableApiUsage")
@Singleton
public class CheatService extends AbstractService {
    private static final Logger LOG = LogManager.getLogger(UserService.class);

    private final ArrayList<String> cheatList = new CheatList().get();
    private final GameService gameService;

    @Inject
    public CheatService(GameService gameService, EventBus bus) {
        super(bus);
        this.gameService = gameService;
    }

    public void parseExecuteCheat(RequestChatMessage cheatMessage) {
        // parse Cheats and execute
        var cheatPrefix = cheatMessage.getMessage().split("\\s")[0];
        if (cheatPrefix.equals("roll")) {
            var cheatArgument = cheatMessage.getMessage().split("\\s")[1];
            if (cheatMessage.getSession().isPresent()) {
                var session = cheatMessage.getSession().get();
                var user = session.getUser();
                //@ToDo: Get lobby name for rollDiceRequest oder harcoded Game name??
                var rollDiceRequest = new RollDiceRequest("test", user, Integer.parseInt(cheatArgument));
                gameService.onRollDiceRequest(rollDiceRequest);
            }
        }
    }


    public boolean isCheat(RequestChatMessage cheatMessage) {
        for (String cheatCode : cheatList) {
            var cheatPrefix = cheatMessage.getMessage().split("\\s")[0];
            if (cheatPrefix.equals(cheatCode)) {
                try {
                    var cheatArgument = cheatMessage.getMessage().split("\\s")[1];
                    return true;
                } catch (ArrayIndexOutOfBoundsException e) {
                    LOG.debug("Cheatcode invalid, argument missing");
                    return false;
                }
            }
        }
        return false;
    }
}
