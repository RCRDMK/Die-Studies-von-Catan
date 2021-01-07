package de.uol.swp.common.lobby;

import de.uol.swp.common.SerializationTestHelper;
import de.uol.swp.common.lobby.message.*;
import de.uol.swp.common.lobby.request.*;
import de.uol.swp.common.lobby.response.AllCreatedLobbiesResponse;
import de.uol.swp.common.lobby.response.LobbyAlreadyExistsResponse;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.response.JoinDeletedLobbyResponse;
import de.uol.swp.common.user.response.LobbyFullResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LobbyMessageSerializableTest {

    private static final UserDTO defaultUser = new UserDTO("marco", "marco", "marco@grawunder.de");

    @Test
    void testLobbyMessagesSerializable() {
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new CreateLobbyRequest("test", defaultUser),
                CreateLobbyRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new LobbyJoinUserRequest("test", defaultUser),
                LobbyJoinUserRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new LobbyLeaveUserRequest("test", defaultUser),
                LobbyLeaveUserRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new UserJoinedLobbyMessage("test", defaultUser),
                UserJoinedLobbyMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new LobbyFullResponse("test"), LobbyFullResponse.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new JoinDeletedLobbyResponse("test"),JoinDeletedLobbyResponse.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new UserLeftLobbyMessage("test", defaultUser),
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
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new LobbyCreatedMessage(defaultUser.getUsername(), defaultUser),
                LobbyCreatedMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new RetrieveAllThisLobbyUsersRequest(),
                RetrieveAllThisLobbyUsersRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new LobbySizeChangedMessage("test"),
                LobbySizeChangedMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new LobbyDroppedMessage("test"),
                LobbyDroppedMessage.class));
    }
}
