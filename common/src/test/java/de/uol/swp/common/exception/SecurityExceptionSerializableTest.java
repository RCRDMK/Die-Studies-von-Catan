package de.uol.swp.common.exception;

import org.junit.jupiter.api.Test;

import de.uol.swp.common.SerializationTestHelper;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class SecurityExceptionSerializableTest {

    @Test
    void testSecurityExceptionSerializable() {
        assertFalse(SerializationTestHelper.checkSerializableAndDeserializable(new SecurityException("test"),
                SecurityException.class));

    }
}
