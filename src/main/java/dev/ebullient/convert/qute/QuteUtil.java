package dev.ebullient.convert.qute;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface QuteUtil {
    default boolean isPresent(Collection<?> s) {
        return s != null && !s.isEmpty();
    }

    default boolean isPresent(Map<?, ?> s) {
        return s != null && !s.isEmpty();
    }

    default boolean isPresent(String s) {
        return s != null && !s.isBlank();
    }

    default void addIntegerUnlessEmpty(Map<String, Object> map, String key, Integer value) {
        if (value != null) {
            map.put(key, value);
        }
    }

    default void addUnlessEmpty(Map<String, Object> map, String key, String value) {
        if (value != null && !value.isBlank()) {
            map.put(key, value);
        }
    }

    default void addUnlessEmpty(Map<String, Object> map, String key, Collection<NamedText> value) {
        if (value != null && !value.isEmpty()) {
            map.put(key, value);
        }
    }

    default void addUnlessEmpty(Map<String, Object> map, String key, List<?> value) {
        if (value != null && !value.isEmpty()) {
            map.put(key, value);
        }
    }
}