package de.uol.swp.common.game.message;

import de.uol.swp.common.user.UserDTO;

import java.util.List;

/**
 * Message for the ChoosePlayerMessage
 * <p>
 * This message is send from the server to the client to communicate from which players a ressource could be drawn.
 * It contains a list of users.
 * <p>
 *
 * @author Marius Birk
 * @see de.uol.swp.common.game.request.BuyDevelopmentCardRequest
 * @since 2021-04-27
 */
public class ChoosePlayerMessage extends AbstractGameMessage {
    private final List<String> userList;

    /**
     * Constructer that gets a string, a user and a list of strings as parameters.
     *
     * @param name     name of the game or the lobby
     * @param user     user, who sends the message
     * @param userList a list of possible users names
     * @author Marius Birk
     * @since 2021-04-27
     */
    public ChoosePlayerMessage(String name, UserDTO user, List<String> userList) {
        super(name, user);
        this.userList = userList;
    }

    /**
     * getter for the user list
     *
     * @return a list of users names
     * @author Marius Birk
     * @since 2021-04-27
     */
    public List<String> getUserList() {
        return userList;
    }
}
