package com.github.kuramastone.marketplace.player;

import com.github.kuramastone.marketplace.Marketplace;
import com.github.kuramastone.marketplace.MarketplaceAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Upload and download player profiles on quit and join
 */
public class JoinQuitListener implements Listener {

    private MarketplaceAPI api;

    public JoinQuitListener(MarketplaceAPI api) {
        this.api = api;
    }

    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent event) {
        // download profile in preparation
        api.getOrCreateProfile(event.getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        // upload on quit
        api.uploadPlayerProfile(api.getProfile(event.getPlayer().getUniqueId()));
    }

    public void register(Marketplace plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

}
