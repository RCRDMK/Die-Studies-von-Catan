package de.uol.swp.client.game;

import de.uol.swp.client.AbstractPresenter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


/**
 * Manages the Game Rules window
 *
 * @author Sergej Tulnev
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2021-05-12
 */
public class GameRulesPresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/GameRulesView.fxml";

    @FXML
    ImageView leftPageImage;

    @FXML
    ImageView rightPageImage;


    int imagecounter = 1;

    /**
     * Handles the Actionevent when the prev button is clicked
     * <p>
     * When the prev button is clicked, this method loads the next two images of the game instructions into the Imageviewers.
     * But only when the imagecounter is greater or equals 3. If it is, the imagecounter will get reduced by 2.
     *
     * @author Ricardo Mook
     * @since 2021-06-10
     */
    @FXML
    public void onPrevButtonClicked() {
        Platform.runLater(() -> {
            if (imagecounter >= 3) {
                imagecounter = imagecounter - 2;
                Image leftPage = new Image("/img/GameRules/" + imagecounter + ".jpg");
                leftPageImage.setImage(leftPage);

                Image rightPage = new Image("/img/GameRules/" + (imagecounter + 1) + ".jpg");
                rightPageImage.setImage(rightPage);
            }
        });
    }


    /**
     * Handles the actionevent when the next button is pressed.
     * <p>
     * When the next button is clicked, this method loads the previous two images of the game instructions into the Imageviewers.
     * But only when the imagecounter is smaller or equals 13. If it is, the imagecounter will get increased by 2.
     *
     * @author Ricardo Mook
     * @see 2021-06-10
     */

    @FXML
    public void onNextButtonClicked() {
        Platform.runLater(() -> {
            if (imagecounter <= 13) {
                imagecounter = imagecounter + 2;

                Image leftPage = new Image("/img/GameRules/" + imagecounter + ".jpg");
                leftPageImage.setImage(leftPage);

                Image rightPage = new Image("/img/GameRules/" + (imagecounter + 1) + ".jpg");
                rightPageImage.setImage(rightPage);
            }
        });
    }
}


