package de.uol.swp.client.game;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.request.RetrieveUserInformationRequest;

public class GameRulesService {

    private final EventBus eventBus;

    /**
     * Constructor
     *
     * @param eventBus The EventBus set in ClientModule
     * @author Sergej Tulnev
     * @see de.uol.swp.client.di.ClientModule
     * @since 2021-05-12
     */
    @Inject
    public GameRulesService(EventBus eventBus) {
        this.eventBus = eventBus;
        this.eventBus.register(this);
    }


    /**
     * Method called after pressing the Game Rules Button in the main menu scene
     * <p>
     * This method creates a new  with the logged in user
     * and post in on the bus.
     *
     * @param user the logged in user
     * @author Sergej Tulnev
     * @see RetrieveUserInformationRequest
     * @see GameRulesPresenter
     * @since 2021-05-12
     */
    public void retrieveUserMail(User user) {
        RetrieveUserInformationRequest retrieveUserInformationRequest = new RetrieveUserInformationRequest(user);
        eventBus.post(retrieveUserInformationRequest);
    }

}
