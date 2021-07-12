package de.uol.swp.common.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import de.uol.swp.common.SerializationTestHelper;
import de.uol.swp.common.game.message.AbstractGameMessage;
import de.uol.swp.common.game.message.BankResponseMessage;
import de.uol.swp.common.game.message.BuyDevelopmentCardMessage;
import de.uol.swp.common.game.message.ChoosePlayerMessage;
import de.uol.swp.common.game.message.GameCreatedMessage;
import de.uol.swp.common.game.message.GameDroppedMessage;
import de.uol.swp.common.game.message.GameStartedMessage;
import de.uol.swp.common.game.message.MoveRobberMessage;
import de.uol.swp.common.game.message.NotEnoughPlayersMessage;
import de.uol.swp.common.game.message.NotEnoughResourcesMessage;
import de.uol.swp.common.game.message.ResolveDevelopmentCardMessage;
import de.uol.swp.common.game.message.RollDiceResultMessage;
import de.uol.swp.common.game.message.SettlementFullyDevelopedMessage;
import de.uol.swp.common.game.message.SuccessfulMovedRobberMessage;
import de.uol.swp.common.game.message.TooMuchResourceCardsMessage;
import de.uol.swp.common.game.message.TradeCardErrorMessage;
import de.uol.swp.common.game.message.TradeEndedMessage;
import de.uol.swp.common.game.message.TradeInformSellerAboutBidsMessage;
import de.uol.swp.common.game.message.TradeOfferInformBiddersMessage;
import de.uol.swp.common.game.message.TradeStartedMessage;
import de.uol.swp.common.game.message.UserLeftGameMessage;
import de.uol.swp.common.game.request.AbstractGameRequest;
import de.uol.swp.common.game.request.BankBuyRequest;
import de.uol.swp.common.game.request.BankRequest;
import de.uol.swp.common.game.request.BuyDevelopmentCardRequest;
import de.uol.swp.common.game.request.CreateGameRequest;
import de.uol.swp.common.game.request.PlayDevelopmentCardRequest;
import de.uol.swp.common.game.request.PlayerReadyRequest;
import de.uol.swp.common.game.request.ResolveDevelopmentCardMonopolyRequest;
import de.uol.swp.common.game.request.ResolveDevelopmentCardRequest;
import de.uol.swp.common.game.request.ResolveDevelopmentCardRoadBuildingRequest;
import de.uol.swp.common.game.request.ResolveDevelopmentCardYearOfPlentyRequest;
import de.uol.swp.common.game.request.ResourcesToDiscardRequest;
import de.uol.swp.common.game.request.RetrieveAllThisGameUsersRequest;
import de.uol.swp.common.game.request.RobbersNewFieldRequest;
import de.uol.swp.common.game.request.RollDiceRequest;
import de.uol.swp.common.game.request.TradeChoiceRequest;
import de.uol.swp.common.game.request.TradeItemRequest;
import de.uol.swp.common.game.request.TradeStartRequest;
import de.uol.swp.common.game.response.AllThisGameUsersResponse;
import de.uol.swp.common.game.response.GameAlreadyExistsResponse;
import de.uol.swp.common.game.response.GameLeftSuccessfulResponse;
import de.uol.swp.common.game.response.NotLobbyOwnerResponse;
import de.uol.swp.common.game.response.PlayDevelopmentCardResponse;
import de.uol.swp.common.game.response.ResolveDevelopmentCardNotSuccessfulResponse;
import de.uol.swp.common.user.UserDTO;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameSerializableTest {
    private static final UserDTO defaultUser = new UserDTO("marco", "marco", "marco@grawunder.de");
    private static final ArrayList<String> defaultUserList = new ArrayList<>();
    private static final HashMap<String, Integer> defaultHashMap = new HashMap<>();
    private static final UUID defaultUuid = UUID.randomUUID();

    @Test
    void testGameMessagesSerializable() {
        assertTrue(SerializationTestHelper
                .checkSerializableAndDeserializable(new AbstractGameMessage(defaultUser.getUsername(), defaultUser),
                        AbstractGameMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new BuyDevelopmentCardMessage(),
                BuyDevelopmentCardMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new GameCreatedMessage(),
                GameCreatedMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new GameDroppedMessage("test"),
                GameDroppedMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new GameStartedMessage(),
                GameStartedMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new NotEnoughPlayersMessage("test"),
                NotEnoughPlayersMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new ResolveDevelopmentCardMessage(),
                ResolveDevelopmentCardMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new UserLeftGameMessage(),
                UserLeftGameMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new NotEnoughResourcesMessage(),
                NotEnoughResourcesMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new RollDiceResultMessage(),
                RollDiceResultMessage.class));
        assertTrue(SerializationTestHelper
                .checkSerializableAndDeserializable(new ChoosePlayerMessage("test", defaultUser, defaultUserList),
                        ChoosePlayerMessage.class));
        assertTrue(
                SerializationTestHelper.checkSerializableAndDeserializable(new MoveRobberMessage("test", defaultUser),
                        MoveRobberMessage.class));
        assertTrue(SerializationTestHelper
                .checkSerializableAndDeserializable(new SuccessfulMovedRobberMessage(defaultUuid),
                        SuccessfulMovedRobberMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new TooMuchResourceCardsMessage(),
                TooMuchResourceCardsMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new TradeStartedMessage(),
                TradeStartedMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new TradeOfferInformBiddersMessage(),
                TradeOfferInformBiddersMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new TradeInformSellerAboutBidsMessage(),
                TradeInformSellerAboutBidsMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new TradeEndedMessage(),
                TradeEndedMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new TradeCardErrorMessage(),
                TradeCardErrorMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new BankResponseMessage(),
                BankResponseMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new SettlementFullyDevelopedMessage(),
                SettlementFullyDevelopedMessage.class));
    }

    @Test
    void testGameRequestSerializable() {
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new AbstractGameRequest(),
                AbstractGameRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new BuyDevelopmentCardRequest(),
                BuyDevelopmentCardRequest.class));
        assertTrue(
                SerializationTestHelper.checkSerializableAndDeserializable(new CreateGameRequest("test", defaultUser),
                        CreateGameRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new PlayDevelopmentCardRequest(),
                PlayDevelopmentCardRequest.class));
        assertTrue(SerializationTestHelper
                .checkSerializableAndDeserializable(new PlayerReadyRequest("test", defaultUser, true),
                        PlayerReadyRequest.class));
        assertTrue(
                SerializationTestHelper.checkSerializableAndDeserializable(new RetrieveAllThisGameUsersRequest("test"),
                        RetrieveAllThisGameUsersRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new RollDiceRequest("test", defaultUser),
                RollDiceRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new BuyDevelopmentCardRequest(),
                BuyDevelopmentCardRequest.class));
        assertTrue(SerializationTestHelper
                .checkSerializableAndDeserializable(new ResourcesToDiscardRequest("test", defaultUser, defaultHashMap),
                        ResourcesToDiscardRequest.class));
        assertTrue(
                SerializationTestHelper.checkSerializableAndDeserializable(new ResolveDevelopmentCardMonopolyRequest(),
                        ResolveDevelopmentCardMonopolyRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new ResolveDevelopmentCardRequest(),
                ResolveDevelopmentCardRequest.class));
        assertTrue(SerializationTestHelper
                .checkSerializableAndDeserializable(new ResolveDevelopmentCardRoadBuildingRequest(),
                        ResolveDevelopmentCardRoadBuildingRequest.class));
        assertTrue(SerializationTestHelper
                .checkSerializableAndDeserializable(new ResolveDevelopmentCardYearOfPlentyRequest(),
                        ResolveDevelopmentCardYearOfPlentyRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new TradeStartRequest(),
                TradeStartRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new TradeItemRequest(),
                TradeItemRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new TradeChoiceRequest(),
                TradeChoiceRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new TradeItemRequest(),
                TradeItemRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new BankRequest(),
                BankRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new BankBuyRequest(),
                BankBuyRequest.class));
        assertTrue(SerializationTestHelper
                .checkSerializableAndDeserializable(new RobbersNewFieldRequest("test", defaultUser, defaultUuid),
                        RobbersNewFieldRequest.class));
    }

    @Test
    void testGameResponseSerializable() {
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new GameAlreadyExistsResponse("test"),
                GameAlreadyExistsResponse.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new NotLobbyOwnerResponse("test"),
                NotLobbyOwnerResponse.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new PlayDevelopmentCardResponse(),
                PlayDevelopmentCardResponse.class));
        assertTrue(SerializationTestHelper
                .checkSerializableAndDeserializable(new ResolveDevelopmentCardNotSuccessfulResponse(),
                        ResolveDevelopmentCardNotSuccessfulResponse.class));
        assertTrue(
                SerializationTestHelper.checkSerializableAndDeserializable(new GameLeftSuccessfulResponse(defaultUser),
                        GameLeftSuccessfulResponse.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new AllThisGameUsersResponse(),
                AllThisGameUsersResponse.class));

    }
}
