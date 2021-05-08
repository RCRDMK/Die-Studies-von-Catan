package de.uol.swp.common.game.message;

import de.uol.swp.common.game.dto.StatsDTO;

/**
 * Message sent by the server when a game is finished
 * <p>
 *
 * @author René Meyer, Sergej Tulnev
 * @see AbstractGameMessage
 * @since 2021-04-18
 */
public class GameFinishedMessage extends AbstractGameMessage {
    private StatsDTO statsDTO;

    /**
     * Constructor
     * <p>
     *
     * @param statsDTO statsDTO to show stats in summaryScreen
     * @author René Meyer, Sergej Tulnev
     * @since 2021-04-18
     */
    public GameFinishedMessage(StatsDTO statsDTO) {
        this.statsDTO = statsDTO;
    }

    /**
     * Getter for StatsDTO
     * <p>
     *
     * @return statsDTO
     * @author René Meyer, Sergej Tulnev
     * @since 2021-04-18
     */
    public StatsDTO getStatsDTO() {
        return this.statsDTO;
    }

    /**
     * Setter for StatsDTO
     * <p>
     *
     * @param statsDTO statsDTO to show stats in summaryScreen
     * @author René Meyer, Sergej Tulnev
     * @since 2021-04-18
     */
    public void setStatsDTO(StatsDTO statsDTO) {
        this.statsDTO = statsDTO;
    }
}
