package com.github.kuramastone.marketplace.player;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.UUID;

public class PlayerProfile {

    private final UUID uuid;
    private final OfflinePlayer offlinePlayer;
    private @Nullable Player player;

    public PlayerProfile(UUID uuid) {
        this.uuid = uuid;
        offlinePlayer = Bukkit.getOfflinePlayer(uuid);
    }

    public boolean isOnline() {
        return offlinePlayer.isOnline();
    }

    /**
     * Load a cached instance of {@link Player} if they are online.
     */
    public Player getPlayer() {
        if(isOnline()) {
            player = offlinePlayer.getPlayer();
            return player;
        }

        player = null;
        return null;
    }

}
