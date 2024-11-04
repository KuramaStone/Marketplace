package com.github.kuramastone.marketplace.commands;

import com.github.kuramastone.bUtilities.commands.SubCommand;
import com.github.kuramastone.marketplace.MarketplaceAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MarketplaceCommand extends SubCommand<MarketplaceAPI> {

    public MarketplaceCommand(MarketplaceAPI api, SubCommand<MarketplaceAPI> parent, int argumentLocation, String subcommand) {
        super(api, parent, argumentLocation, subcommand);
        setPermission("marketplace.view");
        setDescription(api.getMessage("commands.%s.description".formatted(getSubcommand())).getText());
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if(!(sender instanceof Player player)) {
            sender.sendMessage(api.getMessage("commands.requires player").build());
            return true;
        }
        if(getPermission() == null || !sender.hasPermission(getPermission())) {
            sender.sendMessage(api.getMessage("commands.insufficient permissions").build());
            return true;
        }

        api.getGuiManager().showMarketplaceTo(player);

        return true;
    }
}
