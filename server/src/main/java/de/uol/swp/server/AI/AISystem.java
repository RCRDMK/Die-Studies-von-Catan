package de.uol.swp.server.AI;

import de.uol.swp.server.AI.AIActions.AIAction;

import java.util.ArrayList;

public interface AISystem {

    void endTurn();

    void buyDevelopmentCard();

    void placeStreet();

    void placeTown();

    void placeCity();

    void trade();

    void moveBandit();

    ArrayList<AIAction> doAction(int eyes);
}
