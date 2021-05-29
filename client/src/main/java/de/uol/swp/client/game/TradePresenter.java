package de.uol.swp.client.game;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.common.game.message.TradeCardErrorMessage;
import de.uol.swp.common.game.message.TradeInformSellerAboutBidsMessage;
import de.uol.swp.common.game.message.TradeOfferInformBiddersMessage;
import de.uol.swp.common.game.message.TradeStartedMessage;
import de.uol.swp.common.game.message.BankResponseMessage;
import de.uol.swp.common.game.trade.TradeItem;
import de.uol.swp.common.user.UserDTO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is the for the Trade Scene
 *
 * @author Alexander Losse, Ricardo Mook
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2021-04-21
 */

public class TradePresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/TradeView.fxml";
    private static final String lumberString = "Lumber";
    private static final String brickString = "Brick";
    private static final String grainString = "Grain";
    private static final String woolString = "Wool";
    private static final String oreString = "Ore";

    private boolean sellerGotBids = false;;
    private boolean sellerGotBankOffer = false;;
    private boolean isBidder = false;
    private String tradeCode;
    private String gameName;
    private UserDTO user;
    private ArrayList<UserDTO> bidders;
    private ArrayList<ArrayList<TradeItem>> bankOffer;
    private HashMap<UserDTO, ArrayList<TradeItem>> bids;

    /**
     * Initializes the values when a new tradeStartedMessage comes in for the seller
     *
     * @param tradeStartedMessage the message which signals a new trade
     * @author Alexander Lossa, Ricardo Mook
     * @since 2021-04-21
     */
    @Subscribe
    public void onTradeStartedMessage(TradeStartedMessage tradeStartedMessage) {
        if (this.tradeCode == null) {
            this.tradeCode = tradeStartedMessage.getTradeCode();
            this.gameName = tradeStartedMessage.getGame();
            this.user = tradeStartedMessage.getUser();
        }
    }

    /**
     * reacts to a TradeInformSellerAboutBidsMessage
     * <p>
     * checks if the TradeInformSellerMessage is for the user
     * enables the endTradeButton and the offerNoneCheckBox
     * makes all necessary rows visible
     * for convenience it saves the bidders and the bids
     *
     * @param message TradeInformSellerAboutBidsMessage
     * @author Alexander Losse, Ricardo Mook
     * @see TradeInformSellerAboutBidsMessage
     * @since 2021-04-21
     */
    @Subscribe
    public void onTradeInformSellerAboutBidsMessage(TradeInformSellerAboutBidsMessage message) {
        if (this.tradeCode != null) {
            if (message.getUser().getUsername().equals(user.getUsername()) && message.getTradeCode().equals(tradeCode)) {

                endTradeButton.setVisible(true);
                offerNoneRadioButton.setVisible(true);
                row1HBox.setVisible(true);
                offer1RadioButton.setVisible(true);

                int biddersCount = message.getBidders().size();
                if (biddersCount > 1) {
                    row2HBox.setVisible(true);
                    if (biddersCount > 2) {
                        row3HBox.setVisible(true);
                    }
                }
                bidders = message.getBidders();
                bids = message.getBids();
                setBids();
                sellerGotBids = true;
            }
        }
    }

    /**
     * Initializes the values when a new tradeStartedMessage comes in for the bidder
     *
     * @param tradeOfferInformBiddersMessage
     * @author Alexander Losse, Ricardo Mook
     * @see TradeOfferInformBiddersMessage
     * @since 2021-04-21
     */
    @Subscribe
    public void onTradeOfferInformBiddersMessage(TradeOfferInformBiddersMessage tradeOfferInformBiddersMessage) {
        if (this.tradeCode == null) {
            this.tradeCode = tradeOfferInformBiddersMessage.getTradeCode();
            this.gameName = tradeOfferInformBiddersMessage.getName();
            this.user = tradeOfferInformBiddersMessage.getBidder();
            setOffer(tradeOfferInformBiddersMessage.getSellingItems(), tradeOfferInformBiddersMessage.getWantedItems());
            ressourceChoiceBank.setVisible(false);
            createRequestButton.setVisible(false);
            tradeLable2.setVisible(false);
            textRowW.setVisible(false);
            lumberW.setVisible(false);
            brickW.setVisible(false);
            grainW.setVisible(false);
            woolW.setVisible(false);
            oreW.setVisible(false);

        }
    }

    /**
     * reacts to the TradeCardErrorMessage
     * <p>
     * checks if the TradeCardErrorMessage is for the user
     * reenables addItemOfferButton, sendItemsButton, ressourceInputValue, ressourceChoice, endTradeButton
     * they are disabled íf the sendItemsButton is pressed
     * the message is received if the user has not enough items in the inventory
     *
     * @param message TradeCardErrorMessage
     * @author Alexander Losse, Ricardo Mook
     * @see TradeCardErrorMessage
     * @since 2021-04-21
     */
    @Subscribe
    public void onTradeCardErrorMessage(TradeCardErrorMessage message) {
        if (this.tradeCode != null) {
            if (message.getUser().getUsername().equals(user.getUsername()) && message.getTradeCode().equals(tradeCode)) {
                addItemOfferButton.setDisable(false);
                addItemWishButton.setDisable(false);
                sendItemsButton.setDisable(false);
                ressourceInputValue.setDisable(false);
                ressourceChoice.setDisable(false);
                if (!isBidder) {
                    endTradeButton.setVisible(true);
                } else {
                    rejectOfferButton.setDisable(false);
                }
            }
        }
    }

    /**
     * Reacts to the BankResponseMessage
     * <p>
     *
     * @param message BankResponseMessage
     *
     * @author Anton Nikiforov
     * @see BankResponseMessage
     * @since 2021-05-29
     */
    @Subscribe
    public void onBankResponseMessage(BankResponseMessage message) {

    }

    /**
     * gives the selling items to bidder
     * <p>
     * this help-method adds the selling Items to the Object
     * the ArrayList<TradeItem> sellingItems is parsed through and the values are displayed in row 1(row-number starts at 0)
     * row 1 is now visible
     *
     * @param sellingItems ArrayList<TradeItem>
     * @author Alexander Losse, Ricardo Mook
     * @see TradeItem
     * @since 2021-04-21
     */
    public void setOffer(ArrayList<TradeItem> sellingItems, ArrayList<TradeItem> wantedItems) {
        for (TradeItem item : sellingItems) {
            String valueOfCount = String.valueOf(item.getCount());
            if (item.getName().equals(lumberString)) {
                lumber1.setText(valueOfCount);
            } else if (item.getName().equals(brickString)) {
                brick1.setText(valueOfCount);
            } else if (item.getName().equals(grainString)) {
                grain1.setText(valueOfCount);
            } else if (item.getName().equals(woolString)) {
                wool1.setText(valueOfCount);
            } else if (item.getName().equals(oreString)) {
                ore1.setText(valueOfCount);
            }
        }

        for (TradeItem item : wantedItems) {
            String valueOfCount = String.valueOf(item.getCount());
            if (item.getName().equals(lumberString)) {
                lumber2.setText(valueOfCount);
            } else if (item.getName().equals(brickString)) {
                brick2.setText(valueOfCount);
            } else if (item.getName().equals(grainString)) {
                grain2.setText(valueOfCount);
            } else if (item.getName().equals(woolString)) {
                wool2.setText(valueOfCount);
            } else if (item.getName().equals(oreString)) {
                ore2.setText(valueOfCount);
            }
        }
        isBidder = true;
        addItemWishButton.setVisible(false);
        rejectOfferButton.setVisible(true);
        row1HBox.setVisible(true);
        textRow1.setText("Seller offers:");
        offer1RadioButton.setVisible(false);
        row2HBox.setVisible(true);
        textRow2.setText("Seller wants:");
        offer2RadioButton.setVisible(false);
        endTradeButton.setVisible(false);
    }

    /**
     * rejects the offer of the seller
     * <p>
     * sends a TradeItemRequest with all TradeItem counts = 0
     *
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-22
     */
    @FXML
    public void onRejectOfferButtonPressed() {
        ArrayList<TradeItem> sendEmptyTradeItemArrayList = new ArrayList<>();
        sendEmptyTradeItemArrayList.add(new TradeItem(lumberString, 0));
        sendEmptyTradeItemArrayList.add(new TradeItem(brickString, 0));
        sendEmptyTradeItemArrayList.add(new TradeItem(grainString, 0));
        sendEmptyTradeItemArrayList.add(new TradeItem(woolString, 0));
        sendEmptyTradeItemArrayList.add(new TradeItem(oreString, 0));
        gameService.sendItem(user, gameName, sendEmptyTradeItemArrayList, tradeCode, sendEmptyTradeItemArrayList);
        disableAbilityToSentItems();
    }


    /**
     * ends the trade and send the TradeChoice via the gameService/closes the trade window if no Trade started
     * <p>
     * if sellerGotBids == false
     * the method checks which RadioButton is active
     * RadioButtons: none,1,2,3
     * the methods calls gameService.sendTradeChoice with the corresponding bidder
     * else
     * closes the TradeWindow
     *
     * @author Alexander Losse, Ricardo Mook
     * @see TradeItem
     * @see GameService
     * @since 2021-04-21
     */
    @FXML
    public void onEndTradeButtonPressed() {
        if (sellerGotBids) {
            RadioButton selectedRadioButton = (RadioButton) choiceTrade.getSelectedToggle();

            if (selectedRadioButton == offerNoneRadioButton) {
                gameService.sendTradeChoice(user, false, gameName, tradeCode);
            } else if (selectedRadioButton == offer1RadioButton) {
                gameService.sendTradeChoice(bidders.get(0), true, gameName, tradeCode);
            } else if (selectedRadioButton == offer2RadioButton) {
                gameService.sendTradeChoice(bidders.get(1), true, gameName, tradeCode);
            } else if (selectedRadioButton == offer3RadioButton) {
                gameService.sendTradeChoice(bidders.get(2), true, gameName, tradeCode);
            }
        } else if (sellerGotBankOffer) {
            RadioButton selectedRadioButton = (RadioButton) choiceTrade.getSelectedToggle();

            if (selectedRadioButton == offerNoneRadioButton) {
                gameService.sendTradeChoice(user, false, gameName, tradeCode);
            } else if (selectedRadioButton == offer1RadioButton) {
                gameService.sendTradeChoice(bidders.get(0), true, gameName, tradeCode);
            } else if (selectedRadioButton == offer2RadioButton) {
                gameService.sendTradeChoice(bidders.get(1), true, gameName, tradeCode);
            } else if (selectedRadioButton == offer3RadioButton) {
                gameService.sendTradeChoice(bidders.get(2), true, gameName, tradeCode);
            }

        } else {
            gameService.endTradeBeforeItStarted(gameName, tradeCode);
        }
    }

    /**
     * when add button is pressed the help methods gets called to read the input value
     *
     * @author Alexander Losse, Ricardo Mook
     * @see TradeItem
     * @since 2021-04-21
     */
    @FXML
    public void onAddItemOfferButtonPressed() {
        saveRessourceOfferInputValue();
    }

    /**
     * Help method which saves the input wish value
     *
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-21
     */
    @FXML
    public void onAddItemWishButtonPressed() {
        saveRessourceWishInputValue();
    }

    /**
     * method gets called when on the send button is pressed it collects the trade items - help method -
     * <p>
     * ArrayList<TradeItem> sendTradeItemArrayList is created with createTradeItemList()
     * boolean minimalItems tracks if at least one item ha a count of > 0
     * if minimalItems == true a TradeItemRequest is send via the GameService
     * disables addItemOfferButton, sendItemsButton, ressourceInputValue, ressourceChoice, endTradeButton
     * else nothing happens
     *
     * @author Alexander Losse, Ricardo Mook
     * @see GameService
     * @see TradeItem
     * @since 2021-04-21
     */
    @FXML
    public void onSendItemsSuggestButtonsPressed() {
        ArrayList<TradeItem> sendTradeOfferItemArrayList = createTradeOfferItemList();
        ArrayList<TradeItem> sendTradeWishItemArrayList = createTradeWishItemList();
        boolean minimalItems = false;
        for (TradeItem item : sendTradeOfferItemArrayList) {
            if (item.getCount() > 0) {
                minimalItems = true;
            }
        }

        if (minimalItems) {
            gameService.sendItem(user, gameName, sendTradeOfferItemArrayList, tradeCode, sendTradeWishItemArrayList);
            disableAbilityToSentItems();
        }
        else {
            Alert noValidInput = new Alert(Alert.AlertType.CONFIRMATION);
            noValidInput.setContentText("Please only input valid resources and numbers. It should be 1 item in the offer at least.");
            Button conformation;
            ButtonType ok = new ButtonType("OK", ButtonBar.ButtonData.YES);
            noValidInput.getButtonTypes().setAll(ok);
            conformation = (Button) noValidInput.getDialogPane().lookupButton(ok);
            noValidInput.showAndWait();
            conformation.setOnAction(event -> noValidInput.hide());
        }
    }

    /**
     * Method gets called when on the create Request button was pressed
     * <p>
     * Send gameName, user, tradeCode, ressourceChoiceBank to the GameService for the BankRequest
     *
     * @author Anton Nikiforov
     * @see GameService
     * @since 2021-05-29
     */
    @FXML
    public void onCreateRequestButton() {
        gameService.createBankRequest(gameName, user, tradeCode, ressourceChoiceBank.getValue().toString());
    }

    /**
     * reads the input value from ressourceInputValue and saves the value
     * <p>
     * reads the input value from ressourceInputValue
     * if the value is a viable int
     * the method checks which ressource is to be added
     * sets the corresponding text in row 0
     * else nothing happens
     *
     * @author Alexander Losse, Ricardo Mook
     * @see TradeItem
     * @since 2021-04-21
     */
    public void saveRessourceOfferInputValue() {
        String ressourceOfferInputValueText = ressourceInputValue.getText();
        if (isStringNumber(ressourceOfferInputValueText)) {
            String ressourceChoiceString = ressourceChoice.getValue().toString();
            if (ressourceChoiceString.equals(lumberString)) {
                lumber0.setText(ressourceOfferInputValueText);
            } else if (ressourceChoiceString.equals(brickString)) {
                brick0.setText(ressourceOfferInputValueText);
            } else if (ressourceChoiceString.equals(grainString)) {
                grain0.setText(ressourceOfferInputValueText);
            } else if (ressourceChoiceString.equals(woolString)) {
                wool0.setText(ressourceOfferInputValueText);
            } else if (ressourceChoiceString.equals(oreString)) {
                ore0.setText(ressourceOfferInputValueText);
            }
        }
    }

    /**
     * Saves the WishInputValue
     *
     * @author Alexander Losse, Ricardo Moo
     * @since 2021-04-21
     */
    public void saveRessourceWishInputValue() {
        String ressourceWishInputValueText = ressourceInputValue.getText();
        if (isStringNumber(ressourceWishInputValueText)) {
            String ressourceChoiceString = ressourceChoice.getValue().toString();
            if (ressourceChoiceString.equals(lumberString)) {
                lumberW.setText(ressourceWishInputValueText);
            } else if (ressourceChoiceString.equals(brickString)) {
                brickW.setText(ressourceWishInputValueText);
            } else if (ressourceChoiceString.equals(grainString)) {
                grainW.setText(ressourceWishInputValueText);
            } else if (ressourceChoiceString.equals(woolString)) {
                woolW.setText(ressourceWishInputValueText);
            } else if (ressourceChoiceString.equals(oreString)) {
                oreW.setText(ressourceWishInputValueText);
            }
        }
    }

    /**
     * creates a trade item list - help method -
     * <p>
     * creates ArrayList<TradeItems> tradeItems
     * creates TradeItems with the values from row 0 and the corresponding ressource name
     * tradeItems are added to tradeItems
     * tradeItems is returned
     *
     * @return ArrayList with TradeItems in it
     * @author Alexander Losse, Ricardo Mook
     * @see TradeItem
     * @since 2021-04-21
     */
    public ArrayList<TradeItem> createTradeOfferItemList() {
        ArrayList<TradeItem> tradeItems = new ArrayList<>();
        tradeItems.add(new TradeItem(lumberString, Integer.parseInt(lumber0.getText())));
        tradeItems.add(new TradeItem(brickString, Integer.parseInt(brick0.getText())));
        tradeItems.add(new TradeItem(grainString, Integer.parseInt(grain0.getText())));
        tradeItems.add(new TradeItem(woolString, Integer.parseInt(wool0.getText())));
        tradeItems.add(new TradeItem(oreString, Integer.parseInt(ore0.getText())));
        return tradeItems;
    }

    /**
     * Help method for creating the ItemList for the wishes
     *
     * @return TradeItem wish list
     * @author Alexander Losse, Ricardo Mook
     * @see TradeItem
     * @since 2021-04-21
     */
    public ArrayList<TradeItem> createTradeWishItemList() {
        ArrayList<TradeItem> tradeItems = new ArrayList<>();
        tradeItems.add(new TradeItem(lumberString, Integer.parseInt(lumberW.getText())));
        tradeItems.add(new TradeItem(brickString, Integer.parseInt(brickW.getText())));
        tradeItems.add(new TradeItem(grainString, Integer.parseInt(grainW.getText())));
        tradeItems.add(new TradeItem(woolString, Integer.parseInt(woolW.getText())));
        tradeItems.add(new TradeItem(oreString, Integer.parseInt(oreW.getText())));
        return tradeItems;
    }

    /**
     * this help method sets the bid in the view for the seller, so that the user can see the offers and select one of them.
     * <p>
     * ArrayList<UserDTO> bidders and HashMap<UserDTO, ArrayList<TradeItem>> bids are used to fill row 1,2,3 with the bids of the bidders
     * if more then 0 bids are available
     * fills row 1 with the offer of the first bidder in bids
     * if more then 1 bids are available
     * fills row 2 with the offer of the second bidder in bids
     * if more then 2 bids are available
     * fills row 3 with the offer of the third bidder in bids
     *
     * @author Alexander Losse, Ricardo Mook
     * @see TradeItem
     * @since 2021-04-21
     */
    public void setBids() {
        if (bids.size() > 0) {
            ArrayList<TradeItem> itemsOffer1 = bids.get(bidders.get(0));

            for (TradeItem item : itemsOffer1) {
                String valueOfCount = String.valueOf(item.getCount());
                if (item.getName().equals(lumberString)) {
                    lumber1.setText(valueOfCount);
                } else if (item.getName().equals(brickString)) {
                    brick1.setText(valueOfCount);
                } else if (item.getName().equals(grainString)) {
                    grain1.setText(valueOfCount);
                } else if (item.getName().equals(woolString)) {
                    wool1.setText(valueOfCount);
                } else if (item.getName().equals(oreString)) {
                    ore1.setText(valueOfCount);
                }
            }
            if (bids.size() > 1) {

                ArrayList<TradeItem> itemsOffer2 = bids.get(bidders.get(1));
                for (TradeItem item : itemsOffer2) {
                    String valueOfCount = String.valueOf(item.getCount());
                    if (item.getName().equals(lumberString)) {
                        lumber2.setText(valueOfCount);
                    } else if (item.getName().equals(brickString)) {
                        brick2.setText(valueOfCount);
                    } else if (item.getName().equals(grainString)) {
                        grain2.setText(valueOfCount);
                    } else if (item.getName().equals(woolString)) {
                        wool2.setText(valueOfCount);
                    } else if (item.getName().equals(oreString)) {
                        ore2.setText(valueOfCount);
                    }
                }
                if (bids.size() > 2) {

                    ArrayList<TradeItem> itemsOffer3 = bids.get(bidders.get(2));
                    for (TradeItem item : itemsOffer3) {
                        String valueOfCount = String.valueOf(item.getCount());
                        if (item.getName().equals(lumberString)) {
                            lumber3.setText(valueOfCount);
                        } else if (item.getName().equals(brickString)) {
                            brick3.setText(valueOfCount);
                        } else if (item.getName().equals(grainString)) {
                            grain3.setText(valueOfCount);
                        } else if (item.getName().equals(woolString)) {
                            wool3.setText(valueOfCount);
                        } else if (item.getName().equals(oreString)) {
                            ore3.setText(valueOfCount);
                        }
                    }
                }
            }
        }
    }

    /**
     * checks if given string is a int
     * <p>
     * the method initialises isCheckedStringInt as false
     * checks if String is empty
     * if true, the methods returns isCheckedStringInt = false
     * if false,
     * the first char is checked if it is 0
     * if true, the method returns false
     * if false,
     * the methods checks if the chars are valid values(0-9)
     * if true,  isCheckedStringInt is set to true and if all chars are valid the method returns isCheckedStringInt = true
     * if false, the methods returns false
     *
     * @param checkedString the input value
     * @return boolean if input is a number
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-21
     */
    public boolean isStringNumber(String checkedString) {
        boolean isCheckedStringInt = false;
        if (!checkedString.isEmpty()) {
            //char turns the value to ASCII: 0 ->48
            if (checkedString.length() > 1 && checkedString.charAt(0) == 48) {
                return false;
            }
            //char turns the value to ASCII: 0->48, 1->49,...,9->57
            for (char a : checkedString.toCharArray()) {
                if (a == 48 || a == 49 || a == 50 || a == 51 || a == 52 || a == 53 || a == 54 || a == 55 || a == 56 || a == 57) {
                    isCheckedStringInt = true;
                } else {
                    return false;
                }
            }
        }
        return isCheckedStringInt;
    }

    /**
     * Help method for disabling buttons
     *
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-21
     */
    private void disableAbilityToSentItems() {
        addItemOfferButton.setDisable(true);
        addItemWishButton.setDisable(true);
        sendItemsButton.setDisable(true);
        ressourceInputValue.setDisable(true);
        ressourceChoice.setDisable(true);
        ressourceChoiceBank.setVisible(false);
        createRequestButton.setVisible(false);
        tradeLable2.setVisible(false);
        endTradeButton.setVisible(false);
        if (isBidder) {
            rejectOfferButton.setDisable(true);
        }
    }

    ////////////////////////////////////////
    //
    //
    //  FXML STUFF
    //
    //
    ////////////////////////////////////////

    @Inject
    GameService gameService;

    @FXML
    Button addItemOfferButton;

    @FXML
    Button sendItemsButton;

    @FXML
    Button addItemWishButton;

    @FXML
    Button endTradeButton;

    @FXML
    Button rejectOfferButton;

    @FXML
    Button createRequestButton;

    @FXML
    ChoiceBox ressourceChoice;

    @FXML
    ChoiceBox ressourceChoiceBank;

    @FXML
    TextField ressourceInputValue;

    @FXML
    RadioButton offerNoneRadioButton;

    @FXML
    RadioButton offer1RadioButton;

    @FXML
    RadioButton offer2RadioButton;

    @FXML
    RadioButton offer3RadioButton;

    @FXML
    RadioButton offer4RadioButton;

    @FXML
    Text textCol;

    @FXML
    Text lumberW;

    @FXML
    Text lumber0;

    @FXML
    Text lumber1;

    @FXML
    Text lumber2;

    @FXML
    Text lumber3;

    @FXML
    Text lumber4;

    @FXML
    Text brickW;

    @FXML
    Text brick0;

    @FXML
    Text brick1;

    @FXML
    Text brick2;

    @FXML
    Text brick3;

    @FXML
    Text brick4;

    @FXML
    Text grainW;

    @FXML
    Text grain0;

    @FXML
    Text grain1;

    @FXML
    Text grain2;

    @FXML
    Text grain3;

    @FXML
    Text grain4;

    @FXML
    Text woolW;

    @FXML
    Text wool0;

    @FXML
    Text wool1;

    @FXML
    Text wool2;

    @FXML
    Text wool3;

    @FXML
    Text wool4;

    @FXML
    Text oreW;

    @FXML
    Text ore0;

    @FXML
    Text ore1;

    @FXML
    Text ore2;

    @FXML
    Text ore3;

    @FXML
    Text ore4;

    @FXML
    Text textRowW;

    @FXML
    Text textRow0;

    @FXML
    Text textRow1;

    @FXML
    Text textRow2;

    @FXML
    Text textRow3;

    @FXML
    Text textRow4;

    @FXML
    HBox row1HBox;

    @FXML
    HBox row2HBox;

    @FXML
    HBox row3HBox;

    @FXML
    HBox row4HBox;

    @FXML
    Label tradeLable1;

    @FXML
    Label tradeLable2;

    @FXML
    ToggleGroup choiceTrade;
}