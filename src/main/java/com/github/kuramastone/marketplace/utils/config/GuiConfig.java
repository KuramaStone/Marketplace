package com.github.kuramastone.marketplace.utils.config;

import com.github.kuramastone.bUtilities.YamlConfig;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gui info storage. Loadable directly through a yamlsection
 */
public class GuiConfig {

    private Map<Character, ItemConfig> ingredients;
    private List<String> structure;
    private String windowName;

    public GuiConfig(YamlConfig section) {
        windowName = section.getString("window name");
        structure = section.getStringList("structure");

        ingredients = new HashMap<>();
        for(String strChar : section.getKeys("ingredients", false)) {
            char c = strChar.charAt(0);
            ItemConfig ic = YamlConfig.loadFromYaml(new ItemConfig(), section.getSection("ingredients.%s".formatted(strChar)));
            ingredients.put(c, ic);
        }
    }

    public String[] getStructure() {
        return structure.toArray(new String[0]);
    }

    public int contentItemsPerPage() {
        int total = 0;

        for (String string : structure) {
            for (char c : string.toCharArray()) {
                if (c == getListCharacter()) {
                    total++;
                }
            }
        }

        return total;
    }


    public Map<Character, ItemConfig> getIngredients() {
        return ingredients;
    }

    public String getWindowName() {
        return windowName;
    }


    public char getListCharacter() {
        return '.';
    }
}
