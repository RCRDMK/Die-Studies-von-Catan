package de.uol.swp.common.message;

import java.util.ArrayList;
import java.util.List;

import de.uol.swp.common.user.Session;

/**
 * Base class of all server messages. Basic handling of notifications from the server
 * to a group of clients
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.message.AbstractMessage
 * @see de.uol.swp.common.message.ServerMessage
 * @since 2019-08-07
 */
public class AbstractServerMessage extends AbstractMessage implements ServerMessage {

    transient private List<Session> receiver = new ArrayList<>();

    @Override
    public List<Session> getReceiver() {
        return receiver;
    }

    @Override
    public void setReceiver(List<Session> receiver) {
        this.receiver = receiver;
    }
}
