package de.uol.swp.common.user;

import de.uol.swp.common.SerializationTestHelper;
import de.uol.swp.common.user.exception.DropUserExceptionMessage;
import de.uol.swp.common.user.exception.RegistrationExceptionMessage;
import de.uol.swp.common.user.exception.RetrieveUserMailExceptionMessage;
import de.uol.swp.common.user.exception.UpdateUserExceptionMessage;
import de.uol.swp.common.user.message.UserLoggedInMessage;
import de.uol.swp.common.user.message.UserLoggedOutMessage;
import de.uol.swp.common.user.message.UsersListMessage;
import de.uol.swp.common.user.request.*;
import de.uol.swp.common.user.response.*;
import de.uol.swp.common.user.response.DropUserSuccessfulResponse;
import de.uol.swp.common.user.response.LoginSuccessfulResponse;
import de.uol.swp.common.user.response.lobby.AllThisLobbyUsersResponse;
import de.uol.swp.common.user.response.lobby.LobbyCreatedSuccessfulResponse;
import de.uol.swp.common.user.response.lobby.LobbyJoinedSuccessfulResponse;
import de.uol.swp.common.user.response.lobby.LobbyLeftSuccessfulResponse;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UserMessageSerializableTest {

    private static final User defaultUser = new UserDTO("marco", "marco", "marco@grawunder.de");

    private static final int SIZE = 10;
    private static final List<String> users = new ArrayList<>();

    static {
        for (int i = 0; i < SIZE; i++) {
            users.add("User" + i);
        }
    }

    @Test
    void testUserMessagesSerializable() {
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new UserLoggedInMessage("test"),
                UserLoggedInMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new UserLoggedOutMessage("test"),
                UserLoggedOutMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new UsersListMessage(users),
                UsersListMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new RegistrationExceptionMessage("Error"),
                RegistrationExceptionMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new LoginSuccessfulResponse(defaultUser),
                LoginSuccessfulResponse.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new LoginRequest("name", "pass"),
                LoginRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new LogoutRequest(), LogoutRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new RegisterUserRequest(defaultUser),
                RegisterUserRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new RetrieveAllOnlineUsersRequest(),
                RetrieveAllOnlineUsersRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new UpdateUserRequest(defaultUser),
                UpdateUserRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new AllOnlineUsersResponse(),
                AllOnlineUsersResponse.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new AllThisLobbyUsersResponse(),
                AllThisLobbyUsersResponse.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new LobbyCreatedSuccessfulResponse(defaultUser),
                LobbyCreatedSuccessfulResponse.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new LobbyJoinedSuccessfulResponse(defaultUser),
                LobbyJoinedSuccessfulResponse.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new LobbyLeftSuccessfulResponse(defaultUser),
                LobbyLeftSuccessfulResponse.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new RegistrationSuccessfulResponse(),
               RegistrationSuccessfulResponse.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new DropUserExceptionMessage("Error"),
                DropUserExceptionMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new DropUserSuccessfulResponse(),
                DropUserSuccessfulResponse.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new DropUserRequest(defaultUser),
                DropUserRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new UpdateUserExceptionMessage("Error"),
                UpdateUserExceptionMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new RetrieveUserMailExceptionMessage("Error"),
                RetrieveUserMailExceptionMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new RetrieveUserMailRequest(defaultUser),
                RetrieveUserMailRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new RetrieveUserMailResponse(defaultUser),
                RetrieveUserMailResponse.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new UpdateUserMailRequest(defaultUser),
                UpdateUserMailRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new UpdateUserPasswordRequest(defaultUser, "marco"),
                UpdateUserPasswordRequest.class));
    }
}
