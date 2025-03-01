package org.d4rckh.limiterx.core.common;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Key {
    private final String key;

    public static Key of(final String key) {
        return new Key(key);
    }

    public static Key fromClass(final Class<?> clazz, final String key) {
        return new Key(clazz.getName() + key);
    }
}
