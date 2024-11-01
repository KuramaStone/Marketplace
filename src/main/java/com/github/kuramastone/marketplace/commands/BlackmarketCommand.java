package com.github.kuramastone.marketplace.commands;

import com.github.kuramastone.bUtilities.commands.SubCommand;
import com.github.kuramastone.marketplace.MarketplaceAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BlackmarketCommand extends SubCommand<MarketplaceAPI> {

    public BlackmarketCommand(MarketplaceAPI api, SubCommand<MarketplaceAPI> parent, int argumentLocation, String subcommand) {
        super(api, parent, argumentLocation, subcommand);
        setPermission("marketplace.blackmarket");
        setDescription(api.getMessage("commands.%s.description".formatted(getSubcommand())).getText());
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if(!(sender instanceof Player player)) {
            sender.sendMessage(api.getMessage("commands.requires player").build());
            return true;
        }

        api.getGuiManager().showBlackmarketTo(player);

        return true;
    }
}
