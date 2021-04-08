package de.uol.swp.server.game;


import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.GameField;
import de.uol.swp.common.game.dto.GameDTO;
import de.uol.swp.common.user.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Manages starting, deletion and storing of games
 * <p>
 *
 * @author Iskander Yusupov
 * @see de.uol.swp.common.game.Game
 * @see de.uol.swp.common.game.dto.GameDTO
 * @see de.uol.swp.server.game.AbstractGameManagement
 * @since 2021-01-15
 */

public class GameManagement extends AbstractGameManagement {
    private final Map<String, Game> games = new HashMap<>();

    /**
     * Creates a new game and adds it to the list
     * <p>
     * A new GameField is created and stored in the GameDTO object. Also, the round gets set to 0, so the first player
     * can make his first turn.
     * <p>
     * enhanced by Pieter Vogt, Marc Hermes - 2021-03-13 enhanced by Pieter Vogt, 2021-03-26
     *
     * @param name  the name of the game to create
     * @param owner the user who wants to create a game
     * @author Iskander Yusupov
     * @see de.uol.swp.common.user.User
     * @see de.uol.swp.common.game.GameField
     * @since 2021-01-15
     */
    @Override
    public void createGame(String name, User owner, String gameFieldVariant) {
        GameDTO game = new GameDTO(name, owner);
        game.setGameField(new GameField(gameFieldVariant));
        games.put(name, game);
    }

    /**
     * Deletes game with requested name
     * <p>
     *
     * @param name String containing the name of the game to delete
     * @throws IllegalArgumentException there exists no game with the  requested
     *                                  name
     * @author Iskander Yusupov
     * @since 2021-01-15
     */
    @Override
    public void dropGame(String name) {
        if (!games.containsKey(name)) {
            throw new IllegalArgumentException("Game name " + name + " not found!");
        }
        games.remove(name);
    }

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
    @Override
    public Optional<Game> getGame(String name) {
        Game game = games.get(name);
        if (game != null) {
            return Optional.of(game);
        }
        return Optional.empty();
    }

    /**
     * Searches for all games
     *
     * @return containing a HashMap with games
     * @author Iskander Yusupov
     * @since 2021-01-15
     */
    @Override
    public Map<String, Game> getAllGames() {
        return games;
    }
}
