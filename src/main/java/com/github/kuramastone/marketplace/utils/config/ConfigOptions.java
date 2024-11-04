package com.github.kuramastone.marketplace.utils.config;

import com.github.kuramastone.bUtilities.ComponentEditor;
import com.github.kuramastone.bUtilities.YamlConfig;
import com.github.kuramastone.marketplace.MarketplaceAPI;
import com.github.kuramastone.marketplace.utils.GuiManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigOptions {

    /**
     * Easy access to formatted messages from the config by their key
     */
    public Map<String, ComponentEditor> messages;
    public List<String> marketplaceItemHeaders;
    public double blackMarketDiscount;
    public double blackMarketUseRate;
    public DatabaseConfig database;
    public DiscordConfig discordConfig;

    public void loadConfig(MarketplaceAPI api, YamlConfig config) {
        loadMessages(config);
        database = YamlConfig.loadFromYaml(new DatabaseConfig(), config.getSection("Database"));
        discordConfig = YamlConfig.loadFromYaml(new DiscordConfig(), config.getSection("Discord"));

        marketplaceItemHeaders = config.getStringList("Markets.items.header lore");

        blackMarketDiscount = config.getDouble("Markets.blackmarket.discount percentage") / 100.0D;
        blackMarketUseRate = config.getDouble("Markets.blackmarket.chance to use in blackmarket") / 100.0D;

    }

    /**
     * Loads all keys under "messages" and stores them for easy retrieval elsewhere
     */
    public void loadMessages(YamlConfig config) {
        messages = new HashMap<>();

        for (String subkey : config.getKeys("messages", true)) {
            String key = "messages." + subkey;
            if (config.isSection(key)) {
                continue;
            }

            String string;
            Object obj = config.getObject(key);
            if(obj instanceof List<?> list) {
                string = String.join("\n", list.toArray(new String[0]));
            }
            else if(obj instanceof String objStr) {
                string = objStr;
            }
            else {
                string = obj.toString();
            }

            this.messages.put(subkey, new ComponentEditor(string));
        }
    }

}
