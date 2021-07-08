package de.uol.swp.client;

import java.util.HashMap;
import java.util.List;

import javafx.collections.ListChangeListener;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * A Class that helps with the management of tabs in a tabPane
 * <p>
 * It stores tabs of a specific tabPane in a HashMap in regards to their name (text).
 *
 * @author Alexander Losse, Marc Hermes
 * @since 2021-01-20
 */
public class TabHelper {
    private final TabPane tabPane;
    private final HashMap<String, Tab> tabsMap;
    private final HashMap<String, Tab> suspendedTabs;

    /**
     * Constructor
     * <p>
     * This constructor sets the tabPane given by the method input to the tabPane of this TabHelper.
     * A new HashMap tabsMap is created and the method initial() is invoked.
     *
     * @param tabPane tabPane of this TabHelper
     * @author Alexander Losse, Marc Hermes
     * @since 2020-12-02
     */
    public TabHelper(TabPane tabPane) {
        this.tabPane = tabPane;
        tabPane.setMinSize(735, 530);
        tabPane.setMaxSize(1280, 720);
        this.tabsMap = new HashMap<>();
        this.suspendedTabs = new HashMap<>();
        initial();
    }

    /**
     * Getter for the tabPane
     * <p>
     *
     * @return tabPane of the TabHelper
     * @author Alexander Losse, Marc Hermes
     * @since 2021-01-20
     */
    public TabPane getTabPane() {
        return tabPane;
    }

    /**
     * Initializes the TabHelper so that the tabsMap Hashmap is updated everytime a tab is added or removed
     * from the tabPane of this TabHelper
     * <p>
     *
     * @author Alexander Losse, Marc Hermes
     * @since 2021-01-20
     */
    private void initial() {
        tabPane.getTabs().addListener((ListChangeListener<Tab>) c -> {
            while (c.next()) {

                if (c.wasAdded()) {
                    List<? extends Tab> addedTabs = c.getAddedSubList();
                    for (Tab tab : addedTabs) {
                        tabsMap.put(tab.getText(), tab);
                    }
                }
                if (c.wasRemoved()) {
                    List<? extends Tab> removedTabs = c.getRemoved();
                    for (Tab tab : removedTabs) {
                        tabsMap.remove(tab.getText());
                    }
                }
            }

        });
    }

    /**
     * Adds a Tab to the tabPane stored in the TabHelper
     * <p>
     *
     * @param tab the Tab to be added to the inherent tabPane
     * @author Alexander Losse, Marc Hermes
     * @since 2021-01-20
     */
    public void addTab(Tab tab) {
        this.tabPane.getTabs().add(tab);
    }

    /**
     * Returns a Tab in the tabsMap in regards to the text of the tab
     * <p>
     *
     * @param text the text of the Tab to be found in the tabsMap
     * @return the Tab corresponding to the text found in the tabsMap
     * @author Alexander Losse, Marc Hermes
     * @since 2021-01-20
     */
    public Tab getTabByText(String text) {
        return tabsMap.get(text);
    }

    /**
     * Adds a Tab to the tabPane stored in the TabHelper
     * <p>
     *
     * @param text the text of the Tab to be removed from the inherent tabPane
     * @author Alexander Losse, Marc Hermes
     * @since 2021-01-20
     */
    public void removeTab(String text) {
        this.tabPane.getTabs().remove(getTabByText(text));
    }

    /**
     * Suspends a Tab
     * <p>
     * Puts the tab to suspend in the suspendedTabs Map and removes it from the
     * tabsMap.
     *
     * @param text The name of the Tab to suspend
     * @author Marc Hermes
     * @since 2021-03-16
     */
    public void suspendTab(String text) {
        if (tabsMap.containsKey(text)) {
            suspendedTabs.put(text, getTabByText(text));
            removeTab(text);
        }
    }

    /**
     * Unsuspends a Tab
     * <p>
     * Removes the tab to unsuspend from the suspendedTabs Map and adds it to the
     * tabsMap.
     *
     * @param text The name of the Tab to unsuspend
     * @author Marc Hermes
     * @since 2021-03-16
     */
    public void unsuspendTab(String text) {
        if (suspendedTabs.containsKey(text)) {
            Tab tabToUnsuspend = suspendedTabs.remove(text);
            addTab(tabToUnsuspend);
        }
    }
}