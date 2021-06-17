package de.uol.swp.client.game;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.client.game.event.SummaryConfirmedEvent;
import de.uol.swp.common.game.dto.StatsDTO;
import de.uol.swp.common.game.request.DrawRandomResourceFromPlayerRequest;
import de.uol.swp.common.game.request.RobbersNewFieldRequest;
import de.uol.swp.common.game.message.TradeEndedMessage;
import de.uol.swp.common.game.request.*;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for the GameService
 *
 * @author Ricardo Mook, Alexander Losse
 * @since 2021-03-05
 */

public class GameServiceTest {

    final EventBus bus = new EventBus();
    UserDTO userDTO = new UserDTO("Peter", "lustig", "peter.lustig@uol.de");
    Object event;
    GameService gameService = new GameService(bus);


    @Subscribe
    void handle(DeadEvent e) {
        this.event = e.getEvent();

    }

    @BeforeEach
    void setUp() {
        event = null;
        bus.register(this);
    }

    @AfterEach
    void tearDown() {
        bus.unregister(this);
    }

    @Test
    public void rollDiceTest() {
        gameService.rollDice("Test", userDTO);

        assertTrue(event instanceof RollDiceRequest);
        assertEquals("Test", ((RollDiceRequest) event).getName());
    }

    @Test
    public void retrieveAllThisGameUsersTest() {
        gameService.retrieveAllThisGameUsers("Test");

        assertTrue(event instanceof RetrieveAllThisGameUsersRequest);
        assertEquals("Test", ((RetrieveAllThisGameUsersRequest) event).getName());
    }

    @Test
    public void leaveGameTest() {
        gameService.leaveGame("Test", userDTO);

        assertTrue(event instanceof GameLeaveUserRequest);
        assertEquals("Test", ((GameLeaveUserRequest) event).getName());
        assertEquals(userDTO.getUsername(), ((GameLeaveUserRequest) event).getUser().getUsername());
    }

    @Test
    public void returnFromSummaryScreenTest() {
        gameService.returnFromSummaryScreen(new StatsDTO("Test", userDTO.getUsername(), 0, 0, new ArrayList<>()), userDTO);

        assertTrue(event instanceof SummaryConfirmedEvent);
        assertEquals("Test", ((SummaryConfirmedEvent) event).getGameName());
        assertEquals(userDTO.getUsername(), ((SummaryConfirmedEvent) event).getUser().getUsername());
    }

    @Test
    public void buyDevelopmentCardTest(){
        gameService.buyDevelopmentCard(userDTO, "Test");

        assertTrue(event instanceof BuyDevelopmentCardRequest);
        assertEquals("Test", ((BuyDevelopmentCardRequest) event).getName());
        assertEquals(userDTO.getUsername(), ((BuyDevelopmentCardRequest) event).getUser().getUsername());
    }

    @Test
    public void constructBuildingTest(){
        gameService.constructBuilding(userDTO, "Test", UUID.randomUUID(), "Settlement");

        assertTrue(event instanceof ConstructionRequest);
        assertEquals("Test", ((ConstructionRequest) event).getName());
        assertEquals(userDTO.getUsername(), ((ConstructionRequest) event).getUser().getUsername());
    }

    @Test
    public void sendItemTest(){
        gameService.sendItem(userDTO, "Test", new ArrayList<>(), "123", new ArrayList<>());

        assertTrue(event instanceof TradeItemRequest);
        assertEquals("Test", ((TradeItemRequest) event).getName());
        assertEquals(userDTO.getUsername(), ((TradeItemRequest) event).getUser().getUsername());
        assertEquals("123", ((TradeItemRequest) event).getTradeCode());
    }

    @Test
    public void sendTradeChoiceTest(){
        gameService.sendTradeChoice(userDTO, true, "Test", "123");

        assertTrue(event instanceof TradeChoiceRequest);
        assertEquals(userDTO.getUsername(), ((TradeChoiceRequest) event).getUser().getUsername());
        assertEquals("Test", ((TradeChoiceRequest) event).getName());
        assertEquals("123", ((TradeChoiceRequest) event).getTradeCode());
    }

    @Test
    public void endTradeBeforeItStartedTest(){
        gameService.endTradeBeforeItStarted("test","123");

        assertTrue(event instanceof TradeEndedMessage);
        assertEquals("123", ((TradeEndedMessage) event).getTradeCode());
    }

