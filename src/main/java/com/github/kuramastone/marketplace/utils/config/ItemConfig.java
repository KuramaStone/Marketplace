package com.github.kuramastone.marketplace.utils.config;

import com.github.kuramastone.bUtilities.ComponentEditor;
import com.github.kuramastone.bUtilities.YamlConfig;
import com.github.kuramastone.marketplace.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ItemConfig {

    @YamlConfig.YamlKey("material")
    private String type;
    @YamlConfig.YamlKey(value = "amount", required = false)
    private int count = 1;
    @YamlConfig.YamlKey(value = "name", required = false)
    private String name;

    @YamlConfig.YamlKey(value = "lore", required = false)
    private List<String> lore;

    @YamlConfig.YamlKey(value = "enchanted-effect", required = false)
    private boolean hasEnchantedEffect = false;

    @YamlConfig.YamlKey(value = "modeldata", required = false)
    private int modeldata = 0;

    /*
    Used to store extra information
     */
    @YamlConfig.YamlKey(value = "tag", required = false)
    private String tag;

    // Default constructor
    public ItemConfig() {

    }

    // Parameterized constructor
    public ItemConfig(String type, int count, String name, List<String> lore) {
        this.type = type;
        this.count = count;
        this.name = name;
        this.lore = lore;
    }

    public ItemStack toItemStack() {
        Material item = getItem();

        // Create an ItemStack with the specified count
        ItemStack itemStack = new ItemStack(item, count);

        // if it is air without it inputting air, something went wrong.
        if (itemStack.isEmpty() && !(type.equalsIgnoreCase("minecraft:air") || type.equalsIgnoreCase("air"))) {
            throw new RuntimeException(String.format("Failed to find item type '%s' or 'minecraft:%s'", type, type));
        }


        // Add lore if provided
        if (lore != null && !lore.isEmpty()) {
            ItemUtils.setLore(itemStack, this.lore);
        }

        // Set the display name if provided
        if (name != null && !name.isEmpty()) {
            itemStack.editMeta(meta -> meta.displayName(ComponentEditor.decorateComponent("&r" + name)));
        }

        if (hasEnchantedEffect) {
            ItemUtils.applyEnchantedEffect(itemStack);
        }

        if(modeldata != 0) {
            itemStack.editMeta(meta -> meta.setCustomModelData(modeldata));
        }

        return itemStack;

    }

    public String getTag() {
        return tag;
    }

    public Material getItem() {
        try {
            return Material.valueOf(type);
        }
        catch (Exception e) {
            throw new RuntimeException("Item type '" + type + "' does not exist.");
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemConfig that = (ItemConfig) o;
        return count == that.count && Objects.equals(type, that.type) && Objects.equals(name, that.name) && Objects.equals(lore, that.lore);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, count, name, lore);
    }

    public ItemConfig copy() {
        return new ItemConfig(type, count, name, lore == null ? null : new ArrayList<>(lore));
    }
}
