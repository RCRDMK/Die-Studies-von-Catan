package de.uol.swp.server.lobby;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.dto.LobbyDTO;
import de.uol.swp.common.user.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Manages creation, deletion and storing of lobbies
 * <p>
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.lobby.Lobby
 * @see de.uol.swp.common.lobby.dto.LobbyDTO
 * @since 2019-10-08
 */
public class LobbyManagement {

    private final Map<String, Lobby> lobbies = new HashMap<>();

    /**
     * Creates a new lobby and adds it to the list
     * <p>
     *
     * @param name  the name of the lobby to create
     * @param owner the user who wants to create a lobby
     * @throws IllegalArgumentException name already taken
     * @implNote the primary key of the lobbies is the name therefore the name has
     * to be unique
     * @author Marco Grawunder
     * @see de.uol.swp.common.user.User
     * @since 2019-10-08
     */
    public void createLobby(String name, User owner) {
        if (lobbies.containsKey(name)) {
            throw new IllegalArgumentException("Lobby name " + name + " already exists!");
        }
        lobbies.put(name, new LobbyDTO(name, owner));
    }

    /**
     * Creates a new protected lobby and adds it to the list
     * <p>
     *
     * @param name  the name of the lobby to create
     * @param owner the user who wants to create a lobby
     * @throws IllegalArgumentException name already taken
     * @implNote the primary key of the lobbies is the name therefore the name has
     * to be unique
     * @author René Meyer
     * @see de.uol.swp.common.user.User
     * @since 2021-06-05
     */
    public void createProtectedLobby(String name, User owner, String password) {
        if (lobbies.containsKey(name)) {
            throw new IllegalArgumentException("Lobby name " + name + " already exists!");
        }
        var lobby = new LobbyDTO(name, owner);
        lobby.setPassword(password);
        lobbies.put(name, lobby);
    }

    /**
     * Deletes lobby with requested name
     * <p>
     *
     * @param name String containing the name of the lobby to delete
     * @throws IllegalArgumentException there exists no lobby with the  requested
     *                                  name
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    public void dropLobby(String name) {
        if (!lobbies.containsKey(name)) {
            throw new IllegalArgumentException("Lobby name " + name + " not found!");
        }
        lobbies.remove(name);
    }

    /**
     * Searches for the lobby with the requested name
     * <p>
     *
     * @param name String containing the name of the lobby to search for
     * @return either empty Optional or Optional containing the lobby
     * @author Marco Grawunder
     * @see Optional
     * @since 2019-10-08
     */
    public Optional<Lobby> getLobby(String name) {
        Lobby lobby = lobbies.get(name);
        if (lobby != null) {
            return Optional.of(lobby);
        }
        return Optional.empty();
    }

    /**
     * getter
     *
     * @return containing a HashMap with lobbies
     * @author Carsten Dekker and Marius Birk
     * @since 2020-04-12
     */
    public Map<String, Lobby> getAllLobbies() {
        return lobbies;
    }
}
