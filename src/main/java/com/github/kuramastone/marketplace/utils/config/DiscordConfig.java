package com.github.kuramastone.marketplace.utils.config;

import com.github.kuramastone.bUtilities.YamlConfig;

public class DiscordConfig {
    @YamlConfig.YamlKey("webhook.enabled")
    public boolean enabled;
    @YamlConfig.YamlKey("webhook.url")
    public String discordWebhookURL;

    @YamlConfig.YamlKey(value = "webhook.avatarURL", required = false)
    public String discordWebhookAvatarURL;
    @YamlConfig.YamlKey("webhook.username")
    public String discordWebhookUsername;
    @YamlConfig.YamlKey("webhook.content")
    public String discordWebhookContent;
}