    @Test
    public void sendTradeStartedRequestTest(){
        gameService.sendTradeStartedRequest(userDTO, "Test", "123");

        assertTrue(event instanceof TradeStartRequest);
        assertEquals(userDTO.getUsername(), ((TradeStartRequest) event).getUser().getUsername());
        assertEquals("Test", ((TradeStartRequest) event).getName());
        assertEquals("123", ((TradeStartRequest) event).getTradeCode());
    }

    @Test
    public void movedRobberTest(){
        gameService.movedRobber("Test", userDTO, UUID.randomUUID());

        assertTrue(event instanceof RobbersNewFieldRequest);
        assertEquals("Test", ((RobbersNewFieldRequest) event).getName());
        assertEquals(userDTO.getUsername(), ((RobbersNewFieldRequest) event).getUser().getUsername());
    }

    @Test
    public void drawRandomCardFromPlayerTest(){
        gameService.drawRandomCardFromPlayer("Test", userDTO, "Wool");

        assertTrue(event instanceof DrawRandomResourceFromPlayerRequest);
        assertEquals(userDTO.getUsername(), ((DrawRandomResourceFromPlayerRequest) event).getUser().getUsername());
        assertEquals("Test", ((DrawRandomResourceFromPlayerRequest) event).getName());
    }

    @Test
    public void playDevelopmentCardTest(){
        gameService.playDevelopmentCard(userDTO, "Test", "Monopoly");

        assertTrue(event instanceof PlayDevelopmentCardRequest);
        assertEquals(userDTO.getUsername(), ((PlayDevelopmentCardRequest) event).getUser().getUsername());
        assertEquals("Test", ((PlayDevelopmentCardRequest) event).getName());
    }

    @Test
    public void resolveDevelopmentCardMonopolyTest(){
        gameService.resolveDevelopmentCardMonopoly(userDTO, "Test", "Monopoly", "Wool");

        assertTrue(event instanceof ResolveDevelopmentCardMonopolyRequest);
        assertEquals(userDTO.getUsername(), ((ResolveDevelopmentCardMonopolyRequest) event).getUser().getUsername());
        assertEquals("Test", ((ResolveDevelopmentCardMonopolyRequest) event).getName());
        assertEquals("Monopoly", ((ResolveDevelopmentCardMonopolyRequest) event).getDevCard());
        assertEquals("Wool", ((ResolveDevelopmentCardMonopolyRequest) event).getResource());
    }

    @Test
    public void resolveDevelopmentCardYearOfPlentyTest(){
        gameService.resolveDevelopmentCardYearOfPlenty(userDTO, "Test", "YearOfPlenty", "Wool", "Ore");

        assertTrue(event instanceof ResolveDevelopmentCardYearOfPlentyRequest);
        assertEquals(userDTO.getUsername(), ((ResolveDevelopmentCardYearOfPlentyRequest) event).getUser().getUsername());
        assertEquals("Test", ((ResolveDevelopmentCardYearOfPlentyRequest) event).getName());
        assertEquals("YearOfPlenty", ((ResolveDevelopmentCardYearOfPlentyRequest) event).getDevCard());
        assertEquals("Wool", ((ResolveDevelopmentCardYearOfPlentyRequest) event).getResource1());
        assertEquals("Ore", ((ResolveDevelopmentCardYearOfPlentyRequest) event).getResource2());
    }

    @Test
    public void resolveDevelopmentCardRoadBuildingTest(){
        gameService.resolveDevelopmentCardRoadBuilding(userDTO, "Test", "RoadBuilding", UUID.randomUUID(), UUID.randomUUID());

        assertTrue(event instanceof ResolveDevelopmentCardRoadBuildingRequest);
        assertEquals(userDTO.getUsername(), ((ResolveDevelopmentCardRoadBuildingRequest) event).getUser().getUsername());
        assertEquals("Test", ((ResolveDevelopmentCardRoadBuildingRequest) event).getName());
        assertEquals("RoadBuilding", ((ResolveDevelopmentCardRoadBuildingRequest) event).getDevCard());
    }

    @Test
    public void retrieveAllGamesTest(){
        gameService.retrieveAllGames();

        assertTrue(event instanceof RetrieveAllGamesRequest);
    }
}
