package com.github.kuramastone.marketplace.utils.config;

import com.github.kuramastone.bUtilities.ComponentEditor;
import com.github.kuramastone.bUtilities.YamlConfig;
import com.github.kuramastone.marketplace.utils.GuiManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigOptions {

    /**
     * Easy access to formatted messages from the config by their key
     */
    public Map<String, ComponentEditor> messages;
    public GuiManager guiManager;

    public void loadConfig(YamlConfig config) {
        guiManager = new GuiManager();

        loadMessages(config);
        loadMisc(config);
        guiManager.load(config);
    }

    private void loadMisc(YamlConfig config) {
    }

    /**
     * Loads all keys under "messages" and stores them for easy retrieval elsewhere
     */
    public void loadMessages(YamlConfig config) {
        messages = new HashMap<>();
        config.installNewKeysFromDefault("messages", true);

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
