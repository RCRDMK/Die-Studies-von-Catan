package de.uol.swp.client.game.HelperObjects;

import javafx.scene.control.TextField;

/**
 * This class is used as a blueprint for the textfields in TradePopup and should also increase the readablity of the
 * code in said class.
 *
 * @author Alexander Losse, Ricardo Mook
 * @since 2021-04-12
 */

public class AmountInput extends TextField {
    public AmountInput(){
        visibleProperty().set(false);
        promptTextProperty().set("How much?");
    }
}
