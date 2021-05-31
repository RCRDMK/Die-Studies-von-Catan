
package de.uol.swp.common.game;

import de.uol.swp.common.SerializationTestHelper;
import de.uol.swp.common.game.message.*;
import de.uol.swp.common.game.request.*;
import de.uol.swp.common.game.response.*;
import de.uol.swp.common.game.trade.TradeItem;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameSerializableTest {
    private static final UserDTO defaultUser = new UserDTO("marco", "marco", "marco@grawunder.de");
    private static final ArrayList<Game> defaultCollection = new ArrayList<>();
    private static final ArrayList<String> defaultUserList = new ArrayList<>();
    private static final HashMap<String, Integer> defaultHashMap = new HashMap<>();
    private static final ArrayList<TradeItem> defaultOffer1 = new ArrayList<>();
    private static final ArrayList<ArrayList<TradeItem>> defaultOffer2 = new ArrayList<>();
    private static final UUID defaultUuid = UUID.randomUUID();

    @Test
    void testGameMessagesSerializable() {
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new AbstractGameMessage(defaultUser.getUsername(), defaultUser),
                AbstractGameMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new BuyDevelopmentCardMessage(),
                BuyDevelopmentCardMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new GameCreatedMessage("test", defaultUser, new MapGraph(""), new ArrayList<>(), ""),
                GameCreatedMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new GameDroppedMessage("test"),
                GameDroppedMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new GameSizeChangedMessage("test"),
                GameSizeChangedMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new GameStartedMessage(),
                GameStartedMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new NotEnoughPlayersMessage("test"),
                NotEnoughPlayersMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new ResolveDevelopmentCardMessage(),
                ResolveDevelopmentCardMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new UserLeftGameMessage("test", defaultUser, new ArrayList<>()),
                UserLeftGameMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new NotEnoughRessourcesMessage(),
                NotEnoughRessourcesMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new RollDiceResultMessage(),
                RollDiceResultMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new ChoosePlayerMessage("test", defaultUser, defaultUserList),
                ChoosePlayerMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new MoveRobberMessage("test", defaultUser),
                MoveRobberMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new RobbersNewFieldMessage("test", defaultUser, defaultUuid),
                RobbersNewFieldMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new SuccessfullMovedRobberMessage(defaultUuid),
                SuccessfullMovedRobberMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new TooMuchResourceCardsMessage(),
                TooMuchResourceCardsMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new TradeInformSellerAboutBidsMessage(),
                TradeInformSellerAboutBidsMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new BankResponseMessage(defaultUser, "", "", defaultOffer2),
                BankResponseMessage.class));
    }

    @Test
    void testGameRequestSerializable() {
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new AbstractGameRequest(),
                AbstractGameRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new BuyDevelopmentCardRequest(),
                BuyDevelopmentCardRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new CreateGameRequest("test", defaultUser),
                CreateGameRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new PlayDevelopmentCardRequest(),
                PlayDevelopmentCardRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new PlayerReadyRequest("test", defaultUser, true),
                PlayerReadyRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new RetrieveAllGamesRequest(),
                RetrieveAllGamesRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new RetrieveAllThisGameUsersRequest("test"),
                RetrieveAllThisGameUsersRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new RollDiceRequest("test", defaultUser),
                RollDiceRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new BuyDevelopmentCardRequest(),
                BuyDevelopmentCardRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new ResourcesToDiscardRequest("test", defaultUser, defaultHashMap),
                ResourcesToDiscardRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new ResolveDevelopmentCardMonopolyRequest(),
                ResolveDevelopmentCardMonopolyRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new ResolveDevelopmentCardRequest(),
                ResolveDevelopmentCardRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new ResolveDevelopmentCardRoadBuildingRequest(),
                ResolveDevelopmentCardRoadBuildingRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new ResolveDevelopmentCardYearOfPlentyRequest(),
                ResolveDevelopmentCardYearOfPlentyRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new DrawRandomResourceFromPlayerRequest(),
                DrawRandomResourceFromPlayerRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new TradeItemRequest(),
                TradeItemRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new BankRequest("test", defaultUser, "", ""),
                BankRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new BankBuyRequest("test", defaultUser, "", "", defaultOffer1),
                BankBuyRequest.class));
    }

    @Test
    void testGameResponseSerializable() {
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new AllCreatedGamesResponse(defaultCollection),
                AllCreatedGamesResponse.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new GameAlreadyExistsResponse("test"),
                GameAlreadyExistsResponse.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new NotLobbyOwnerResponse("test"),
                NotLobbyOwnerResponse.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new PlayDevelopmentCardResponse(),
                PlayDevelopmentCardResponse.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new ResolveDevelopmentCardNotSuccessfulResponse(),
                ResolveDevelopmentCardNotSuccessfulResponse.class));
    }
}
