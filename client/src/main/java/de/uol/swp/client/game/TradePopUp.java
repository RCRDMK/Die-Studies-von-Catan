package de.uol.swp.client.game;

import com.google.common.eventbus.Subscribe;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.game.HelperObjects.AmountInput;
import de.uol.swp.common.game.message.TradeInformSellerAboutBidsMessage;
import de.uol.swp.common.game.message.TradeSuccessfulMessage;
import de.uol.swp.common.game.request.TradeItemRequest;
import de.uol.swp.common.game.trade.TradeItem;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

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

public class TradePopUp extends AbstractPresenter {

        //important for the switch cases
        int count = 2;
        UserDTO user;
        String tradeCode;

        AmountInput amountInput = new AmountInput();
        String choice[] = {"What do you want to trade?", "Lumber", "Brick", "Grain", "Wool", "Ore"};
        ChoiceBox ressourceChoice = new ChoiceBox(FXCollections.observableArrayList(choice));
        Button trade = new Button("Start a Trade");
        Button add = new Button("Add");
        Button hide = new Button("Hide");

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

        TextField sellerBid = new TextField();
        TextArea bids = new TextArea();
        StackPane rootPane = new StackPane();
        Scene scene = new Scene(rootPane, 600, 400);
        Stage stage = new Stage();

        ArrayList<TradeItem> tradeItems = new ArrayList();

        ChoiceBox secondChoice = new ChoiceBox(FXCollections.observableArrayList(choice));
        ChoiceBox thirdChoice = new ChoiceBox(FXCollections.observableArrayList(choice));
        ChoiceBox fourthChoice = new ChoiceBox(FXCollections.observableArrayList(choice));
        ChoiceBox fifthChoice = new ChoiceBox(FXCollections.observableArrayList(choice));

        AmountInput secondInput = new AmountInput();
        AmountInput thirdInput = new AmountInput();
        AmountInput fourthInput = new AmountInput();
        AmountInput fifthInput = new AmountInput();
Button bidder1 = new Button();
Button bidder2 = new Button();
Button bidder3 = new Button();
Button noOffer = new Button();

