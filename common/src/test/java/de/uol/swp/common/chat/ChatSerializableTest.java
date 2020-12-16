package de.uol.swp.common.chat;

import de.uol.swp.common.SerializationTestHelper;
import de.uol.swp.common.user.message.UserLoggedInMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChatSerializableTest {
    @Test
    void testChatSerializable(){
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new RequestChatMessage("test", 0, "Peter", 12.15),
                RequestChatMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new ResponseChatMessage("test", 0, "Peter", 12.15), ResponseChatMessage.class));

    }
}
