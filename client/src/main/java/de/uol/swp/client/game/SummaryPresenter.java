package de.uol.swp.client.game;

import de.uol.swp.client.AbstractPresenter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manages the SummaryView
 * <p>
 * Class was build exactly like GamePresenter.
 *
 * @author René Meyer, Sergej Tulnev
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2021-04-18
 */
public class SummaryPresenter extends AbstractPresenter {
    public static final String fxml = "/fxml/SummaryView.fxml";
    private static final Logger LOG = LogManager.getLogger(GamePresenter.class);

}
