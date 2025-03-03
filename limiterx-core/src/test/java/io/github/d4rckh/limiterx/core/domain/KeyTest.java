package io.github.d4rckh.limiterx.core.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

public class KeyTest {

    @Test
    void keyOf_shouldReturnProperKey() {
        Key key = Key.of("key");

        assertThat(key.getKey()).contains("key");
        assertThat(key.getKey()).containsIgnoringCase("limiterx");
    }

    @Test
    void keyOfClass_shouldReturnKeyWithClassAndMethodName() {
        Class<KeyTest> clazz = KeyTest.class;

        Key key = Key.fromClass(clazz, "methodName", "key");

        assertThat(key.getKey()).contains(clazz.getName());
        assertThat(key.getKey()).contains("key");
        assertThat(key.getKey()).containsIgnoringCase("limiterx");
    }

}
