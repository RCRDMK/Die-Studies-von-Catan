package de.uol.swp.client.game;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.common.game.message.TradeCardErrorMessage;
import de.uol.swp.common.game.message.TradeInformSellerAboutBidsMessage;
import de.uol.swp.common.game.trade.TradeItem;
import de.uol.swp.common.user.UserDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashMap;

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

    public static final String fxml = "/fxml/TradeView.fxml";

    private String tradeCode;
    private String gameName;
    private UserDTO user;
    private ArrayList<UserDTO> bidders;
    private HashMap<UserDTO, ArrayList<TradeItem>> bids;
    private String lumberString = "Lumber";
    private String oreString = "Ore";
    private String woolString = "Wool";
    private String grainString = "Grain";
    private String brickString = "Brick";
    private String seller;

    @Inject
    GameService gameService;

    @FXML
    Button addItemButton;

    @FXML
    Button sendItemsButton;

    @FXML
    Button endTradeButton;
    @FXML
    ChoiceBox ressourceChoice;

    @FXML
    TextField ressourceInputValue;

    @FXML
    RadioButton offerNoneCheckBox;

    @FXML
    RadioButton offer1CheckBox;

    @FXML
    RadioButton offer2CheckBox;

    @FXML
    RadioButton offer3CheckBox;

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

    @Subscribe
    public void onTradeInformSellerAboutBidsMessage(TradeInformSellerAboutBidsMessage message) {
        if (message.getUser().getUsername().equals(user.getUsername()) && message.getTradeCode().equals(tradeCode)) {

            endTradeButton.setDisable(false);
            offerNoneCheckBox.setDisable(false);
            row1Hbox.setVisible(true);
            offer1CheckBox.setDisable(false);

            int biddersCount = message.getBidders().size();
            if(biddersCount>1){
                row2Hbox.setVisible(true);
                if(biddersCount>2){
                    row3Hbox.setVisible(true);
                }
            }
            bidders = message.getBidders();
            bids = message.getBids();
            setBids();
        }
    }

    @Subscribe
    public void onTradeCardErrorMessage (TradeCardErrorMessage message){
        if(message.getUser().getUsername().equals(user.getUsername()) && message.getTradeCode().equals(tradeCode)){
            addItemButton.setDisable(false);
            sendItemsButton.setDisable(false);
            ressourceInputValue.setDisable(false);
            ressourceChoice.setDisable(false);
        }
    }
    public void setValuesOfTradeView(UserDTO currentUser, String gameName, String tradeCode, String nameOfSeller) {
        this.gameName = gameName;
        this.user = currentUser;
        this.tradeCode = tradeCode;
        this.seller = nameOfSeller;
    }
    //TODO: methode setAngebot
    public void setOffer(ArrayList<TradeItem> sellingItems){
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
                row1Hbox.setVisible(true);
            }
        }


    //TODO: Subscribe methode die die Informationen für das Bieter Fenster übernimmt

    @FXML
    public void onEndTradeButtonPressed(ActionEvent actionEvent) {
        //TODO: Radiobox auf active überprüfen und RadioBoxValue überprüfen und mit value richtiges Angebot weiterleiten(TradeChoiceRequest

        RadioButton selectedRadioButton = (RadioButton) choiceTrade.getSelectedToggle();

        if (selectedRadioButton == offerNoneCheckBox) {
            gameService.sendTradeChoice(user, false, gameName, tradeCode);
        } else if (selectedRadioButton == offer1CheckBox) {
            gameService.sendTradeChoice(bidders.get(0), true, gameName, tradeCode);
        } else if (selectedRadioButton == offer2CheckBox) {
            gameService.sendTradeChoice(bidders.get(1), true, gameName, tradeCode);
        } else if (selectedRadioButton == offer3CheckBox) {
            gameService.sendTradeChoice(bidders.get(2), true, gameName, tradeCode);
        }
    }

    @FXML
    public void onAddItemButtonPressed() {
        readRessourceInputValue();
    }

    @FXML
    public void onSendItemsButtonsPressed() {
        // TODO: hier eigenes angebot einblenden? ^^
        ArrayList<TradeItem> sendTradeItemArrayList = createTradeItemList();
        boolean minimalItems = false;
        for(TradeItem item: sendTradeItemArrayList){
            if(item.getCount()>0){
                minimalItems = true;
            }
        }
        if(minimalItems) {
            gameService.sendItem(user, gameName, sendTradeItemArrayList, tradeCode);
            addItemButton.setDisable(true);
            sendItemsButton.setDisable(true);
            ressourceInputValue.setDisable(true);
            ressourceChoice.setDisable(true);
        }
    }


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

    public ArrayList<TradeItem> createTradeItemList() {
        ArrayList<TradeItem> tradeItems = new ArrayList<>();
        tradeItems.add(new TradeItem(lumberString, Integer.parseInt(lumber0.getText())));
        tradeItems.add(new TradeItem(oreString, Integer.parseInt(ore0.getText())));
        tradeItems.add(new TradeItem(brickString, Integer.parseInt(brick0.getText())));
        tradeItems.add(new TradeItem(grainString, Integer.parseInt(grain0.getText())));
        tradeItems.add(new TradeItem(woolString, Integer.parseInt(wool0.getText())));
        return tradeItems;
    }

    //fills the Textfields with the bids, uses bidders, bids
    public void setBids() {
        if (bids.size() == 1) {
            ArrayList<TradeItem> itemsOffer1 = bids.get(bidders.get(0));
            //fills text for offer 1
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
        } else if (bids.size() == 2) {
            //fills text for offer 2
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
        } else if (bids.size() == 3) {
            //fills text for offer 3
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

    public boolean isStringNumber(String checkedString) {
        if (Integer.parseInt(checkedString) >= 1 && Integer.parseInt(checkedString) <= 999) {
            return true;
        }else{
            return false;
        }
    }
}
 

