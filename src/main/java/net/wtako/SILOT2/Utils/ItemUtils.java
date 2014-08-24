package net.wtako.SILOT2.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

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
                    && ((Map<?, ?>) entry.getValue()).containsKey(ConfigurationSerialization.SERIALIZED_TYPE_KEY)) {
                entry.setValue(ItemUtils.deserialize((Map<String, Object>) entry.getValue()));
            }
        }
        return ConfigurationSerialization.deserializeObject(map);
    }

    @SuppressWarnings("unchecked")
    public static JSONObject encodeMeta(ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR) {
            return null;
        }
        final JSONObject metaJson = new JSONObject();
        metaJson.putAll(ItemUtils.serialize(stack.getItemMeta()));

        final JSONObject itemJson = new JSONObject();
        itemJson.put("item-meta", metaJson);
        return itemJson;
    }

    @SuppressWarnings("unchecked")
    public static JSONObject encodeItem(ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR) {
            return null;
        }
        final JSONObject metaJson = new JSONObject();
        metaJson.putAll(ItemUtils.serialize(stack.getItemMeta()));

        final JSONObject itemJson = new JSONObject();
        itemJson.put("item-meta", metaJson);
        itemJson.put("item-type", stack.getType().name());
        itemJson.put("item-qty", stack.getAmount());
        itemJson.put("item-damage", stack.getDurability());
        return itemJson;
    }

    @SuppressWarnings("unchecked")
    public static JSONObject encodeItems(Iterable<ItemStack> items) {
        final JSONObject contentJson = new JSONObject();
        final ArrayList<JSONObject> content = new ArrayList<JSONObject>();
        for (final ItemStack itemStack: items) {
            content.add(ItemUtils.encodeItem(itemStack));
        }
        contentJson.put("content", content);
        return contentJson;
    }

    @SuppressWarnings("unchecked")
    public static JSONObject encodeItems(ItemStack[] items) {
        final JSONObject contentJson = new JSONObject();
        final ArrayList<JSONObject> content = new ArrayList<JSONObject>();
        for (final ItemStack itemStack: items) {
            content.add(ItemUtils.encodeItem(itemStack));
        }
        contentJson.put("content", content);
        return contentJson;
    }

    public static JSONObject encodeInventory(PlayerInventory inv) {
        return ItemUtils.encodeInventory(inv.getContents(), inv.getArmorContents());
    }

    @SuppressWarnings("unchecked")
    public static JSONObject encodeInventory(ItemStack[] invContents, ItemStack[] armorContents) {
        final JSONObject invJson = new JSONObject();
        invJson.put("content", ItemUtils.encodeItems(invContents).get("content"));
        invJson.put("helmet", ItemUtils.encodeItem(armorContents[0]));
        invJson.put("chestplate", ItemUtils.encodeItem(armorContents[1]));
        invJson.put("leggings", ItemUtils.encodeItem(armorContents[2]));
        invJson.put("boots", ItemUtils.encodeItem(armorContents[3]));
        return invJson;
    }

    @SuppressWarnings("unchecked")
    public static void restoreInventory(PlayerInventory inv, String str) {
        final Map<String, Object> itemJson = (Map<String, Object>) JSONValue.parse(str);
        inv.setContents(ItemUtils.restoreItems((ArrayList<JSONObject>) itemJson.get("content")));
        inv.setHelmet(ItemUtils.restoreItem((Map<String, Object>) itemJson.get("helmet")));
        inv.setChestplate(ItemUtils.restoreItem((Map<String, Object>) itemJson.get("chestplate")));
        inv.setLeggings(ItemUtils.restoreItem((Map<String, Object>) itemJson.get("leggings")));
        inv.setBoots(ItemUtils.restoreItem((Map<String, Object>) itemJson.get("boots")));
    }

    @SuppressWarnings("unchecked")
    public static ItemStack[] restoreItems(ArrayList<JSONObject> encodedContent) {
        final ItemStack[] content = new ItemStack[encodedContent.size()];
        for (int i = 0; i < content.length; i++) {
            content[i] = ItemUtils.restoreItem(encodedContent.get(i));
        }
        return content;
    }

    @SuppressWarnings("unchecked")
    public static ItemStack restoreItem(String string) {
        return ItemUtils.restoreItem((Map<String, Object>) JSONValue.parse(string));
    }

    @SuppressWarnings("unchecked")
    public static ItemStack restoreItem(Map<String, Object> itemJson) {
        if (itemJson == null) {
            return null;
        }
        final Map<String, Object> metaMap = (Map<String, Object>) itemJson.get("item-meta");
        final Material itemType = Material.valueOf((String) itemJson.get("item-type"));
        final Integer itemQty = ((Long) itemJson.get("item-qty")).intValue();
        final Short itemDamage = ((Long) itemJson.get("item-damage")).shortValue();

        final ItemStack stack = new ItemStack(itemType, itemQty);
        stack.setDurability(itemDamage);
        if (stack.getType() == Material.ENCHANTED_BOOK) {
            final Map<String, Long> enchantmentMap = (Map<String, Long>) metaMap.get("stored-enchants");
            final EnchantmentStorageMeta esm = (EnchantmentStorageMeta) ItemUtils.deserialize(metaMap);
            if (enchantmentMap != null) {
                for (final Entry<String, Long> entry: enchantmentMap.entrySet()) {
                    esm.addStoredEnchant(Enchantment.getByName(entry.getKey()), entry.getValue().intValue(), false);
                }
            }
            stack.setItemMeta(esm);
        } else {
            stack.setItemMeta((ItemMeta) ItemUtils.deserialize(metaMap));
            final Map<String, Long> enchantmentMap = (Map<String, Long>) metaMap.get("enchants");
            if (enchantmentMap != null) {
                for (final Entry<String, Long> entry: enchantmentMap.entrySet()) {
                    stack.addUnsafeEnchantment(Enchantment.getByName(entry.getKey()), entry.getValue().intValue());
                }
            }
        }
        return stack;
    }

    @SuppressWarnings("unchecked")
    public static void restoreMeta(ItemStack stack, String string) {
        final Map<String, Object> itemJson = (Map<String, Object>) JSONValue.parse(string);
        final Map<String, Object> metaMap = (Map<String, Object>) itemJson.get("item-meta");
        if (stack.getType() == Material.ENCHANTED_BOOK) {
            final Map<String, Long> enchantmentMap = (Map<String, Long>) metaMap.get("stored-enchants");
            final EnchantmentStorageMeta esm = (EnchantmentStorageMeta) ItemUtils.deserialize(metaMap);
            if (enchantmentMap != null) {
                for (final Entry<String, Long> entry: enchantmentMap.entrySet()) {
                    esm.addStoredEnchant(Enchantment.getByName(entry.getKey()), entry.getValue().intValue(), false);
                }
            }
            stack.setItemMeta(esm);
        } else {
            stack.setItemMeta((ItemMeta) ItemUtils.deserialize(metaMap));
            final Map<String, Long> enchantmentMap = (Map<String, Long>) metaMap.get("enchants");
            if (enchantmentMap != null) {
                for (final Entry<String, Long> entry: enchantmentMap.entrySet()) {
                    stack.addUnsafeEnchantment(Enchantment.getByName(entry.getKey()), entry.getValue().intValue());
                }
            }
        }
    }
}
