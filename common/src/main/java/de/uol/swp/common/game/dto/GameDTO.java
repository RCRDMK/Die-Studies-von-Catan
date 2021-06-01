package de.uol.swp.common.game.dto;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.MapGraph;
import de.uol.swp.common.game.inventory.DevelopmentCardDeck;
import de.uol.swp.common.game.inventory.Inventory;
import de.uol.swp.common.game.trade.Trade;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

import java.util.*;

/**
 * Object to transfer the information of a game
 * <p>
 * This object is used to communicate the current state of games between the server and clients. It contains information
 * about the Name of the game, who owns the game.
 * <p>
 * enhanced by Pieter Vogt 2021-03-26 enhanced by Anton Nikiforov 2021-04-01
 *
 * @author Iskander Yusupov
 * @since 2021-01-15
 */
public class GameDTO implements Game {

    private final String name;
    private final Set<User> users = new TreeSet<>();
    private final int turn = 0; //this points to the index of the user who now makes his turn.
    private MapGraph mapGraph;
    private int overallTurns = 0; //This just counts +1 every time a player ends his turn. (good for Summaryscreen for example)
    private final ArrayList<User> userArrayList = new ArrayList<>();
    private final ArrayList<User> aiUsers = new ArrayList<>();
    private User owner;
    private int amountOfPlayers = 0;
    private final Set<User> usersInLobby;
    private boolean startingTurns = true;
    private int startingPhase = 1;
    private boolean countingUp = true;
    private boolean lastPlayerSecondTurn = false;
    private boolean playedCardThisTurn = false;
    private int lastRolledDiceValue = 0;
    private final DevelopmentCardDeck developmentCardDeck = new DevelopmentCardDeck();
    private final ArrayList<MapGraph.BuildingNode> lastBuildingOfOpeningTurn = new ArrayList<>();

    private Inventory inventoryWithLargestArmy = null;

    private Inventory inventory1;
    private Inventory inventory2;
    private Inventory inventory3;
    private Inventory inventory4;
    private Inventory bankInventory;

    private final HashMap<String, Trade> tradeList = new HashMap<>();
    private String currentCard = "";
    private boolean isTest;
    private boolean rolledDiceThisTurn = false;

    private HashMap<String, Integer> boughtDevCardsThisTurn = new HashMap<>();


