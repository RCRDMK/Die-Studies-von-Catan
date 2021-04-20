package de.uol.swp.client.game;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.common.game.message.*;
import de.uol.swp.common.game.request.TradeItemRequest;
import de.uol.swp.common.game.trade.TradeItem;
import de.uol.swp.common.user.UserDTO;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;

import java.util.ArrayList;
import java.util.UUID;

/**
 * This class is responsible for managing the Trade popup window.
 * <p>
 * When this class is called, either through a onTrade Action event or through a subscription method, it will depict the
 * the trade window. Depending on which method was called, the depiction varies. Inside the window, you can select what
 * resources you want to trade and in which amount.
 *
 * @author Alexander Losse, Ricardo Mook
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2021-04-07
 */

public class TradePresenter extends AbstractPresenter {//TODO JavaDoc

    public static final String sellerFxml = "/fxml/SellerTradeView.fxml";
    public static final String bidderFxml = "/fxml/SellerTradeView.fxml";

    //TODO JavaDoc und Aufräumen

    //important for the switch cases
    int switchCount = 2;


    @Inject
    GameService gameService;

    UserDTO user;
    String lobby;
    String tradeCode;

    @FXML
    Text oreT;

    @FXML
    Text grainT;

    @FXML
    Text woolT;

    @FXML
    Text brickT;

    @FXML
    Text lumberT;

    @FXML
    public TextField amountInput;

    @FXML
    public TextField secondInput;

    @FXML
    public TextField thirdInput;

    @FXML
    public TextField fourthInput;

    @FXML
    public TextField fifthInput;

    @FXML
    public ChoiceBox<String> ressourceChoice;

    @FXML
    public ChoiceBox<String> secondChoice;

    @FXML
    public ChoiceBox<String> thirdChoice;

    @FXML
    public ChoiceBox<String> fourthChoice;

    @FXML
    public ChoiceBox<String> fifthChoice;

    @FXML
    public Button trade;

    @FXML
    public Button offer;

    @FXML
    public Button add;

    @FXML
    public Button hide;

    @FXML
    public Button bidder1;

    @FXML
    public Button bidder2;

    @FXML
    public Button bidder3;

    @FXML
    public Button noOffer;

    @FXML
    public TextField sellerBid;

    @FXML
    Button acceptOffer1B;

    @FXML
    Button acceptOffer2B;

    @FXML
    Button acceptOffer3B;


    String choice[] = {"What do you want to trade?", "Lumber", "Brick", "Grain", "Wool", "Ore"};

    String whatRessource;
    String whatSecondRessource;
    String whatThirdRessource;
    String whatFourthRessource;
    String whatFifthRessource;

    int amount;
    int secondAmount;
    int thirdAmount;
    int fourthAmount;
    int fifthAmount;


    ArrayList<TradeItem> tradeItems = new ArrayList();


    /**
     * This method is responsible for the correct setup of the UI elements
     *
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-09
     */

