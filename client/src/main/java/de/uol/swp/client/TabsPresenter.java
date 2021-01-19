package de.uol.swp.client;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class TabsPresenter {

    public static final String fxml = "/fxml/TabView.fxml";
    @FXML
    private TabPane tabPane ;

    @FXML
    private void addTab() {
        int numTabs = tabPane.getTabs().size();
        Tab tab = new Tab("Tab "+(numTabs+1));
        tabPane.getTabs().add(tab);
    }

    @FXML
    private void listTabs() {
        tabPane.getTabs().forEach(tab -> System.out.println(tab.getText()));
        System.out.println();
    }
}
