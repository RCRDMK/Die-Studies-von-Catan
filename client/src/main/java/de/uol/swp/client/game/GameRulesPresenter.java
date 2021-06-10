package de.uol.swp.client.game;

import de.uol.swp.client.AbstractPresenter;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.*;


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
    ImageView firstRuleImage;

    @FXML
    ImageView secondRuleImage;


    int imagecounter= 00;



    public void onBackButtonClicked(ActionEvent actionEvent) {
        if (imagecounter >=02){
            imagecounter= imagecounter-2;
            Image leftPage = new Image("src/main/resources/img/GameRules/00" + imagecounter + ".jpg");
            firstRuleImage.setImage(leftPage);

            Image rightPage = new Image("src/main/resources/img/GameRules/00"+ (imagecounter+1) + ".jpg");
            secondRuleImage.setImage(leftPage);
        }


    }

    public void onNextButtonClicked(ActionEvent actionEvent) {
        if (imagecounter <=10){
            imagecounter= imagecounter+2;
            Platform.runLater(()->{
                Image leftPage = new Image("src/main/resources/img/GameRules/00" + imagecounter + ".jpg");
                firstRuleImage.setImage(leftPage);

                Image rightPage = new Image("src/main/resources/img/GameRules/00"+ (imagecounter+1) + ".jpg");
                secondRuleImage.setImage(leftPage);
            });

        }
    }
}
