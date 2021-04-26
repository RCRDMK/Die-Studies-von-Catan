package de.uol.swp.common.game.dto;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.GameField;
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
    private final ArrayList<User> userArrayList = new ArrayList<User>();
    private User owner;
    private boolean startingTurns = true;
    private boolean countingUp = true;
    private boolean lastPlayerSecondTurn = false;
    private DevelopmentCardDeck developmentCardDeck = new DevelopmentCardDeck();

    private Inventory inventory1;
    private Inventory inventory2;
    private Inventory inventory3;
    private Inventory inventory4;

    private HashMap<String, Trade> tradeList = new HashMap<>();

    /**
     * Constructor
     *
     * @param name    The name the game should have
     * @param creator The user who created the game and therefore shall be the owner
     *
     * @since 2021-01-15
     */
    public GameDTO(String name, User creator) {
        this.name = name;
        this.owner = creator;
        this.users.add(creator);
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

    @Override
    public void setUpUserArrayList() {
        userArrayList.addAll(users);
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
        return overallTurns % users.size();
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
    }

    /**
     * Organizing the opening-phase for the set amount of players.
     *
     * <p>
     * This is checking many different statements for boolean value to evaluate wich players turn is up next. It does
     * this until every player did his move according to the games rules. For n players, the opening-phase goes from
     * player 1 upwards to player n, then player n again and then backwards to player 1. After that, it disables the
     * opening-phase for the rest of the game.
     * </p>
     *
     * @author Pieter Vogt
     * @since 2021-03-30
     */
    @Override
    public void openingPhase() {
        //If the players are in openingphase...

        if (overallTurns == userArrayList.size() - 1 && !lastPlayerSecondTurn) { // 1)... and if the last player did his first turn but he did not use his second turn:
            lastPlayerSecondTurn = true; // now he did.
            countingUp = false; // we count backwards from now on.
            return;
        } else if (overallTurns <= userArrayList.size()) { // 2)... and if we are not at the last player ...
            if (countingUp) { // 2a)... and if we still count up:
                overallTurns++; //count one up.
                return;
            } else { // 2b)... and if we dont count up anymore ...
                if (overallTurns > 0) {  // 2b1) ... and if we did not arrive back at player 1:
                    overallTurns--; // count one down.
                    countingUp = false; // dont count up anymore.
                    return;
                } else
                    startingTurns = false; // 2b2) if we are at player 1 and were already counting backwards, end the openingphase.
            }
        }
    }

    /**
     * Gives the inventory 1-4 a User
     * <p>
     * It gives the inventory 1-4 a User from the userArrayList if its not empty and the user exists in the ArrayList
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
    }

    /**
     * Getter for the Inventory from user
     * <p>
     * It compares the user with the inventory user and returns the inventory from user
     *
     * @param user
     *
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
        return null;
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
    public void setMapGraph(MapGraph mapGraph) {
        this.mapGraph = mapGraph;
    }

    //TODO: This method needs to be deleted as soon as the dependencies to the obsolete classes are fixed!!!
    @Override
    public GameField getGameField() {
        return null;
    }

    /**
     * adds a Trade to the game
     *
     * @see Trade
     * @param trade Trade to be added
     * @param tradeCode String used to identify trade
     * @author Alecander Losse, Ricardo Mook
     * @since 2021-04-13
     */
    @Override
    public void addTrades(Trade trade, String tradeCode){
        tradeList.put(tradeCode,trade);
    }
    /**
     * getter for the HashMap containing the Trades
     *
     * @return HashMap<String, Trade>
     * @author Alecander Losse, Ricardo Mook
     * @since 2021-04-13
     */
    @Override
    public HashMap getTradeList(){
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
    public void removeTrade(String tradeCode){
        tradeList.remove(tradeCode);
    }
}
