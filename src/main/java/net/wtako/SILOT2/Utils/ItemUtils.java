package net.wtako.SILOT2.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

public class ItemUtils {

    public static Map<String, Object> serialize(ConfigurationSerializable cs) {
        final Map<String, Object> serialized = ItemUtils.recreateMap(cs.serialize());
        for (final Entry<String, Object> entry: serialized.entrySet()) {
            if (entry.getValue() instanceof ConfigurationSerializable) {
                entry.setValue(ItemUtils.serialize((ConfigurationSerializable) entry.getValue()));
            }
        }
        serialized.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY,
                ConfigurationSerialization.getAlias(cs.getClass()));
        return serialized;
    }

    public static Map<String, Object> recreateMap(Map<String, Object> original) {
        final Map<String, Object> map = new HashMap<String, Object>();
        for (final Entry<String, Object> entry: original.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    public static ConfigurationSerializable deserialize(Map<String, Object> map) {
        for (final Entry<String, Object> entry: map.entrySet()) {
            if (entry.getValue() instanceof Long) {
                entry.setValue(((Long) entry.getValue()).intValue());
            }
        }
        for (final Entry<String, Object> entry: map.entrySet()) {
            if (entry.getValue() instanceof Map
                    && ((Map<?, ?>) entry.getValue())
                            .containsKey(ConfigurationSerialization.SERIALIZED_TYPE_KEY)) {
                entry.setValue(ItemUtils.deserialize((Map<String, Object>) entry.getValue()));
            }
        }
        return ConfigurationSerialization.deserializeObject(map);
    }
}
