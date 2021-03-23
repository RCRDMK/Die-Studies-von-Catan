package de.uol.swp.common.game;

import de.uol.swp.common.SerializationTestHelper;
import de.uol.swp.common.game.message.*;
import de.uol.swp.common.game.request.*;
import de.uol.swp.common.game.response.AllCreatedGamesResponse;
import de.uol.swp.common.game.response.GameAlreadyExistsResponse;
import de.uol.swp.common.game.response.NotLobbyOwnerResponse;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameSerializableTest {
    private static final UserDTO defaultUser = new UserDTO("marco", "marco", "marco@grawunder.de");
    private static final ArrayList<Game> defaultCollection = new ArrayList<>();
    private static final GameField defaultGameField = new GameField();

    @Test
    void testGameMessagesSerializable() {
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new AbstractGameMessage(
                defaultUser.getUsername(), defaultUser), AbstractGameMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new GameCreatedMessage("test",
                defaultUser, defaultGameField), GameCreatedMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new GameDroppedMessage("test"),
                GameDroppedMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new GameSizeChangedMessage("test"),
                GameSizeChangedMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new NotEnoughPlayersMessage("test"),
                NotEnoughPlayersMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new UserLeftGameMessage(
                "test", defaultUser), UserLeftGameMessage.class));
    }

    @Test
    void testGameRequestSerializable() {
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new AbstractGameRequest(
                defaultUser.getUsername(), defaultUser), AbstractGameRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new CreateGameRequest("test",
                defaultUser), CreateGameRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new PlayerReadyRequest("test",
                defaultUser, true), PlayerReadyRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new RetrieveAllGamesRequest(),
                RetrieveAllGamesRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new RetrieveAllThisGameUsersRequest(
                "test"), RetrieveAllThisGameUsersRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new RollDiceRequest("test",
                defaultUser), RollDiceRequest.class));
    }

    @Test
    void testGameResponseSerializable() {
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new AllCreatedGamesResponse(
                defaultCollection), AllCreatedGamesResponse.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new GameAlreadyExistsResponse(
                "test"), GameAlreadyExistsResponse.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new NotLobbyOwnerResponse(
                "test"), NotLobbyOwnerResponse.class));
    }
}
