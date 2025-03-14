package de.uol.swp.common.chat;

import org.junit.jupiter.api.Test;

import de.uol.swp.common.SerializationTestHelper;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChatSerializableTest {

    @Test
    void testChatSerializable() {
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new RequestChatMessage("test",
                        "TestLobby", "Peter", 12.15),
                RequestChatMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new ResponseChatMessage("test",
                "TestLobby", "Peter", 12.15), ResponseChatMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new ResponseEmptyChatMessage("test",
                "TestLobby", "Peter", 12.15), ResponseEmptyChatMessage.class));

    }
}

