package de.uol.swp.client.game;

import de.uol.swp.client.AbstractPresenter;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.application.HostServices;
import javafx.stage.Stage;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Manages the Game Rules window
 *
 * enhanced by Ricardo Mook, 2021-06-27
 * made the Presenter extend from Application rather as from the AbstractPresenter, to properly execute a hyperlink
 *
 * @author Sergej Tulnev
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2021-05-12
 */
public class GameRulesPresenter extends Application {

    public static final String fxml = "/fxml/GameRulesView.fxml";

    @FXML
    ImageView leftPageImage;

    @FXML
    ImageView rightPageImage;

    @FXML
    Hyperlink hyperlink;

    HostServices hostServices = getHostServices();

    private int imagecounter = 1;

    /**
     * Method which must be implemented when extending the Application class. Apart from that it's not used in this
     * presenter
     *
     * @author Ricardo Mook
     * @since 2021-06-27
     */
    @Override
    public void start(Stage stage) throws Exception {

    }

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
     * @since 2021-06-10
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

    /**
     * Handles the actionevent when the hyperlink is clicked
     * <p>
     * When hyperlink is clicked, through the hostServices the website for the game appears and the users can download
     * a copy of the game rules in the form of a PDF file.
     *
     * @author Ricardo Mook
     * @since 2021-06-27
     */
    public void onGameRulesLinkClicked() {
            hostServices.showDocument("https://www.catan.com/service/game-rules");
    }

}


