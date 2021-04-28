package de.uol.swp.common.user.response;

import de.uol.swp.common.message.AbstractResponseMessage;

/**
 * A response, that the user deletion was successful
 * <p>
 * This response is only sent to clients that previously sent a DropUserRequest
 * that was executed successfully, otherwise an ExceptionMessage would be sent.
 *
 * @author Carsten Dekker
 * @since 2020-12-15
 */
public class DropUserSuccessfulResponse extends AbstractResponseMessage {
}
