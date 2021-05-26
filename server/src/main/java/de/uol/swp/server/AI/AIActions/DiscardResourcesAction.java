package de.uol.swp.server.AI.AIActions;

import de.uol.swp.common.user.User;

import java.util.HashMap;

/**
 * The AIAction used to discardResources for the robber
 *
 * @author Marc Hermes
 * @since 2021-05-18
 */
public class DiscardResourcesAction extends AIAction {

    private final HashMap<String, Integer> resourcesToDiscard;

    /**
     * Constructor
     *
     * @param user               the User who this AI represents
     * @param gameName           the name of the Game for which this Action is to be done
     * @param resourcesToDiscard the HashMap with the resources that the AI wishes to discard
     * @author Marc Hermes
     * @since 2021-05-08
     */
    public DiscardResourcesAction(User user, String gameName, HashMap<String, Integer> resourcesToDiscard) {
        super("DiscardResources", user, gameName);
        this.resourcesToDiscard = resourcesToDiscard;
    }

    /**
     * Getter for the HasMap of resources to discard
     *
     * @return the HashMap holding the resources to discard
     * @author Marc Hermes
     * @since 2021-05-18
     */
    public HashMap<String, Integer> getResourcesToDiscard() {
        return resourcesToDiscard;
    }
}
