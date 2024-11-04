package com.github.kuramastone.marketplace.utils;

import com.github.kuramastone.bUtilities.ComponentEditor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.codehaus.plexus.util.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemUtils {

    /**
     * Get the itemstacks displayName or formatted material name
     *
     * @param itemstack
     * @return
     */
    public static String getItemStackName(ItemStack itemstack) {
        if (itemstack.hasItemMeta()) {
            if (itemstack.getItemMeta().hasDisplayName()) {
                String plainText = PlainTextComponentSerializer.plainText().serialize(itemstack.getItemMeta().displayName());
                return plainText;
            }
        }

        String name = itemstack.getType().toString().toLowerCase().replace("_", " ");
        return StringUtils.capitaliseAllWords(name);
    }


    public static void setLore(ItemStack item, List<String> lore) {
        item.editMeta(meta -> {
            List<Component> newLore = new ArrayList<>();
            for (String line : lore) {
                newLore.add(ComponentEditor.decorateComponent(line));
            }
            meta.lore(newLore);
        });
    }

    public static void applyEnchantedEffect(ItemStack itemStack) {
        itemStack.addEnchantment(Enchantment.LURE, 1);
        itemStack.editMeta(meta -> meta.addItemFlags(ItemFlag.HIDE_ENCHANTS));
    }

    public static boolean isPickaxe(ItemStack item) {
        Material mat = item.getType();
        return mat == Material.WOODEN_PICKAXE
                || mat == Material.STONE_PICKAXE
                || mat == Material.GOLDEN_PICKAXE
                || mat == Material.IRON_PICKAXE
                || mat == Material.DIAMOND_PICKAXE
                || mat == Material.NETHERITE_PICKAXE;
    }

    public static boolean isArmor(ItemStack item) {
        if (item == null) return false; // Null check

        Material type = item.getType();
        // Check if the item is one of the armor materials
        return type == Material.LEATHER_HELMET || type == Material.LEATHER_CHESTPLATE ||
                type == Material.LEATHER_LEGGINGS || type == Material.LEATHER_BOOTS ||
                type == Material.CHAINMAIL_HELMET || type == Material.CHAINMAIL_CHESTPLATE ||
                type == Material.CHAINMAIL_LEGGINGS || type == Material.CHAINMAIL_BOOTS ||
                type == Material.IRON_HELMET || type == Material.IRON_CHESTPLATE ||
                type == Material.IRON_LEGGINGS || type == Material.IRON_BOOTS ||
                type == Material.GOLDEN_HELMET || type == Material.GOLDEN_CHESTPLATE ||
                type == Material.GOLDEN_LEGGINGS || type == Material.GOLDEN_BOOTS ||
                type == Material.DIAMOND_HELMET || type == Material.DIAMOND_CHESTPLATE ||
                type == Material.DIAMOND_LEGGINGS || type == Material.DIAMOND_BOOTS ||
                type == Material.NETHERITE_HELMET || type == Material.NETHERITE_CHESTPLATE ||
                type == Material.NETHERITE_LEGGINGS || type == Material.NETHERITE_BOOTS;
    }

    public static boolean isTool(ItemStack item) {
        if (item == null) return false; // Null check

        Material type = item.getType();
        // Check if the item is one of the tool materials
        return type == Material.WOODEN_AXE || type == Material.WOODEN_HOE ||
                type == Material.WOODEN_PICKAXE || type == Material.WOODEN_SHOVEL ||
                type == Material.WOODEN_SWORD || type == Material.STONE_AXE ||
                type == Material.STONE_HOE || type == Material.STONE_PICKAXE ||
                type == Material.STONE_SHOVEL || type == Material.STONE_SWORD ||
                type == Material.IRON_AXE || type == Material.IRON_HOE ||
                type == Material.IRON_PICKAXE || type == Material.IRON_SHOVEL ||
                type == Material.IRON_SWORD || type == Material.GOLDEN_AXE ||
                type == Material.GOLDEN_HOE || type == Material.GOLDEN_PICKAXE ||
                type == Material.GOLDEN_SHOVEL || type == Material.GOLDEN_SWORD ||
                type == Material.DIAMOND_AXE || type == Material.DIAMOND_HOE ||
                type == Material.DIAMOND_PICKAXE || type == Material.DIAMOND_SHOVEL ||
                type == Material.DIAMOND_SWORD || type == Material.NETHERITE_AXE ||
                type == Material.NETHERITE_HOE || type == Material.NETHERITE_PICKAXE ||
                type == Material.NETHERITE_SHOVEL || type == Material.NETHERITE_SWORD;
    }

    public static boolean isLog(Material type) {
        return Tag.LOGS.isTagged(type);
    }

    public static boolean isLeaves(Material type) {
        return Tag.LEAVES.isTagged(type);
    }

    /**
     * Places item in the player inventory, or drops it if their inventory is full
     * @param player
     * @param item
     */
    public static void giveOrDropItem(Player player, ItemStack item) {
        HashMap<Integer, ItemStack> didntFit = player.getInventory().addItem(item);
        if(!didntFit.isEmpty()) {
            for (ItemStack extra : didntFit.values()) {
                player.getWorld().dropItem(player.getLocation(), extra);
            }
        }
    }
}
