package com.github.kuramastone.marketplace.commands;

import com.github.kuramastone.bUtilities.commands.SubCommand;
import com.github.kuramastone.marketplace.MarketplaceAPI;
import com.github.kuramastone.marketplace.player.PlayerProfile;
import com.github.kuramastone.marketplace.player.TransactionEntry;
import com.github.kuramastone.marketplace.storage.ItemEntry;
import com.github.kuramastone.marketplace.storage.ItemEntryData;
import com.github.kuramastone.marketplace.utils.ItemUtils;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.codehaus.plexus.util.StringUtils;

public class SellCommand extends SubCommand<MarketplaceAPI> {

    public SellCommand(MarketplaceAPI api, SubCommand<MarketplaceAPI> parent, int argumentLocation, String subcommand) {
        super(api, parent, argumentLocation, subcommand);
        setPermission("marketplace.sell");
        setDescription(api.getMessage("commands.%s.description".formatted(getSubcommand())).getText());
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(api.getMessage("commands.requires player").build());
            return false;
        }
        if (getPermission() == null || !sender.hasPermission(getPermission())) {
            sender.sendMessage(api.getMessage("commands.insufficient permissions").build());
            return true;
        }

        if (args.length < getArgumentLocation() + 1) {
            sender.sendMessage("Usage: /%s [price]".formatted(getFullCommandString()));
            return false;
        }

        String stringPrice = args[getArgumentLocation()];
        double price = -1;
        try {
            price = Double.parseDouble(stringPrice);
        }
        catch (NumberFormatException e) {
            sender.sendMessage(api.getMessage("commands.invalid number", "{value}", price).build());
            return false;
        }

        // round price to the floor of its penny
        price = Math.floor(price * 100) / 100.0D;

        if (price <= 0) {
            sender.sendMessage(api.getMessage("commands.sell.failure.invalid price", "{amount}", price).build());
            return false;
        }

        ItemStack itemstack = getItemFromHands(player);
        if (itemstack.isEmpty()) {
            sender.sendMessage(api.getMessage("commands.sell.failure.invalid item").build());
            return false;
        }

        PlayerProfile profile = api.getOrCreateProfile(player.getUniqueId());
        player.getInventory().remove(itemstack);

        long listTime = System.currentTimeMillis();
        ItemEntry itemEntry = new ItemEntry(profile, new ItemEntryData(profile.getUUID(), itemstack, price, listTime));
        TransactionEntry te = profile.addNewTransaction(itemEntry, listTime); // add to player history
        api.addItemToMarketplace(itemEntry);

        sender.sendMessage(api.getMessage("commands.sell.success",
                "{name}", ItemUtils.getItemStackName(itemstack),
                "{amount}", itemstack.getAmount()).build());


        return true;
    }

    private ItemStack getItemFromHands(Player player) {
        ItemStack itemstack = player.getInventory().getItem(EquipmentSlot.HAND);
        if (itemstack.isEmpty()) {
            itemstack = player.getInventory().getItem(EquipmentSlot.OFF_HAND);
        }
        return itemstack;
    }
}
