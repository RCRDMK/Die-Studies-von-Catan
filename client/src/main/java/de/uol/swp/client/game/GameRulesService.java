package de.uol.swp.client.game;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.request.RetrieveUserInformationRequest;

public class GameRulesService {

    private final EventBus eventBus;

    @Inject
    public GameRulesService(EventBus eventBus) {
        this.eventBus = eventBus;
        this.eventBus.register(this);
    }

    public void retrieveUserMail(User user) {
        RetrieveUserInformationRequest retrieveUserInformationRequest = new RetrieveUserInformationRequest(user);
        eventBus.post(retrieveUserInformationRequest);
    }

}
