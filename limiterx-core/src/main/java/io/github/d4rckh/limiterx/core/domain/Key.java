package io.github.d4rckh.limiterx.core.domain;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Key {
    private final String key;

    public static Key of(final String key) {
        return new Key(
            String.format("LimiterX(%s)", key)
        );
    }

    public static Key fromClass(final Class<?> clazz, final String signature, final String key) {
        return Key.of(
            String.format("%s#%s#%s", clazz.getName(), signature, key)
        );
    }
}
