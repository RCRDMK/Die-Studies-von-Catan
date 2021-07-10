package de.uol.swp.common;

import java.io.Serializable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SerializationTestHelperTest {

    @Test
    void checkNonSerializable() {
        assertThrows(RuntimeException.class, () ->
                SerializationTestHelper.checkSerializableAndDeserializable(new NotSerializable(),
                        NotSerializable.class));
    }

    @Test
    void checkSerializable() {
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable("Hallo", String.class));
    }

    private static class NotSerializable implements Serializable {
        private final Thread thread = new Thread();
    }


}
