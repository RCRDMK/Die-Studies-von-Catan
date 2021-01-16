package de.uol.swp.server.game;


import de.uol.swp.common.game.dto.GameDTO;
import de.uol.swp.common.game.Game;
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


    @Override
    public void createGame(String name, User owner) {
        games.put(name, new GameDTO(name, owner));
    }


    @Override
    public void dropGame(String name) {
        if (!games.containsKey(name)) {
            throw new IllegalArgumentException("Game name " + name + " not found!");
        }
        games.remove(name);
    }


    @Override
    public Optional<Game> getGame(String name) {
        Game game = games.get(name);
        if (game != null) {
            return Optional.of(game);
        }
        return Optional.empty();
    }


    @Override
    public Map<String, Game> getAllGames() {
        return games;
    }
}
