package de.uol.swp.client;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;

public class TabsManager  {

    static final Logger LOG = LogManager.getLogger(TabsManager.class);
    private final Injector injector;
    final private Stage primaryStage;
    private static TabPane tabPane;


    @Inject
    public TabsManager(EventBus eventBus, Injector injected, @Assisted Stage tabStage) {
        eventBus.register(this);
        this.injector = injected;
        this.primaryStage = tabStage;
        initialize();
    }


    public void initialize() {
        // Tabs

        TabPane tabPane = new TabPane();
        this.tabPane = tabPane;
        VBox vBox = new VBox(tabPane);
        Scene tabScene = new Scene(vBox);
        Parent rootPane = initTab(TabsPresenter.fxml);
    }

    /**
     * Subroutine creating parent panes from FXML files
     * <p>
     * This Method tries to create a parent pane from the FXML file specified by
     * the URL String given to it. If the LOG-Level is set to Debug or higher loading
     * is written to the LOG.
     * If it fails to load the view a RuntimeException is thrown.
     *
     * @param fxmlFile FXML file to load the view from
     * @return view loaded from FXML or null
     * @author Marco Grawunder
     * @since 2019-09-03
     */
    private Parent initTab(String fxmlFile) {
        Parent root;
        FXMLLoader loader = injector.getInstance(FXMLLoader.class);
        try {
            URL url = getClass().getResource(fxmlFile);
            LOG.debug("Loading " + url);
            loader.setLocation(url);
            root = loader.load();
        } catch (Exception e) {
            throw new RuntimeException("Could not load View!" + e.getMessage(), e);
        }
        return root;
    }

    public void showMainTab(Scene mainMenu) {
        CustomTab mainMenuTab = new CustomTab("Planes", new Label("Show all planes available"));
        tabPane.getTabs().add(mainMenuTab);
        mainMenuTab.setGraphic(mainMenu);
        VBox vBox = new VBox(tabPane);
        Scene tabScene = new Scene(vBox);
        this.primaryStage.setScene(tabScene);
        primaryStage.show();
    }

}