    @FXML
    public void onTradePressed(ActionEvent event) {//If the amount textfield has a number from 1 -500 in it and
        // "What do you want to trade?" is not selected, it will create a new tradeitem and add it to the Arraylist
        if (hasNumber(amountInput.getText()) == true && !ressourceChoice.getValue().toString().equals("What do you want to trade?")) {
            StringConverter sc = new NumberStringConverter();
            whatRessource = ressourceChoice.getValue();
            amount = Integer.parseInt(amountInput.getText());
            tradeItems.add(new TradeItem(whatRessource, amount));
            if (secondChoice.isVisible() == true && hasNumber(amountInput.getText()) == true && !secondChoice.getValue().toString().equals("What do you want to trade?")) {
                whatSecondRessource = secondChoice.getValue();
                secondAmount = Integer.parseInt(secondInput.getText());
                tradeItems.add(new TradeItem(whatSecondRessource, secondAmount));
                if (thirdChoice.isVisible() == true && hasNumber(amountInput.getText()) == true && !thirdChoice.getValue().toString().equals("What do you want to trade?")) {
                    whatThirdRessource = thirdChoice.getValue();
                    thirdAmount = Integer.parseInt(thirdInput.getText());
                    tradeItems.add(new TradeItem(whatThirdRessource, thirdAmount));
                    if (fourthChoice.isVisible() == true && hasNumber(amountInput.getText()) == true && !fourthChoice.getValue().toString().equals("What do you want to trade?")) {
                        whatFourthRessource = fourthChoice.getValue();
                        fourthAmount = Integer.parseInt(fourthInput.getText());
                        tradeItems.add(new TradeItem(whatFourthRessource, fourthAmount));
                        if (fifthChoice.isVisible() == true && hasNumber(amountInput.getText()) == true && !fifthChoice.getValue().toString().equals("What do you want to trade?")) {
                            whatFifthRessource = fifthChoice.getValue();
                            fifthAmount = Integer.parseInt(fifthInput.getText());
                            tradeItems.add(new TradeItem(whatFifthRessource, fifthAmount));
                        }
                    }
                }
            }
            gameService.sendSellingItem(user, lobby, tradeItems, tradeCode);
            trade.setDisable(true);
        } else {
            //If the first if clause is not fulfilled the user receives an alarm
            Alert noValidInput = new Alert(Alert.AlertType.CONFIRMATION);
            noValidInput.setContentText("Please only input valid ressources and numbers");
            Button conformation;
            ButtonType ok = new ButtonType("OK", ButtonBar.ButtonData.YES);
            noValidInput.getButtonTypes().setAll(ok);
            conformation = (Button) noValidInput.getDialogPane().lookupButton(ok);
            noValidInput.showAndWait();
            conformation.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    noValidInput.hide();
                }
            });
        }
    }


    @FXML
    public void onAddPressed(ActionEvent event) {
        switch (switchCount) {//add another textfield and dropdown menu to send different ressources in one trade
            case 2:
                secondChoice.setVisible(true);
                secondInput.setVisible(true);
                switchCount++;
                hide.setDisable(false);
                break;
            case 3:
                thirdChoice.setVisible(true);
                thirdInput.setVisible(true);
                switchCount++;
                break;
            case 4:
                fourthChoice.setVisible(true);
                fourthInput.setVisible(true);
                switchCount++;
                break;
            case 5:
                fifthChoice.setVisible(true);
                fifthInput.setVisible(true);
                add.setDisable(true);
                break;
        }


    }


    @FXML
    public void onHidePressed(ActionEvent event) {
        switch (switchCount) {//hide the last added textfield and dropdown menu in case it isn't needed or it the adding was an accident
            case 2:
                secondChoice.setVisible(false);
                secondInput.setVisible(false);
                hide.setDisable(true);
                break;
            case 3:
                thirdChoice.setVisible(false);
                thirdInput.setVisible(false);
                switchCount--;
                break;
            case 4:
                add.setDisable(false);
                fourthChoice.setVisible(false);
                fourthInput.setVisible(false);
                switchCount--;
                break;
            case 5:
                fifthChoice.setVisible(false);
                fifthInput.setVisible(false);
                switchCount--;
                break;
        }
    }

    @FXML
    public void onBidder1Pressed(ActionEvent event) {
        eventBus.post(new TradeEndedMessage(tradeCode));
        resetTradeUiElements();
    }

    @FXML
    public void onBidder2Pressed(ActionEvent event) {
        eventBus.post(new TradeEndedMessage(tradeCode));
        resetTradeUiElements();
    }

    @FXML
    public void onBidder3Pressed(ActionEvent event) {
        eventBus.post(new TradeEndedMessage(tradeCode));
        resetTradeUiElements();
    }

    @FXML
    public void onNoOfferPressed(ActionEvent event) {
        eventBus.post(new TradeEndedMessage(tradeCode));
        resetTradeUiElements();
    }

    /**
     * This method is called from the onTrade action event in the GamePresenter
     * <p>
     * When this method is called, it depicts a trade popup window for the seller. Inside of it he can not only send a
     * request to trade but also decide on an offer made from another player or decide for no offer at all.
     *
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-09
     */

    public void sellerTradePopup() {
        tradeCode = user.getUsername() + UUID.randomUUID().toString();
    }


    @Subscribe
    void onTradeOfferInformBiddersMessage(TradeOfferInformBiddersMessage toibm) {
        if (user == null) {
            tradeCode = toibm.getTradeCode();
            lobby = toibm.getName();
            user = (UserDTO) toibm.getBidder();
            System.out.println(user + " " + tradeCode);
        }
    }

    //opens the trade window for seller
    @Subscribe
    void onTradeStartedMessage(TradeStartedMessage tsm) {
        if (user == null) {
            user = tsm.getUser();
            lobby = tsm.getLobby();
            tradeCode = tsm.getTradeCode();
            System.out.println(user + " " + tradeCode);
        }
    }

    /*
    for(UserDTO bidder: bidder){
        bids.get(bidder);
    }
    */

    @FXML
    Text offerMessage;

    @Subscribe
    void onTradeInformSellerAboutBidsMessage(TradeInformSellerAboutBidsMessage tisabm) {
        if (tisabm.getName().equals(lobby) && tisabm.getTradeCode().equals(tradeCode)) {
            //There's no control structure so far because there will be always three other players even if some of them are AIs
            if (tisabm.getBidders().size() > 0) {
                bidder1.setText("Accept the offer from " + tisabm.getBidders().get(0).getUsername());
                bidder1.setVisible(true);
                if (tisabm.getBidders().size() > 1) {
                    bidder2.setText("Accept the offer from " + tisabm.getBidders().get(1).getUsername());
                    bidder2.setVisible(true);
                    if (tisabm.getBidders().size() > 2) {
                        bidder3.setText("Accept the offer from " + tisabm.getBidders().get(2).getUsername());
                        bidder3.setVisible(true);
                    }
                }
                noOffer.setText("Accepting no offer");
                noOffer.setVisible(true);
            }
        }
        //TODO: Angebot besser darstellen
        offerMessage.setText(tisabm.getBidders().get(0).getUsername() + " offers:");
        for (TradeItem item : tisabm.getBids().get(tisabm.getBidders().get(0))) {
            if (item.getName().equals("Lumber")) {
                lumberT.setText("Lumber " + item.getCount());
            } else if (item.getName().equals("Ore")) {
                oreT.setText("Ore " + item.getCount());
            } else if (item.getName().equals("Wool")) {
                woolT.setText("Wool " + item.getCount());
            } else if (item.getName().equals("Brick")) {
                brickT.setText("Brick " + item.getCount());
            } else if (item.getName().equals("Grain")) {
                grainT.setText("Grain " + item.getCount());
            }
        }

        //bids.setText(tisabm.getBidders().get(0).getUsername() + " offers " + tisabm.getBids().get(tisabm.getBidders().get(0)));

        //TODO: Beim Button Press muss sich das Fenster schließen und Ressourcen müssen ihren Besitzer ändern

    }

    /**
     * This method is called from the subscription method XXX in the GamePresenter
     * <p>
     * When this method is called it depicts the trade window for the bidder. Inside of it the seller can choose which ressources he wants to trade
     *
     * @param user
     * @param lobby
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-11
     */


    //TODO Bids müssen korrekt angezeigt werden

    /**
     * This method is responsible for updating the sellerTradePopup
     *
     * @param tir Message from the server containing the bids from the other players
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-09
     */
    @Subscribe
    private void onTradeItemRequest(TradeItemRequest tir) {
        if (tir.getUser() == user && tir.getName().equals(lobby) && tir.getTradeCode().equals(tradeCode)) {
            //sellerBid.setText("Your offer: " + tir.getTradeItems().toString());
            trade.setDisable(true);
            add.setDisable(true);
        }

    }
    //TODO Bidder dürfen nur ihr eigenes Gebot und das vom Seller sehen

    /**
     * This method is responsible for updating the bidderTradePopup
     * <p>
     * When this methods gets called the trade is over and the seller gets informed if his bid got accepted or not.
     *
     * @param tsm TradeSuccessfulMessage Message from the server containing if the bid from the seller was accepted or not
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-11
     */
    @Subscribe
    private void onTradeSuccessfulMessage(TradeSuccessfulMessage tsm) {
        if (tsm.isTradeSuccessful() == true && user.getUsername().equals(tsm.getUser().getUsername())) {
            Alert bidGotAccepted = new Alert(Alert.AlertType.CONFIRMATION);
            bidGotAccepted.setContentText("Congratulation! Your bid was accepted!");
            Button conformation;
            ButtonType ok = new ButtonType("OK", ButtonBar.ButtonData.YES);
            bidGotAccepted.getButtonTypes().setAll(ok);
            conformation = (Button) bidGotAccepted.getDialogPane().lookupButton(ok);
            bidGotAccepted.showAndWait();
            conformation.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    bidGotAccepted.hide();
                    eventBus.post(new TradeEndedMessage(tradeCode));

                }
            });

        } else {
            Alert bidGotDenied = new Alert(Alert.AlertType.CONFIRMATION);
            bidGotDenied.setContentText("Your bid got denied");
            Button conformation;
            ButtonType ok = new ButtonType("OK", ButtonBar.ButtonData.YES);
            bidGotDenied.getButtonTypes().setAll(ok);
            conformation = (Button) bidGotDenied.getDialogPane().lookupButton(ok);
            bidGotDenied.showAndWait();
            conformation.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    bidGotDenied.hide();
                    eventBus.post(new TradeEndedMessage(tradeCode));

                }
            });
        }
    }

    /**
     * This method checks if the input in the textfield is a number from 0-500 or a letter
     *
     * @param text Input in the textfield
     * @return true or false
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-13
     */
    private boolean hasNumber(String text) {
        if (text.matches("^(^500|^4[0-9][0-9]$|^3[0-9][0-9]$|^2[0-9][0-9]$|^1[0-9][0-9]$|^[1-9][0-9]$|[0-9]$)$")) {
            return true;
        } else {
            return false;
        }
    }

    private void resetTradeUiElements() {

        bidder1.setVisible(false);
        bidder2.setVisible(false);
        bidder3.setVisible(false);
        noOffer.setVisible(false);
        amountInput.setText("");
        secondInput.setVisible(false);
        secondInput.setText("");
        thirdInput.setVisible(false);
        thirdInput.setText("");
        fourthInput.setVisible(false);
        fourthInput.setText("");
        fifthInput.setVisible(false);
        fifthInput.setText("");

        ressourceChoice.setValue("What do you want to trade?");
        secondChoice.setValue("What do you want to trade?");
        secondChoice.setVisible(false);
        thirdChoice.setValue("What do you want to trade?");
        thirdChoice.setVisible(false);
        fourthChoice.setValue("What do you want to trade?");
        fourthChoice.setVisible(false);
        fifthChoice.setValue("What do you want to trade?");
        fifthChoice.setVisible(false);
        hide.setDisable(true);

    }

    public void setValuesOfTradeView(UserDTO user, String lobby, String tradeCode) {
        this.user = user;
        this.lobby = lobby;
        this.tradeCode = tradeCode;
    }
}
 

