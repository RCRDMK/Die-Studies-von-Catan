package de.uol.swp.common.game.response;


import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.dto.GameDTO;
import de.uol.swp.common.message.AbstractResponseMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Response message for the RetrieveAllGamesRequest
 * <p>
 * This message gets sent to the client that sent an RetrieveAllGamesRequest.
 * It contains a List with Game objects of every game currently existing on the
 * server.
 *
 * @author Iskander Yusupov
 * @see de.uol.swp.common.message.AbstractResponseMessage
 * @see de.uol.swp.common.game.request.RetrieveAllGamesRequest
 * @see de.uol.swp.common.game.Game
 * @since 2021-01-15
 */
public class AllCreatedGamesResponse extends AbstractResponseMessage {


    final private ArrayList<GameDTO> games = new ArrayList<>();

    /**
     * Default Constructor
     *
     * @implNote this constructor is needed for serialization
     * @since 2021-01-15
     */

    public AllCreatedGamesResponse() {
    }

    /**
     * Constructor
     * <p>
     * This constructor generates a new List of currently existing games in GameDTO from the given
     * Collection. The significant difference between the two being that the new
     * List contains copies of the GameDTO objects.
     *
     * @param gameCollection Collection of all games currently existing
     * @since 2021-01-15
     */
    public AllCreatedGamesResponse(Collection<Game> gameCollection) {
        for (Game game : gameCollection) {
            this.games.add(new GameDTO(game.getName(), game.getOwner(), "", null));
        }
    }

    /**
     * Getter for the list of the GameDTO
     *
     * @return list of games
     * @since 2021-01-15
     */
    public List<GameDTO> getGameDTOs() {
        return games;
    }
}
