package de.uol.swp.client;

import com.sun.javafx.event.EventHandlerManager;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Tab;


public class CustomTab extends Tab {

    private SimpleObjectProperty graphic;
    private final InvalidationListener parentDisabledChangedListener;
    private ReadOnlyBooleanWrapper disabled;
    private final ObservableList<String> styleClass;
    private final EventHandlerManager eventHandlerManager;


    public CustomTab(String var1, Node var2) {
        this.parentDisabledChangedListener = (var1x) -> {
            this.updateDisabled();
        };
        this.styleClass = FXCollections.observableArrayList();
        this.eventHandlerManager = new EventHandlerManager(this);
        this.setText(var1);
        this.setContent(var2);
        this.styleClass.addAll(new String[]{"tab"});
    }

    private void updateDisabled() {
        boolean var1 = this.isDisable() || this.getTabPane() != null && this.getTabPane().isDisabled();
        this.setDisabled(var1);
        Node var2 = this.getContent();
        if (var2 != null) {
            var2.setDisable(var1);
        }

    }

    private final void setDisabled(boolean var1) {
        this.disabledPropertyImpl().set(var1);
    }

    private ReadOnlyBooleanWrapper disabledPropertyImpl() {
        if (this.disabled == null) {
            this.disabled = new ReadOnlyBooleanWrapper() {
                public Object getBean() {
                    return CustomTab.this;
                }

                public String getName() {
                    return "disabled";
                }
            };
        }

        return this.disabled;
    }
    public final ObjectProperty<Scene> graphicPropertyScene() {
        if (this.graphic == null) {
            this.graphic = new SimpleObjectProperty(this, "graphic");
        }

        return this.graphic;
    }

    public final void setGraphic(Scene var1) {
        this.graphicPropertyScene().set(var1);
    }


}
