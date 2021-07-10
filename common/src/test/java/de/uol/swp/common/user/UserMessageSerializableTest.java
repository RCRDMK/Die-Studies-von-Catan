package de.uol.swp.common.user;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.uol.swp.common.SerializationTestHelper;
import de.uol.swp.common.user.exception.DropUserExceptionMessage;
import de.uol.swp.common.user.exception.RegistrationExceptionMessage;
import de.uol.swp.common.user.exception.RetrieveUserInformationExceptionMessage;
import de.uol.swp.common.user.exception.UpdateUserExceptionMessage;
import de.uol.swp.common.user.message.UserLoggedInMessage;
import de.uol.swp.common.user.message.UserLoggedOutMessage;
import de.uol.swp.common.user.message.UsersListMessage;
import de.uol.swp.common.user.request.DropUserRequest;
import de.uol.swp.common.user.request.LoginRequest;
import de.uol.swp.common.user.request.LogoutRequest;
import de.uol.swp.common.user.request.RegisterUserRequest;
import de.uol.swp.common.user.request.RetrieveAllOnlineUsersRequest;
import de.uol.swp.common.user.request.RetrieveUserInformationRequest;
import de.uol.swp.common.user.request.UpdateUserMailRequest;
import de.uol.swp.common.user.request.UpdateUserPasswordRequest;
import de.uol.swp.common.user.request.UpdateUserProfilePictureRequest;
import de.uol.swp.common.user.request.UpdateUserRequest;
import de.uol.swp.common.user.response.AllOnlineUsersResponse;
import de.uol.swp.common.user.response.DropUserSuccessfulResponse;
import de.uol.swp.common.user.response.LoginSuccessfulResponse;
import de.uol.swp.common.user.response.RegistrationSuccessfulResponse;
import de.uol.swp.common.user.response.RetrieveUserInformationResponse;
import de.uol.swp.common.user.response.lobby.AllThisLobbyUsersResponse;
import de.uol.swp.common.user.response.lobby.LobbyCreatedSuccessfulResponse;
import de.uol.swp.common.user.response.lobby.LobbyJoinedSuccessfulResponse;
import de.uol.swp.common.user.response.lobby.LobbyLeftSuccessfulResponse;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UserMessageSerializableTest {

    private static final User defaultUser = new UserDTO("marco", "marco", "marco@grawunder.de", 1);

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
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new LoginRequest("name",
                "pass"), LoginRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new LogoutRequest(),
                LogoutRequest.class));
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
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new LobbyCreatedSuccessfulResponse(
                defaultUser), LobbyCreatedSuccessfulResponse.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new LobbyJoinedSuccessfulResponse(
                defaultUser), LobbyJoinedSuccessfulResponse.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new LobbyLeftSuccessfulResponse(
                defaultUser), LobbyLeftSuccessfulResponse.class));
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
        assertTrue(SerializationTestHelper
                .checkSerializableAndDeserializable(new RetrieveUserInformationExceptionMessage("Error"),
                        RetrieveUserInformationExceptionMessage.class));
        assertTrue(SerializationTestHelper
                .checkSerializableAndDeserializable(new RetrieveUserInformationRequest(defaultUser),
                        RetrieveUserInformationRequest.class));
        assertTrue(SerializationTestHelper
                .checkSerializableAndDeserializable(new RetrieveUserInformationResponse(defaultUser),
                        RetrieveUserInformationResponse.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new UpdateUserMailRequest(defaultUser),
                UpdateUserMailRequest.class));
        assertTrue(SerializationTestHelper
                .checkSerializableAndDeserializable(new UpdateUserPasswordRequest(defaultUser, "marco"),
                        UpdateUserPasswordRequest.class));
        assertTrue(SerializationTestHelper
                .checkSerializableAndDeserializable(new UpdateUserProfilePictureRequest(defaultUser),
                        UpdateUserProfilePictureRequest.class));
    }
}
