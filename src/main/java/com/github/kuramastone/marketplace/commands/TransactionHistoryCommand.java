package com.github.kuramastone.marketplace.commands;

import com.github.kuramastone.bUtilities.ComponentEditor;
import com.github.kuramastone.bUtilities.commands.SubCommand;
import com.github.kuramastone.marketplace.MarketplaceAPI;
import com.github.kuramastone.marketplace.player.PlayerProfile;
import com.github.kuramastone.marketplace.player.TransactionEntry;
import com.github.kuramastone.marketplace.storage.ItemEntry;
import com.github.kuramastone.marketplace.utils.ItemUtils;
import com.github.kuramastone.marketplace.utils.TimeUtils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.codehaus.plexus.util.StringUtils;

import java.util.List;
import java.util.UUID;

public class TransactionHistoryCommand extends SubCommand<MarketplaceAPI> {

    public TransactionHistoryCommand(MarketplaceAPI api, SubCommand<MarketplaceAPI> parent, int argumentLocation, String subcommand) {
        super(api, parent, argumentLocation, subcommand);
        setPermission("marketplace.history");
        setDescription(api.getMessage("commands.%s.description".formatted(getSubcommand())).getText());
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {


        OfflinePlayer offlinePlayer;

        // try getting sender as target
        if (args.length <= getArgumentLocation()) {
            // console cant be sender
            if (!(sender instanceof Player player)) {
                sender.sendMessage(api.getMessage("commands.requires player").build());
                return false;
            }

            offlinePlayer = player;
        }
        else {
            String targetArg = args[getArgumentLocation()];

            try {
                // try to get as a uuid
                offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(targetArg)); // throws error if not a uuid
            }
            catch (IllegalArgumentException e) {
                // try to get as a player name
                offlinePlayer = Bukkit.getOfflinePlayer(targetArg);
            }

        }

        PlayerProfile profile = api.getProfile(offlinePlayer.getUniqueId());
        if (profile == null) {
            sender.sendMessage(api.getMessage("commands.%s.failure.no history".formatted(getSubcommand()),
                    "{name}", offlinePlayer.getName()).build());
            return false;
        }

        sendTransactionHistory(sender, profile);


        return true;
    }

    /**
     * Takes the configured messages and applies the formatting
     *
     * @param sender
     * @param profile
     */
    private void sendTransactionHistory(CommandSender sender, PlayerProfile profile) {
        StringBuilder transactionLinesBuilder = new StringBuilder();
        Component[] transactionLines = new Component[profile.getTransactionHistory().size()];
        for (int i = 0; i < transactionLines.length; i++) {
            TransactionEntry te = profile.getTransactionHistory().get(i);
            String buyerName = te.hasBeenSold() ?
                    Bukkit.getOfflinePlayer(te.getPurchasedBy()).getName() :
                    "Nobody";
            String buyTimeString = te.hasBeenSold() ?
                    TimeUtils.convertTimeToReadable(te.getTimePurchased()) :
                    "Not Yet";
            String forSellTime = TimeUtils.convertTimeToReadable(te.getTimeSubmitted());

            String messageKey = te.hasBeenSold() ?
                    "commands.%s.success.transaction_line.sold" :
                    "commands.%s.success.transaction_line.unsold";

            ItemStack paperItem = te.getItemEntryData().getItemstack();
            String line = api.getMessage(messageKey.formatted(getSubcommand()),
                    "{material}", ItemUtils.getItemStackName(paperItem),
                    "{count}", paperItem.getAmount(),
                    "{sold_for_price}", te.getPurchasePrice(),
                    "{list_price}", te.getListPrice(),
                    "{buyer}", buyerName,
                    "{for_sell_time}", forSellTime,
                    "{buy_time}", buyTimeString
            ).getText();


            TextComponent component = ComponentEditor.decorateComponent(line)
                    .hoverEvent(paperItem);

            transactionLines[i] = component;

            boolean isLast = (i == transactionLines.length - 1);
            if (!isLast)
                transactionLinesBuilder.append("\n");
        }

        String[] parentLines = api.getMessage("commands.%s.success.full_format".formatted(getSubcommand()),
                "{player}", profile.getOfflinePlayer().getName()).getText().split("\n");

        for (String line : parentLines) {
            if (line.contains("{transaction_lines}")) {
                for (Component transactionLine : transactionLines) {
                    sender.sendMessage(transactionLine);
                }
            }
            else {
                sender.sendMessage(ComponentEditor.decorateComponent(line));
            }
        }
    }

    private ItemStack getItemFromHands(Player player) {
        ItemStack itemstack = player.getInventory().getItem(EquipmentSlot.HAND);
        if (itemstack.isEmpty()) {
            itemstack = player.getInventory().getItem(EquipmentSlot.OFF_HAND);
        }
        return itemstack;
    }
}
