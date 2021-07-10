package de.uol.swp.common.game;

import de.uol.swp.common.game.trade.Trade;
import de.uol.swp.common.user.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Interface to unify game objects
 * <p>
 * This is an Interface to allow for multiple types of game objects since it is possible that not every client has to
 * have every information of the game.
 *
 * @author Iskander Yusupov
 * @see de.uol.swp.common.game.dto.GameDTO
 * @since 2021-01-15
 */
public interface Game extends Serializable {

    /**
     * Getter for the game's name
     *
     * @return A String containing the name of the game
     * @author Iskander Yusupov
     * @since 2021-01-15
     */
    String getName();

    /**
     * Changes the owner of the game
     *
     * @param user The user who should be the new owner
     * @author Iskander Yusupov
     * @since 2021-01-15
     */
    void updateOwner(User user);

    /**
     * Getter for the current owner of the game
     *
     * @return A User object containing the owner of the game
     * @author Iskander Yusupov
     * @since 2021-01-15
     */
    User getOwner();

    /**
     * Adds a new user to the game
     *
     * @param user The new user to add to the game
     * @since 2021-01-25
     */
    void joinUser(User user);

    /**
     * Removes an user from the game
     *
     * @param user The user to remove from the game
     * @author Iskander Yusupov
     * @since 2021-01-15
     */
    void leaveUser(User user);

    /**
     * Removes an selected user from the game
     *
     * @param user The selected user to remove from the game
     * @author Iskander Yusupov
     * @since 2021-06-24
     */
    void kickPlayer(User user);

    /**
     * Getter for all users in the game
     *
     * @return A Set containing all user in this game
     * @author Iskander Yusupov
     * @since 2021-01-15
     */
    Set<User> getUsers();

    /**
     * Method used for removing the last user in the game so that
     * the AI can play by itself. This method should only be used for test
     * purposes.
     *
     * @param user the last user to be removed from the game
     * @author Marc Hermes
     * @since 2021-06-06
     */
    void removeUserForTest(User user);

    /**
     * Getter for the games users in a List.
     *
     * <p>Returns the users in the game as a list so we can look for users at certain indices.</p>
     *
     * @return List of Users
     * @author Pieter Vogt
     * @since 2021-03-26
     */
    ArrayList<User> getUsersList();

    /**
     * Getter for the inventories
     *
     * <p>Returns the inventories from all users</p>
     *
     * @return ArrayList from game inventories
     * @author René Meyer
     * @since 2021-05-08
     */
    ArrayList<Inventory> getInventoriesArrayList();

    /**
     * Getter for a single user.
     *
     * <p>This getter returns a single User specified by an index.</p>
     *
     * @return A User object
     * @author Pieter Vogt
     * @since 2021-03-26
     */
    User getUser(int index);

    /**
     * Sets up the Arraylist containing the users.
     *
     * <p>This is used to enable the server to address users with indices. This was not possible with the Set-structure
     * of the Users.</p>
     *
     * @author Pieter Vogt
     * @since 2021-03-26
     */
    void setUpUserArrayList();

    /**
     * Returns an integer, indicating the number of starting phase.
     *
     * @author Kirstin Beyer
     * @since 2021-04-25
     */
    int getStartingPhase();

    /**
     * Returns a number, indicating whose turn it is.
     *
     * <p>The number represents the index inside the playersList, pointing to a certain Player.</p>
     *
     * @author Pieter Vogt
     * @since 2021-03-26
     */
    int getTurn();

    /**
     * Returns the number of total turns played so far.
     * <p>
     * This can be used for the summary screen at the end of a game.
     * </p>
     *
     * @return Number of overall turns played so far
     * @author Pieter Vogt
     * @since 2021-03-26
     */
    int getOverallTurns();

    /**
     * Increments the round number to the next player.
     *
     * @author Pieter Vogt
     * @since 2021-03-26
     */
    void nextRound();

    /**
     * Getter for the last Building of the opening turn.
     *
     * @return the last Building of the opening turn
     * @author Philip Nitsche
     */
    ArrayList<MapGraph.BuildingNode> getLastBuildingOfOpeningTurn();

    /**
     * Runs the initial Turn for every player.
     * <p>
     * If the game has 4 players, it lets the players do their turn in the following order: 1, 2, 3, 4, 4, 3, 2, 1.
     * </p>
     *
     * @author Pieter Vogt
     * @since 2021-03-30
     */
    void openingPhase();

    /**
     * Gives the inventory 1-4 a User
     *
     * @author Anton Nikiforov
     * @since 2021-04-01
     */
    void setUpInventories();

    /**
     * Getter for the Inventory from user
     *
     * @param user the User whose inventory to get
     * @return The Inventory from user
     * @author Anton Nikiforov
     * @see Inventory
     * @since 2021-04-01
     */
    Inventory getInventory(User user);

    /**
     * Returns the MapGraph object.
     *
     * @return The logical graph with all Nodes, Hexagons and connections between them.
     * @author Pieter Vogt
     * @see MapGraph
     * @since 2021-04-11
     */
    MapGraph getMapGraph();

    /**
     * Returns a boolean, whether the opening phase is active.
     *
     * <p>This is used to identify the starting round at the beginning of a game.</p>
     *
     * @return boolean value, whether the opening phase is active
     * @author Philip Nitsche
     * @since 2021-04-26
     */
    boolean isStartingTurns();

    /**
     * Puts the parsed MapGraph into the DTO.
     *
     * @param mapGraph the mapGraph to set as the mapGraph of this gameDTO
     * @author Pieter Vogt
     * @see MapGraph
     * @since 2021-04-11
     */
    void setMapGraph(MapGraph mapGraph);