    /**
     * Constructor
     *
     * @param name             The name the game should have
     * @param creator          The user who created the game and therefore shall be the owner
     * @param gameFieldVariant The variant that the game field should have
     * @param usersInLobby     The actual users in the lobby
     * @since 2021-01-15
     */
    public GameDTO(String name, User creator, String gameFieldVariant, Set<User> usersInLobby) {
        this.name = name;
        this.owner = creator;
        this.users.add(creator);
        this.usersInLobby = usersInLobby;
        this.mapGraph = new MapGraph(gameFieldVariant);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void joinUser(User user) {
        this.users.add(user);
    }

    @Override
    public void leaveUser(User user) {
        if (users.size() == 1) {
            throw new IllegalArgumentException("Game must contain at least one user!");
        }
        if (users.contains(user)) {
            this.users.remove(user);
            if (this.owner.equals(user)) {
                updateOwner(users.iterator().next());
            }
        }
    }

    @Override
    public void updateOwner(User user) {
        if (!this.users.contains(user)) {
            throw new IllegalArgumentException("User " + user.getUsername() +
                    "not found. Owner must be member of game!");
        }
        this.owner = user;
    }

    @Override
    public User getOwner() {
        return owner;
    }

    @Override
    public Set<User> getUsers() {
        return Collections.unmodifiableSet(users);
    }

    /**
     * Converts the Set of Users into an Arraylist
     *
     * <p>Without conversion of user-set into user-Arraylist, we have no tool to adress a specific user with
     * gamelogic.</p>
     *
     * @return Arraylist of users
     * @author Pieter Vogt
     * @since 2021-03-26
     */
    @Override
    public ArrayList<User> getUsersList() {
        return userArrayList;
    }

    @Override
    public User getUser(int index) {
        return userArrayList.get(index);
    }

    /**
     * Method called when starting the game
     * <p>
     * First puts all users in the lobby into the usersArrayList.
     * Then, if there are less players in the lobby than the lobby owner wanted to play with,
     * the difference will be filled with AI Users.
     * In case the other users in the lobby just didn't want to start the game, AI Users will also play until
     * they decide they want to join.
     *
     * @author Marc Hermes
     * @since 2021-05-27
     */
    @Override
    public void setUpUserArrayList() {
        userArrayList.addAll(usersInLobby);
        int players = userArrayList.size();
        int i = 0;
        while (amountOfPlayers > players) {
            UserDTO aiUser = new UserDTO("KI" + i, "", "", 65+i);
            aiUsers.add(aiUser);
            userArrayList.add(aiUser);
            players++;
            i++;
        }
    }

    /**
     * This returns the index of the player who has the current turn.
     *
     * <p>This can be used to get the currently turning user.</p>
     *
     * @return The user that currently makes his turn.
     * @author Pieter Vogt
     * @since 2021-03-26
     */
    @Override
    public int getTurn() {
        return overallTurns % userArrayList.size();
    }

    /**
     * Returns the number of turnes played in the current game as a whole.
     *
     * <p>Returns the overall turns played. It is primarily used to determine the current active player. This can e.g.
     * also be useful for the summaryscreen after a game in an informative manner.</p>
     *
     * @return Overall number of turnes played in the current game.
     * @author Pieter Vogt
     * @since 2021-03-26
     */
    @Override
    public int getOverallTurns() {
        return overallTurns;
    }

    /**
     * Handling who can make his turn next.
     *
     * <p>This method checks, if the opening-phase is still ongoing. If so, it calls the method for handling the
     * opening-phase. If not, it just increments the number of rounds played.</p>
     *
     * @author Pieter Vogt
     * @since 2021-03-26
     */
    @Override
    public void nextRound() {
        if (startingTurns) {
            openingPhase();
        } else overallTurns++;
        playedCardThisTurn = false;
        rolledDiceThisTurn = false;
        boughtDevCardsThisTurn.clear();
    }

    /**
     * Returns the last built Buildings.
     *
     * <p>Returns a Array List with the last built Buildings</p>
     *
     * @return Returns a Array List with the last built Buildings
     * @author Philip Nitsche
     * @since 2021-04-26
     */
    @Override
    public ArrayList<MapGraph.BuildingNode> getLastBuildingOfOpeningTurn() {
        return lastBuildingOfOpeningTurn;
    }

    /**
     * Organizing the opening-phase for the set amount of players.
     *
     * <p>
     * This is checking many different statements for boolean value to evaluate which players turn is up next. It does
     * this until every player did his move according to the games rules. For n players, the opening-phase goes from
     * player 1 upwards to player n, then player n again and then backwards to player 1. After that, it disables the
     * opening-phase for the rest of the game. It also creates a list of the built buildings from which raw materials
     * are distributed in the opening phase
     * </p>
     *
     * @author Pieter Vogt, Philip Nitsche
     * @since 2021-03-30
     */
    @Override
    public void openingPhase() {
        //If the players are in openingphase...

        if (overallTurns == userArrayList.size() - 1 && !lastPlayerSecondTurn) { // 1)... and if the last player did his first turn but he did not use his second turn:
            lastPlayerSecondTurn = true; // now he did.
            countingUp = false; // we count backwards from now on.
            startingPhase = 2;
            return;
        } else if (overallTurns <= userArrayList.size()) { // 2)... and if we are not at the last player ...
            if (countingUp) { // 2a)... and if we still count up:
                overallTurns++; //count one up.
                startingPhase = 1;
                return;
            } else { // 2b)... and if we dont count up anymore ...
                if (overallTurns > 0) {  // 2b1) ... and if we did not arrive back at player 1:
                    overallTurns--; // count one down.
                    countingUp = false; // dont count up anymore.
                    startingPhase = 2;
                    return;
                } else {
                    startingTurns = false; // 2b2) if we are at player 1 and were already counting backwards, end the openingphase.
                    startingPhase = 0;
                    for (int i = 0; i < userArrayList.size(); i++) {
                        lastBuildingOfOpeningTurn.add(mapGraph.getBuiltBuildings().get(mapGraph.getBuiltBuildings().size() - 1 - i));
                    }
                }
            }
        }
    }

    /**
     * Gives the inventory 1-4 a User and creates the Bank
     * <p>
     * It gives the inventory 1-4 a User from the userArrayList if
     * its not empty and the user exists in the ArrayList.
     * Then it creates the Bank and loads them with resources
     *
     * @author Anton Nikiforov
     * @since 2021-04-01
     */
    @Override
    public void setUpInventories() {
        if (!(userArrayList.isEmpty())) {
            if (userArrayList.size() > 0) inventory1 = new Inventory(userArrayList.get(0));
            if (userArrayList.size() > 1) inventory2 = new Inventory(userArrayList.get(1));
            if (userArrayList.size() > 2) inventory3 = new Inventory(userArrayList.get(2));
            if (userArrayList.size() > 3) inventory4 = new Inventory(userArrayList.get(3));
        }
        bankInventory = new Inventory(new UserDTO("Bank", "password", "rich@man.net"));
        bankInventory.lumber.setNumber(19);
        bankInventory.brick.setNumber(19);
        bankInventory.grain.setNumber(19);
        bankInventory.wool.setNumber(19);
        bankInventory.ore.setNumber(19);
    }

    /**
     * Getter for the Inventory from user
     * <p>
     * It compares the user with the inventory user and returns the inventory from user
     *
     * @param user form the inventory you want
     * @return The Inventory from user
     * @author Anton Nikiforov
     * @see de.uol.swp.common.game.inventory.Inventory
     * @since 2021-04-01
     */
    @Override
    public Inventory getInventory(User user) {
        if (user.equals(inventory1.getUser())) return inventory1;
        if (user.equals(inventory2.getUser())) return inventory2;
        if (user.equals(inventory3.getUser())) return inventory3;
        if (user.equals(inventory4.getUser())) return inventory4;
        if (user.equals(bankInventory.getUser())) return bankInventory;
        return null;
    }

    /**
     * Getter for all inventories as ArrayList
     * <p>
     * Retrieves all inventories from the game to show the stats in summary Screen
     *
     * @return all game inventories
     * @author René Meyer
     * @see ArrayList
     * @see Inventory
     * @since 2021-05-08
     */
    public ArrayList<Inventory> getInventoriesArrayList() {
        ArrayList<Inventory> inventories = new ArrayList<Inventory>();
        var users = this.getUsersList();
        users.forEach((user) -> inventories.add(getInventory(user)));
        return inventories;
    }

    @Override
    public Inventory getBankInventory() {
        return bankInventory;
    }

    @Override
    public DevelopmentCardDeck getDevelopmentCardDeck() {
        return developmentCardDeck;
    }

    @Override
    public MapGraph getMapGraph() {
        return mapGraph;
    }

    @Override
    public boolean isStartingTurns() {
        return startingTurns;
    }

    @Override
    public void setMapGraph(MapGraph mapGraph) {
        this.mapGraph = mapGraph;
    }

    @Override
    public int getStartingPhase() {
        return startingPhase;
    }

    @Override
    public void setStartingPhase(int startingPhase) {
        this.startingPhase = startingPhase;
    }

    /**
     * adds a Trade to the game
     *
     * @param trade     Trade to be added
     * @param tradeCode String used to identify trade
     * @author Alecander Losse, Ricardo Mook
     * @see Trade
     * @since 2021-04-13
     */
    @Override
    public void addTrades(Trade trade, String tradeCode) {
        tradeList.put(tradeCode, trade);
    }

    /**
     * getter for the HashMap containing the Trades
     *
     * @return HashMap<String, Trade>
     * @author Alecander Losse, Ricardo Mook
     * @since 2021-04-13
     */
    @Override
    public HashMap getTradeList() {
        return tradeList;
    }

    /**
     * removes a trade from the game
     *
     * @param tradeCode String used to identify trade
     * @author Alecander Losse, Ricardo Mook
     * @since 2021-04-13
     */
    @Override
    public void removeTrade(String tradeCode) {
        tradeList.remove(tradeCode);
    }

    @Override
    public String getCurrentCard() {
        return this.currentCard;
    }

    @Override
    public void setCurrentCard(String currentCard) {
        this.currentCard = currentCard;
    }

    @Override
    public boolean playedCardThisTurn() {
        return this.playedCardThisTurn;
    }

    @Override
    public void setPlayedCardThisTurn(boolean value) {
        playedCardThisTurn = value;
    }

    @Override
    public void setLastRolledDiceValue(int eyes) {
        this.rolledDiceThisTurn = true;
        this.lastRolledDiceValue = eyes;
    }

    @Override
    public int getLastRolledDiceValue() {
        return lastRolledDiceValue;
    }

    @Override
    public boolean isUsedForTest() {
        return this.isTest;
    }

    @Override
    public void setIsUsedForTest(boolean value) {
        this.isTest = value;
    }

    @Override
    public boolean rolledDiceThisTurn() {
        return this.rolledDiceThisTurn;
    }

    @Override
    public void setAmountOfPlayers(int amount) {
        this.amountOfPlayers = amount;
    }

    @Override
    public Inventory getInventoryWithLargestArmy() {
        return inventoryWithLargestArmy;
    }

    @Override
    public void setInventoryWithLargestArmy(Inventory inventoryWithLargestArmy) {
        this.inventoryWithLargestArmy = inventoryWithLargestArmy;
    }
    /**
     * method used to remember the DevelopmentCards bought in a turn
     * <p>
     * saved in HashMap<String, Integer>String,>boughtDevCardThisTurn
     *
     * @param card   String name of the Card
     * @param amount int amount of the card
     * @author Alexander Losse
     * @since 2021-05-30
     */
    @Override
    public void rememberDevCardBoughtThisTurn(String card, int amount) {
        if (card.equals("Monopoly") || card.equals("Road Building") || card.equals("Year of Plenty") || card.equals("Knight")) {
            boughtDevCardsThisTurn.put(card, boughtDevCardsThisTurn.getOrDefault(card, 0) + amount);
        }
    }

    /**
     * returns HashMap<String, Integer> boughtDevCardsThisTurn
     *
     * @return HashMap<String, Integer> boughtDevCardsThisTurn
     * @author Alexander Losse
     * @since 2021-05-30
     */
    @Override
    public HashMap<String, Integer> getBoughtDevCardsThisTurn() {
        return boughtDevCardsThisTurn;
    }

    /**
     * returns how many Cards of type were bought this turn.
     * <p>
     * returns 0 if Card not in HashMap
     *
     * @param card String name of the card
     * @return int
     * @author Alexander Losse
     * @since 2021-05-30
     */
    @Override
    public int getHowManyCardsOfTypeWereBoughtThisTurn(String card) {
        if (!boughtDevCardsThisTurn.isEmpty() && boughtDevCardsThisTurn.containsKey(card)) {
            return boughtDevCardsThisTurn.get(card);
        } else {
            return 0;
        }
    }

    /**
     * checks if a user can play a development card
     * <p>
     * methods checks if String card is an development card
     * method compares the an development card in inventory of user with cards bought this turn,
     * checks that he he can play an development card that was not bought this turn
     * returns true if successful
     * return false if not
     *
     * @param user User
     * @param card String name of the card
     * @return boolean
     * @author Alexander Losse
     * @since 2021-05-30
     */
    @Override
    public boolean canUserPlayDevCard(User user, String card) {
        if (card.equals("Monopoly") || card.equals("Road Building") || card.equals("Year of Plenty") || card.equals("Knight")) {
            Inventory inventoryDummy = getInventory(user);
            int cardsInInventory = inventoryDummy.getCardStack(card).getNumber();
            int boughtCards = getHowManyCardsOfTypeWereBoughtThisTurn(card);
            return cardsInInventory - boughtCards > 0;
        } else return false;
    }
}
