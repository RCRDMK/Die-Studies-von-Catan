package de.uol.swp.server.game;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.dto.GameDTO;
import de.uol.swp.common.user.User;

import java.util.Map;
import java.util.Optional;

/**
 * An interface for all methods of the server game service
 *
 * @author Iskander Yusupov
 * @since 2021-01-15
 */
public interface ServerGameService {

    /**
     * Creates a new game and adds it to the list
     * <p>
     *
     * @param name  the name of the game to create
     * @param owner the user who wants to create a game
     * @implNote the primary key of the games is the name therefore the name has
     * to be unique
     * @author Iskander Yusupov
     * @see de.uol.swp.common.user.User
     * @since 2021-01-15
     */
    void createGame(String name, User owner);


    /**
     * Deletes game with requested name
     * <p>
     *
     * @param name String containing the name of the lobby to delete
     * @throws IllegalArgumentException there exists no lobby with the  requested
     *                                  name
     * @author Iskander Yusupov
     * @since 2021-01-15
     */
    void dropGame(String name);


    /**
     * Searches for the game with the requested name
     * <p>
     *
     * @param name String containing the name of the game to search for
     * @return either empty Optional or Optional containing the game
     * @author Iskander Yusupov
     * @see Optional
     * @since 2021-01-15
     */
    Optional<Game> getGame(String name);

    /**
     * getter
     *
     * @return containing a HashMap with games
     * @author Iskander Yusupov
     * @since 2021-01-15
     */
    Map<String, Game> getAllGames();


}