    /**
     * Getter for the inventory of the bank.
     *
     * @return inventory of the bank
     * @author Anton Nikiforov
     */
    Inventory getBankInventory();

    /**
     * Getter for the development card deck.
     *
     * @return development card deck
     * @author Anton Nikiforov
     */
    DevelopmentCardDeck getDevelopmentCardDeck();

    /**
     * adds a Trade to the game
     *
     * @param trade     Trade to be added
     * @param tradeCode String used to identify trade
     * @author Alecander Losse, Ricardo Mook
     * @see Trade
     * @since 2021-04-13
     */
    void addTrades(Trade trade, String tradeCode);

    /**
     * getter for the HashMap containing the Trades
     *
     * @return HashMap<String, Trade>
     * @author Alecander Losse, Ricardo Mook
     * @since 2021-04-13
     */
    HashMap<String, Trade> getTradeList();

    /**
     * removes a trade from the game
     *
     * @param tradeCode String used to identify trade
     * @author Alecander Losse, Ricardo Mook
     * @since 2021-04-13
     */
    void removeTrade(String tradeCode);

    /**
     * getter for the String of the card that is currently being played in the game
     *
     * @return The name of the card currently being played
     * @author Marc Hermes
     * @since 2021-05-03
     */
    String getCurrentCard();

    /**
     * setter for the String of the card that is currently being played in the game
     *
     * @param currentCard the card that is currently being played
     * @author Marc Hermes
     * @since 2021-05-03
     */
    void setCurrentCard(String currentCard);

    /**
     * getter for the boolean value whether or not a card was already played this turn.
     * useful, as only 1 card may be played each turn
     *
     * @return true, if already played one card, false if not
     * @author Marc Hermes
     * @since 2021-05-03
     */
    boolean playedCardThisTurn();

    /**
     * setter for the boolean value whether or not a card was already played this turn.
     *
     * @param value the boolean value being set to
     * @author Marc Hermes
     * @since 2021-05-03
     */
    void setPlayedCardThisTurn(boolean value);

    /**
     * Setter for the current Dice value of the game
     *
     * @param eyes the number that was last rolled
     * @author Marc Hermes
     * @since 2021-05-12
     */
    void setLastRolledDiceValue(int eyes);

    /**
     * Getter for the current Dice value of the game
     *
     * @return the integer value representation of the dice
     * @author Marc Hermes
     * @since 2021-05-12
     */
    int getLastRolledDiceValue();

    /**
     * Getter for the boolean value if this game is used for the JUNIT tests (and thus uses a different AI potentially)
     *
     * @return boolean value, true if yes, false if not
     * @author Marc Hermes
     * @since 2021-05-12
     */
    boolean isUsedForTest();

    /**
     * Getter for the boolean value whether or not the game has finished
     *
     * @return true if the game has finished, false if not
     * @author Marc Hermes
     * @since 2021-06-06
     */
    boolean hasConcluded();

    /**
     * Setter for the boolean value whether or not the game has finished
     *
     * @param value the boolean value to set hasConcluded to
     * @author Marc Hermes
     * @since 2021-06-06
     */
    void setHasConcluded(boolean value);

    /**
     * Setter for the boolean value if this game is used for the JUNIT tests (and thus uses a different AI potentially)
     *
     * @param value the boolean value
     * @author Marc Hermes
     * @since 2021-05-12
     */
    void setIsUsedForTest(boolean value);

    /**
     * Getter for the boolean value showing whether the dice were already rolled this turn or not.
     *
     * @return true if yes, false if not
     * @author Marc Hermes
     * @since 2021-05-14
     */
    boolean rolledDiceThisTurn();

    /**
     * Setter for the minimum amount of players the owner wants to play with in the game
     *
     * @param amount the minimum amount of players
     * @author Marc Hermes
     * @since 2021-05-27
     */
    void setAmountOfPlayers(int amount);

    /**
     * Getter for the largest Army reference
     *
     * @return referenced inventory with the largest army
     */
    Inventory getInventoryWithLargestArmy();

    /**
     * setter for the largest Army reference
     *
     * @param inventoryWithLargestArmy the inventory referencing the inventory with the largest army
     */
    void setInventoryWithLargestArmy(Inventory inventoryWithLargestArmy);

    /**
     * Getter for the longest Road reference
     *
     * @return referenced inventory with the longest road
     */
    Inventory getInventoryWithLongestRoad();

    /**
     * setter for the longest Road reference
     *
     * @param inventoryWithLongestRoad the inventory referencing the inventory with the longest road
     */
    void setInventoryWithLongestRoad(Inventory inventoryWithLongestRoad);


    /**
     * method used to remember the DevelopmentCards bought in a turn
     *
     * @param card   String name of the Card
     * @param amount int amount of the card
     * @author Alexander Losse
     * @since 2021-05-30
     */
    void rememberDevCardBoughtThisTurn(String card, int amount);

    /**
     * getter for how many cards of a type were bought this turn
     *
     * @param card String name of the card
     * @return itn amount of cards
     * @author Alexander Losse
     * @since 2021-05-30
     */
    int getHowManyCardsOfTypeWereBoughtThisTurn(String card);

    /**
     * checks if user can play a development card
     *
     * @param user User
     * @param card String name of the card
     * @return boolean
     * @author Alexander Losse
     * @since 2021-05-30
     */
    boolean canUserPlayDevCard(User user, String card);
}
