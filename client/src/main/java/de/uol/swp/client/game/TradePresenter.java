package de.uol.swp.client.game;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.common.game.message.TradeCardErrorMessage;
import de.uol.swp.common.game.message.TradeInformSellerAboutBidsMessage;
import de.uol.swp.common.game.trade.TradeItem;
import de.uol.swp.common.user.UserDTO;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
    private static final String oreString = "Ore";
    private static final String woolString = "Wool";
    private static final String grainString = "Grain";
    private static final String brickString = "Brick";


    private boolean sellerGotBids;
    private boolean isBidder = false;
    private String tradeCode;
    private String gameName;
    private UserDTO user;
    private ArrayList<UserDTO> bidders;
    private HashMap<UserDTO, ArrayList<TradeItem>> bids;
    private String seller;


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
        if (message.getUser().getUsername().equals(user.getUsername()) && message.getTradeCode().equals(tradeCode)) {

            endTradeButton.setVisible(true);
            offerNoneRadioButton.setVisible(true);
            row1Hbox.setVisible(true);
            offer1RadioButton.setVisible(true);

            int biddersCount = message.getBidders().size();
            if (biddersCount > 1) {
                row2Hbox.setVisible(true);
                if (biddersCount > 2) {
                    row3Hbox.setVisible(true);
                }
            }
            bidders = message.getBidders();
            bids = message.getBids();
            setBids();
            sellerGotBids = true;
        }
    }

    /**
     * reacts to the TradeCardErrorMessage
     * <p>
     * checks if the TradeCardErrorMessage is for the user
     * reenables addItemButton, sendItemsButton, ressourceInputValue, ressourceChoice, endTradeButton
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
        if (message.getUser().getUsername().equals(user.getUsername()) && message.getTradeCode().equals(tradeCode)) {
            addItemButton.setDisable(false);
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

    /**
     * sets the values for the tradewindow
     * <p>
     * this help-method adds the user,gameName,tradeCode and nameOfSeller to the Object
     *
     * @param currentUser  UserDTO
     * @param gameName     String
     * @param tradeCode    String
     * @param nameOfSeller String
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-21
     */
    public void setValuesOfTradeView(UserDTO currentUser, String gameName, String tradeCode, String nameOfSeller) {
        this.gameName = gameName;
        this.user = currentUser;
        this.tradeCode = tradeCode;
        this.seller = nameOfSeller;
        sellerGotBids = false;
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
    public void setOffer(ArrayList<TradeItem> sellingItems) {
        for (TradeItem item : sellingItems) {
            String valueOfCount = String.valueOf(item.getCount());
            if (item.getName().equals(lumberString)) {
                lumber1.setText(valueOfCount);
            } else if (item.getName().equals(oreString)) {
                ore1.setText(valueOfCount);
            } else if (item.getName().equals(brickString)) {
                brick1.setText(valueOfCount);
            } else if (item.getName().equals(grainString)) {
                grain1.setText(valueOfCount);
            } else if (item.getName().equals(woolString)) {
                wool1.setText(valueOfCount);
            }
            endTradeButton.setVisible(false);
            rejectOfferButton.setVisible(true);
            row1Hbox.setVisible(true);
            isBidder = true;

        }
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
        sendEmptyTradeItemArrayList.add(new TradeItem(oreString, 0));
        sendEmptyTradeItemArrayList.add(new TradeItem(lumberString, 0));
        sendEmptyTradeItemArrayList.add(new TradeItem(woolString, 0));
        sendEmptyTradeItemArrayList.add(new TradeItem(grainString, 0));
        sendEmptyTradeItemArrayList.add(new TradeItem(brickString, 0));
        gameService.sendItem(user, gameName, sendEmptyTradeItemArrayList, tradeCode);
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
        } else {
            gameService.endTradeBeforeItStarted(user, gameName, tradeCode);
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
    public void onAddItemButtonPressed() {
        readRessourceInputValue();
    }

    /**
     * method gets called when on the send button is pressed it collects the trade items - help method -
     * <p>
     * ArrayList<TradeItem> sendTradeItemArrayList is created with createTradeItemList()
     * boolean minimalItems tracks if at least one item ha a count of > 0
     * if minimalItems == true a TradeItemRequest is send via the GameService
     * disables addItemButton, sendItemsButton, ressourceInputValue, ressourceChoice, endTradeButton
     * else nothing happens
     *
     * @author Alexander Losse, Ricardo Mook
     * @see GameService
     * @see TradeItem
     * @since 2021-04-21
     */
    @FXML
    public void onSendItemsButtonsPressed() {
        ArrayList<TradeItem> sendTradeItemArrayList = createTradeItemList();
        boolean minimalItems = false;
        for (TradeItem item : sendTradeItemArrayList) {
            if (item.getCount() > 0) {
                minimalItems = true;
            }
        }

        if (minimalItems) {
            gameService.sendItem(user, gameName, sendTradeItemArrayList, tradeCode);
            disableAbilityToSentItems();
        }//TODO: inform the user that he has to send at least 1 item
        else {
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
    public void readRessourceInputValue() {
        String ressourceInputValueText = ressourceInputValue.getText();
        if (isStringNumber(ressourceInputValueText)) {
            String ressourceChoiceString = ressourceChoice.getValue().toString();
            if (ressourceChoiceString.equals(lumberString)) {
                lumber0.setText(ressourceInputValueText);
            } else if (ressourceChoiceString.equals(oreString)) {
                ore0.setText(ressourceInputValueText);
            } else if (ressourceChoiceString.equals(brickString)) {
                brick0.setText(ressourceInputValueText);
            } else if (ressourceChoiceString.equals(grainString)) {
                grain0.setText(ressourceInputValueText);
            } else if (ressourceChoiceString.equals(woolString)) {
                wool0.setText(ressourceInputValueText);
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
    public ArrayList<TradeItem> createTradeItemList() {
        ArrayList<TradeItem> tradeItems = new ArrayList<>();
        tradeItems.add(new TradeItem(lumberString, Integer.parseInt(lumber0.getText())));
        tradeItems.add(new TradeItem(oreString, Integer.parseInt(ore0.getText())));
        tradeItems.add(new TradeItem(brickString, Integer.parseInt(brick0.getText())));
        tradeItems.add(new TradeItem(grainString, Integer.parseInt(grain0.getText())));
        tradeItems.add(new TradeItem(woolString, Integer.parseInt(wool0.getText())));
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
                } else if (item.getName().equals(oreString)) {
                    ore1.setText(valueOfCount);
                } else if (item.getName().equals(brickString)) {
                    brick1.setText(valueOfCount);
                } else if (item.getName().equals(grainString)) {
                    grain1.setText(valueOfCount);
                } else if (item.getName().equals(woolString)) {
                    wool1.setText(valueOfCount);
                }
            }
            if (bids.size() > 1) {

                ArrayList<TradeItem> itemsOffer2 = bids.get(bidders.get(1));
                for (TradeItem item : itemsOffer2) {
                    String valueOfCount = String.valueOf(item.getCount());
                    if (item.getName().equals(lumberString)) {
                        lumber2.setText(valueOfCount);
                    } else if (item.getName().equals(oreString)) {
                        ore2.setText(valueOfCount);
                    } else if (item.getName().equals(brickString)) {
                        brick2.setText(valueOfCount);
                    } else if (item.getName().equals(grainString)) {
                        grain2.setText(valueOfCount);
                    } else if (item.getName().equals(woolString)) {
                        wool2.setText(valueOfCount);
                    }
                }
                if (bids.size() > 2) {

                    ArrayList<TradeItem> itemsOffer3 = bids.get(bidders.get(2));
                    for (TradeItem item : itemsOffer3) {
                        String valueOfCount = String.valueOf(item.getCount());
                        if (item.getName().equals(lumberString)) {
                            lumber3.setText(valueOfCount);
                        } else if (item.getName().equals(oreString)) {
                            ore3.setText(valueOfCount);
                        } else if (item.getName().equals(brickString)) {
                            brick3.setText(valueOfCount);
                        } else if (item.getName().equals(grainString)) {
                            grain3.setText(valueOfCount);
                        } else if (item.getName().equals(woolString)) {
                            wool3.setText(valueOfCount);
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

    private void disableAbilityToSentItems() {
        addItemButton.setDisable(true);
        sendItemsButton.setDisable(true);
        ressourceInputValue.setDisable(true);
        ressourceChoice.setDisable(true);
        endTradeButton.setVisible(false);
        if (isBidder) {
            rejectOfferButton.setDisable(true);
        }
    }

    //
    // <----- FXML STUFF ----->
    //

    @Inject
    GameService gameService;

    @FXML
    Button addItemButton;

    @FXML
    Button sendItemsButton;

    @FXML
    Button endTradeButton;

    @FXML
    Button rejectOfferButton;

    @FXML
    ChoiceBox ressourceChoice;

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
    Text brick0;

    @FXML
    Text brick1;

    @FXML
    Text brick2;

    @FXML
    Text brick3;

    @FXML
    Text ore0;

    @FXML
    Text ore1;

    @FXML
    Text ore2;

    @FXML
    Text ore3;

    @FXML
    Text lumber0;

    @FXML
    Text lumber1;

    @FXML
    Text lumber2;

    @FXML
    Text lumber3;

    @FXML
    Text grain0;

    @FXML
    Text grain1;

    @FXML
    Text grain2;

    @FXML
    Text grain3;

    @FXML
    Text wool0;

    @FXML
    Text wool1;

    @FXML
    Text wool2;

    @FXML
    Text wool3;

    @FXML
    ToggleGroup choiceTrade;

    @FXML
    HBox row1Hbox;

    @FXML
    HBox row2Hbox;

    @FXML
    HBox row3Hbox;
}
 

