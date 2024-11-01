package com.github.kuramastone.marketplace.commands;

import com.github.kuramastone.bUtilities.commands.SubCommand;
import com.github.kuramastone.marketplace.MarketplaceAPI;
import com.github.kuramastone.marketplace.player.PlayerProfile;
import com.github.kuramastone.marketplace.storage.ItemEntry;
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
            // handle during check later
        }

        if (price <= 0) {
            sender.sendMessage(api.getMessage("commands.sell.failure.invalid price").build());
            return false;
        }

        ItemStack itemstack = getItemFromHands(player);
        if (itemstack.isEmpty()) {
            sender.sendMessage(api.getMessage("commands.sell.failure.invalid item").build());
            return false;
        }

        PlayerProfile profile = api.getOrCreateProfile(player.getUniqueId());
        ItemEntry itemEntry = new ItemEntry(profile, itemstack, price);
        api.addItemToMarketplace(profile, itemEntry);

        sender.sendMessage(api.getMessage("commands.sell.success",
                "{name}", getItemStackName(itemstack),
                "{amount}", itemstack.getAmount()).build());


        return true;
    }

    /**
     * Get the itemstacks displayName or formatted material name
     *
     * @param itemstack
     * @return
     */
    private String getItemStackName(ItemStack itemstack) {
        if (itemstack.hasItemMeta()) {
            if (itemstack.getItemMeta().hasDisplayName()) {
                String plainText = PlainTextComponentSerializer.plainText().serialize(itemstack.getItemMeta().displayName());
                return plainText;
            }
        }

        String name = itemstack.getType().toString().toLowerCase().replace("_", " ");
        return StringUtils.capitaliseAllWords(name);
    }

    private ItemStack getItemFromHands(Player player) {
        ItemStack itemstack = player.getInventory().getItem(EquipmentSlot.HAND);
        if (itemstack.isEmpty()) {
            itemstack = player.getInventory().getItem(EquipmentSlot.OFF_HAND);
        }
        return itemstack;
    }
}
