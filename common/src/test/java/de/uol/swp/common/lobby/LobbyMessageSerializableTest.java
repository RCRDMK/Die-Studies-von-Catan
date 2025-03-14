package de.uol.swp.common.lobby;

import org.junit.jupiter.api.Test;

import de.uol.swp.common.SerializationTestHelper;
import de.uol.swp.common.lobby.message.AbstractLobbyMessage;
import de.uol.swp.common.lobby.message.JoinOnGoingGameMessage;
import de.uol.swp.common.lobby.message.LobbyCreatedMessage;
import de.uol.swp.common.lobby.message.LobbyDroppedMessage;
import de.uol.swp.common.lobby.message.LobbySizeChangedMessage;
import de.uol.swp.common.lobby.message.UserJoinedLobbyMessage;
import de.uol.swp.common.lobby.message.UserLeftLobbyMessage;
import de.uol.swp.common.lobby.request.AbstractLobbyRequest;
import de.uol.swp.common.lobby.request.CreateLobbyRequest;
import de.uol.swp.common.lobby.request.JoinOnGoingGameRequest;
import de.uol.swp.common.lobby.request.LobbyJoinUserRequest;
import de.uol.swp.common.lobby.request.LobbyLeaveUserRequest;
import de.uol.swp.common.lobby.request.RetrieveAllLobbiesRequest;
import de.uol.swp.common.lobby.request.RetrieveAllThisLobbyUsersRequest;
import de.uol.swp.common.lobby.response.AllCreatedLobbiesResponse;
import de.uol.swp.common.lobby.response.JoinOnGoingGameResponse;
import de.uol.swp.common.lobby.response.LobbyAlreadyExistsResponse;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.response.lobby.JoinDeletedLobbyResponse;
import de.uol.swp.common.user.response.lobby.LobbyFullResponse;
import de.uol.swp.common.user.response.lobby.WrongLobbyPasswordResponse;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LobbyMessageSerializableTest {

    private static final UserDTO defaultUser = new UserDTO("marco", "marco", "marco@grawunder.de");

    @Test
    void testLobbyMessagesSerializable() {
        assertTrue(
                SerializationTestHelper.checkSerializableAndDeserializable(new CreateLobbyRequest("test", defaultUser),
                        CreateLobbyRequest.class));
        assertTrue(SerializationTestHelper
                .checkSerializableAndDeserializable(new CreateLobbyRequest("test", defaultUser, "testPw"),
                        CreateLobbyRequest.class));
        assertTrue(SerializationTestHelper
                .checkSerializableAndDeserializable(new LobbyJoinUserRequest("test", defaultUser),
                        LobbyJoinUserRequest.class));
        assertTrue(SerializationTestHelper
                .checkSerializableAndDeserializable(new LobbyJoinUserRequest("test", defaultUser, "testPw"),
                        LobbyJoinUserRequest.class));
        assertTrue(SerializationTestHelper
                .checkSerializableAndDeserializable(new LobbyLeaveUserRequest("test", defaultUser),
                        LobbyLeaveUserRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new UserJoinedLobbyMessage(),
                UserJoinedLobbyMessage.class));
        assertTrue(SerializationTestHelper
                .checkSerializableAndDeserializable(new LobbyFullResponse("test"), LobbyFullResponse.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new JoinDeletedLobbyResponse("test"),
                JoinDeletedLobbyResponse.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new UserLeftLobbyMessage(),
                UserLeftLobbyMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new LobbyAlreadyExistsResponse(),
                LobbyAlreadyExistsResponse.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new RetrieveAllLobbiesRequest(),
                RetrieveAllLobbiesRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new AllCreatedLobbiesResponse(),
                AllCreatedLobbiesResponse.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new AbstractLobbyMessage(),
                AbstractLobbyMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new AbstractLobbyRequest(),
                AbstractLobbyRequest.class));
        assertTrue(SerializationTestHelper
                .checkSerializableAndDeserializable(new AbstractLobbyRequest("test", defaultUser),
                        AbstractLobbyRequest.class));
        assertTrue(SerializationTestHelper
                .checkSerializableAndDeserializable(new AbstractLobbyRequest("test", defaultUser, "testPw"),
                        AbstractLobbyRequest.class));
        assertTrue(SerializationTestHelper
                .checkSerializableAndDeserializable(new LobbyCreatedMessage(defaultUser.getUsername(), defaultUser),
                        LobbyCreatedMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new RetrieveAllThisLobbyUsersRequest(),
                RetrieveAllThisLobbyUsersRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new LobbySizeChangedMessage("test"),
                LobbySizeChangedMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new LobbyDroppedMessage("test"),
                LobbyDroppedMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new JoinOnGoingGameMessage(),
                JoinOnGoingGameMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new JoinOnGoingGameRequest(),
                JoinOnGoingGameRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new JoinOnGoingGameResponse(),
                JoinOnGoingGameResponse.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new WrongLobbyPasswordResponse("test"),
                WrongLobbyPasswordResponse.class));
    }
}