        /**
         * This method is responsible for the correct setup of the UI elements
         *
         * @author Alexander Losse, Ricardo Mook
         * @since 2021-04-09
         */
        private void setPopupUiElements(UserDTO user, String lobby) {



            bidder1.setLayoutX(33.0);
            bidder1.setLayoutY(210.0);
            bidder1.setMnemonicParsing(false);
            bidder1.setVisible(false);


            bidder2.setLayoutX(33.0);
            bidder2.setLayoutY(250.0);
            bidder2.setMnemonicParsing(false);
            bidder2.setVisible(false);


            bidder3.setLayoutX(33.0);
            bidder3.setLayoutY(290.0);
            bidder3.setMnemonicParsing(false);
            bidder3.setVisible(false);


            noOffer.setLayoutX(33.0);
            noOffer.setLayoutY(330.0);
            noOffer.setMnemonicParsing(false);
            noOffer.setVisible(false);





            amountInput.setLayoutX(230.0);
            amountInput.setLayoutY(32.0);
            amountInput.setVisible(true);

            secondInput.setLayoutX(230.0);
            secondInput.setLayoutY(62.0);

            thirdInput.setLayoutX(230.0);
            thirdInput.setLayoutY(92.0);

            fourthInput.setLayoutX(230.0);
            fourthInput.setLayoutY(122.0);

            fifthInput.setLayoutX(230.0);
            fifthInput.setLayoutY(152.0);

            ressourceChoice.setValue("What do you want to trade?");
            ressourceChoice.setLayoutX(33.0);
            ressourceChoice.setLayoutY(32.0);
            ressourceChoice.prefWidth(150.0);

            secondChoice.setValue("What do you want to trade?");
            secondChoice.setLayoutX(33.0);
            secondChoice.setLayoutY(62.0);
            secondChoice.setVisible(false);

            thirdChoice.setValue("What do you want to trade?");
            thirdChoice.setLayoutX(33.0);
            thirdChoice.setLayoutY(92.0);
            thirdChoice.setVisible(false);

            fourthChoice.setValue("What do you want to trade?");
            fourthChoice.setLayoutX(33.0);
            fourthChoice.setLayoutY(122.0);
            fourthChoice.setVisible(false);

            fifthChoice.setValue("What do you want to trade?");
            fifthChoice.setLayoutX(33.0);
            fifthChoice.setLayoutY(152.0);
            fifthChoice.setVisible(false);

            trade.setLayoutX(400.0);
            trade.setLayoutY(32.0);
            trade.setMnemonicParsing(false);

            add.setLayoutX(400.0);
            add.setLayoutY(72.0);
            add.setMnemonicParsing(false);

            hide.setLayoutX(400.0);
            hide.setLayoutY(112.0);
            hide.setMnemonicParsing(false);
            hide.setDisable(true);

            sellerBid.setLayoutX(230.0);
            sellerBid.setLayoutY(192.0);
            sellerBid.setEditable(false);

            bids.setLayoutX(230.0);
            bids.setLayoutY(252.0);
            bids.setEditable(false);
            bids.setMaxWidth(rootPane.getMaxWidth() + 250);
            bids.setMaxHeight(rootPane.getMaxHeight() + 75);


            trade.setOnAction(new EventHandler<ActionEvent>() {


                @Override
                public void handle(ActionEvent event) {//If the amount textfield has a number from 1 -500 in it and
                    // "What do you want to trade?" is not selected, it will create a new tradeitem and add it to the Arraylist
                    if (hasNumber(amountInput.getText()) == true && !ressourceChoice.getValue().toString().equals("What do you want to trade?")) {
                        whatRessource = ressourceChoice.toString();
                        amount = Integer.parseInt(amountInput.getText());
                        tradeItems.add(new TradeItem(whatRessource, amount));
                        if (secondChoice.isVisible() == true && hasNumber(amountInput.getText()) == true && !secondChoice.getValue().toString().equals("What do you want to trade?")) {
                            whatSecondRessource = secondChoice.toString();
                            secondAmount = Integer.parseInt(secondInput.getText());
                            tradeItems.add(new TradeItem(whatSecondRessource, secondAmount));
                            if (thirdChoice.isVisible() == true && hasNumber(amountInput.getText()) == true && !thirdChoice.getValue().toString().equals("What do you want to trade?")) {
                                whatThirdRessource = thirdChoice.toString();
                                thirdAmount = Integer.parseInt(thirdInput.getText());
                                tradeItems.add(new TradeItem(whatThirdRessource, thirdAmount));
                                if (fourthChoice.isVisible() == true && hasNumber(amountInput.getText()) == true && !fourthChoice.getValue().toString().equals("What do you want to trade?")) {
                                    whatFourthRessource = fourthChoice.toString();
                                    fourthAmount = Integer.parseInt(fourthInput.getText());
                                    tradeItems.add(new TradeItem(whatFourthRessource, fourthAmount));
                                    if (fifthChoice.isVisible() == true && hasNumber(amountInput.getText()) == true && !fifthChoice.getValue().toString().equals("What do you want to trade?")) {
                                        whatFifthRessource = fifthChoice.toString();
                                        fifthAmount = Integer.parseInt(fifthInput.getText());
                                        tradeItems.add(new TradeItem(whatFifthRessource, fifthAmount));
                                    }
                                }
                            }
                        }
                        TradeItemRequest tir = new TradeItemRequest(user, lobby, tradeItems, tradeCode);
                        eventBus.post(tir);
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
            });

            add.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {//add another textfield and dropdown menu to send different ressources in one trade
                    switch (count) {
                        case 2:
                            secondChoice.setVisible(true);
                            secondInput.setVisible(true);
                            count++;
                            break;

                        case 3:
                            thirdChoice.setVisible(true);
                            thirdInput.setVisible(true);
                            count++;
                            break;
                        case 4:
                            fourthChoice.setVisible(true);
                            fourthInput.setVisible(true);
                            count++;
                            break;
                        case 5:
                            fifthChoice.setVisible(true);
                            fifthInput.setVisible(true);
                            count++;
                            add.setDisable(true);
                            break;
                    }
                }
            });
            hide.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    switch (count) {//hide the last added textfield and dropdown menu in case it isn't needed or it the adding was an accident
                        case 2:
                            secondChoice.setVisible(false);
                            secondInput.setVisible(false);
                            hide.setDisable(true);
                            break;
                        case 3:
                            thirdChoice.setVisible(false);
                            thirdInput.setVisible(false);
                            count--;
                            break;
                        case 4:
                            add.setDisable(false);
                            fourthChoice.setVisible(false);
                            fourthInput.setVisible(false);
                            count--;
                            break;
                        case 5:
                            fifthChoice.setVisible(false);
                            fifthInput.setVisible(false);
                            count--;
                            break;
                    }
                }
            });
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

        void sellerTradePopup(User user, String lobby) {

            tradeCode = user.getUsername() +  UUID.randomUUID().toString();
            setPopupUiElements((UserDTO)user, lobby);

            /*//There's no control structure so far because there will be always three other players even if some of them are AIs
            Button bidder1 = new Button("Accept the offer from " + tisabm.getBidders().get(0).getUsername());
            bidder1.setLayoutX(33.0);
            bidder1.setLayoutY(210.0);
            bidder1.setMnemonicParsing(false);

            Button bidder2 = new Button("Accept the offer from "+ tisabm.getBidders().get(1).getUsername());
            bidder2.setLayoutX(33.0);
            bidder2.setLayoutY(250.0);
            bidder2.setMnemonicParsing(false);

            Button bidder3 = new Button("Accept the offer from " + tisabm.getBidders().get(2).getUsername());
            bidder3.setLayoutX(33.0);
            bidder3.setLayoutY(290.0);
            bidder3.setMnemonicParsing(false);

            Button noOffer = new Button("Accepting no offer");
            noOffer.setLayoutX(33.0);
            noOffer.setLayoutY(330.0);
            noOffer.setMnemonicParsing(false);*/

            Pane pane = new Pane();
            pane.getChildren().addAll(ressourceChoice, amountInput, trade, sellerBid, bids, bidder1, bidder2, bidder3, noOffer,
                    add, hide, secondChoice, thirdChoice, fourthChoice, fifthChoice, secondInput, thirdInput, fourthInput, fifthInput);
            rootPane.getChildren().addAll(pane);

            stage.setScene(scene);
            stage.setTitle("Trade window");
            stage.show();

            /*//TODO Beim Button Press muss sich das Fenster schließen und Ressourcen müssen ihren Besitzer ändern
            bidder1.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event){
                    stage.close();
                }
            });

            bidder2.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    stage.hide();

                }
            });

            bidder3.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    stage.close();
                }
            });

            noOffer.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    System.out.println("Accepted no offer");
                    stage.close();

                }
            });*/

        }

        @Subscribe
         void test(TradeInformSellerAboutBidsMessage tisabm){
            //There's no control structure so far because there will be always three other players even if some of them are AIs
            bidder1.setText("Accept the offer from "+ tisabm.getBidders().get(0).getUsername());
            bidder1.setVisible(true);

            bidder2.setText("Accept the offer from "+ tisabm.getBidders().get(1).getUsername());
            bidder2.setVisible(true);

            bidder3.setText("Accept the offer from " + tisabm.getBidders().get(2).getUsername());
            bidder3.setVisible(true);

            noOffer.setText("Accepting no offer");
            noOffer.setVisible(true);

            //TODO Beim Button Press muss sich das Fenster schließen und Ressourcen müssen ihren Besitzer ändern
            bidder1.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event){
                    stage.close();
                }
            });

            bidder2.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    stage.hide();

                }
            });

            bidder3.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    stage.close();
                }
            });

            noOffer.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    System.out.println("Accepted no offer");
                    stage.close();

                }
            });
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

        void bidderTradePopup(User user, String lobby) {
            setPopupUiElements((UserDTO)user, lobby);
            //TODO: check if correct game, add tradeCode
            Pane pane = new Pane();
            pane.getChildren().addAll(ressourceChoice, amountInput, trade, sellerBid, bids, add, hide, secondChoice,
                    thirdChoice, fourthChoice, fifthChoice, secondInput, thirdInput, fourthInput, fifthInput);
            rootPane.getChildren().addAll(pane);

            stage.setScene(scene);
            stage.setTitle("Trade window");
            stage.show();
        }

        //TODO Bids müssen korrekt angezeigt werden

        /**
         * This method is responsible for updating the sellerTradePopup
         *
         * @param tradeAnswer Message from the server containing the bids from the other players
         * @author Alexander Losse, Ricardo Mook
         * @since 2021-04-09
         *//*
        @Subscribe
        private void updateForSeller(TradeItemRequest tir) {
            sellerBid.setText("Your offer: " + tir.getTradeItems().toString());
            trade.setDisable(true);
            add.setDisable(true);
        }*/
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
        private void updateForBidder(TradeSuccessfulMessage tsm) {
            if (tsm.isTradeSuccessful()==true && user.getUsername().equals(tsm.getUser().getUsername())) {
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
                        stage.close();
                    }
                });

            } else {
                Alert bidGotDenied= new Alert(Alert.AlertType.CONFIRMATION);
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
                        stage.close();
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
    }

