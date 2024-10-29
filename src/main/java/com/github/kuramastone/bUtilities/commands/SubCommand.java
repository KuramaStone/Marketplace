package com.github.kuramastone.bUtilities.commands;

import com.github.kuramastone.bUtilities.ComponentEditor;
import com.github.kuramastone.bUtilities.SimpleAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Library for easy command tree usage.
 * @param <T> should be the api in usage for all downstream commands
 */
public abstract class SubCommand<T extends SimpleAPI> {

    protected final T api;
    protected final SubCommand<T> parent;
    private final int argumentLocation;
    private final String subcommand;
    protected Map<String, SubCommand<T>> subCommands;

    private String permission = null;
    private String description = null;

    public SubCommand(T api, SubCommand<T> parent, int argumentLocation, String subcommand) {
        this.api = api;
        this.parent = parent;
        this.argumentLocation = argumentLocation;
        this.subcommand = subcommand;

        subCommands = new HashMap<>();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFullCommandString() {
        List<SubCommand<T>> parentsInReverseOrder = new ArrayList<>();

        followParentPath(this, parentsInReverseOrder);
        List<SubCommand<T>> parentsInOrder = new ArrayList<>();
        for (int i = parentsInReverseOrder.size() - 1; i >= 0; i--) {
            parentsInOrder.add(parentsInReverseOrder.get(i));
        }

        StringBuilder sb = new StringBuilder();
        for (SubCommand<T> sub : parentsInOrder) {
            sb.append(sub.getSubcommand()).append(" ");
        }

        return sb.toString().trim();
    }

    private void followParentPath(SubCommand<T> subCommand, List<SubCommand<T>> parentsInReverseOrder) {
        parentsInReverseOrder.add(subCommand);
        if (subCommand.getParent() != null) {
            followParentPath(subCommand.getParent(), parentsInReverseOrder);
        }
    }

    public SubCommand<T> getParent() {
        return parent;
    }

    public void register(JavaPlugin plugin) {
        PluginCommand cmd = plugin.getCommand(subcommand);
        Objects.requireNonNull(cmd, String.format("This subcommand '%s' has not been registered in the plugin.yml!", subcommand));
        cmd.setExecutor((sender, command, label, args) -> execute(sender, args));
        cmd.setTabCompleter((sender, command, label, args) -> tabComplete(sender, args));
    }

    public Double parseDouble(CommandSender sender, String arg) {
        Objects.requireNonNull(arg, "Cannot parse null String as a double.");
        try {
            return Double.parseDouble(arg);
        }
        catch (Exception e) {
            sender.sendMessage("commands.unknown_decimal", "{arg}", arg);
            return null;
        }
    }

    public World parseWorld(CommandSender sender, String arg) {
        Objects.requireNonNull(arg, "Cannot parse null String as a double.");
        World world = Bukkit.getWorld(arg);
        if (world == null) {
            sender.sendMessage("commands.unknown_world", "{arg}", arg);
            return null;
        }

        return world;
    }

    public Location parseLocation(CommandSender sender, String[] args, int start) {
        Objects.requireNonNull(args, "Cannot parse null String as a double.");
        if (args.length - start < 4) {
            throw new RuntimeException("Not enough arguments: " + Arrays.toString(args));
        }
        World world = parseWorld(sender, args[start + 0]);
        Double x = parseDouble(sender, args[start + 1]);
        Double y = parseDouble(sender, args[start + 2]);
        Double z = parseDouble(sender, args[start + 3]);

        if (x == null || y == null || z == null || world == null) {
            return null;
        }

        return new Location(world, x, y, z);
    }

    public List<String> tabCompleteChildren(CommandSender sender, String[] args) {

        if (args.length == getArgumentLocation() + 1) {
            List<String> list = new ArrayList<>();
            for (SubCommand cmd : this.getSubCommandsList()) {
                if (cmd.getPermission() == null || sender.hasPermission(cmd.getPermission())) {
                    list.add(cmd.getSubcommand());
                }
            }

            return predictSuggestionStartingWith(args[getArgumentLocation()], list);
        }
        else {
            String sub = args[getArgumentLocation()];
            for (SubCommand cmd : this.getSubCommandsList()) {
                if (cmd.getPermission() == null || sender.hasPermission(cmd.getPermission())) {
                    if (sub.equalsIgnoreCase(cmd.getSubcommand())) {
                        return cmd.tabComplete(sender, args);
                    }
                }
            }
        }

        return List.of();
    }

    public boolean executeChildren(CommandSender sender, String[] args) {
        String subcmd = args[getArgumentLocation()];

        for (SubCommand sub : this.getSubCommandsList()) {
            if (sub.getPermission() != null &&
                    !sender.hasPermission(sub.getPermission())) {
                sender.sendMessage(api.getMessage("commands.insufficient_permissions").build());
                return true;
            }

            // check if subcommand fits provided details
            if (sub.getSubcommand().equalsIgnoreCase(subcmd)) {
                return sub.execute(sender, args);
            }
        }

        sender.sendMessage(api.getMessage("commands.unknown_command").build());
        return true;
    }

    public void registerSubCommand(SubCommand command) {
        this.subCommands.put(command.getSubcommand(), command);
    }

    public abstract boolean execute(CommandSender sender, String[] args);

    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

    protected Collection<String> getWorldSuggestions() {
        List<String> list = new ArrayList<>();

        for (World world : Bukkit.getWorlds()) {
            list.add(world.getName());
        }

        return list;
    }

    protected List<String> predictSuggestionStartingWith(String prefix, Collection<String> col) {
        List<String> list = new ArrayList<>(col);
        List<String> valid = new ArrayList<>();

        if (prefix != null && !prefix.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).toLowerCase().startsWith(prefix.toLowerCase(

                ))) {
                    valid.add(list.get(i));
                }
            }
        }
        else {
            return list;
        }

        //sort alphabetically
        valid.sort(String::compareTo);

        return valid;
    }

    protected List<String> getPlayersAndTargetSuggestion(CommandSender sender) {
        List<String> list = getPlayerSuggestions(sender);

        if (sender instanceof Player plyr) {
            Entity target = plyr.getTargetEntity(5);
            if (target != null && plyr.canSee(target)) {
                list.add(target.getUniqueId().toString());
            }
        }

        return list;
    }

    protected List<String> getPlayerSuggestions(CommandSender sender) {
        List<String> list = new ArrayList<>();

        Player mainPlayer = null;
        if (sender instanceof Player plyr) {
            mainPlayer = plyr;
        }


        for (Player plyr : sender.getServer().getOnlinePlayers()) {
            if (mainPlayer == null || mainPlayer.canSee(plyr))
                list.add(plyr.getName());
        }

        return list;
    }

    protected UUID getTargetByIdentifier(String identifier) {
        // might be a username
        Entity entity = Bukkit.getServer().getPlayer(identifier);
        if (entity != null) {
            return entity.getUniqueId();
        }

        try {
            UUID uuid = UUID.fromString(identifier);

            // must be an offline player or an entity
            return uuid;

        }
        catch (IllegalArgumentException e) {
            // not a username or uuid.
            return null;
        }

    }

    public int getArgumentLocation() {
        return argumentLocation;
    }

    public String getSubcommand() {
        return subcommand;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public Collection<SubCommand<T>> getSubCommandsList() {
        return subCommands.values();
    }

    public static class DescriptionCommand<T extends SimpleAPI> extends SubCommand<T> {

        public DescriptionCommand(T api, SubCommand parentCommand, int argumentLocation, String subcommand) {
            super(api, parentCommand, argumentLocation, subcommand);
            setDescription("Explains the usage of commands that you can access.");
        }

        @Override
        public boolean execute(CommandSender sender, String[] args) {

            List<String> linesToSend = new ArrayList<>();
            linesToSend.add("&e------- Commands -------");
            // alternate the colors
            String descriptionLine1 = "&7- &6/%s %s: &f%s";
            String descriptionLine2 = "&7- &6/%s %s: &7%s";

            int index = 0;
            for (SubCommand<?> child : parent.getSubCommandsList()) {
                if (child.getPermission() == null || sender.hasPermission(child.getPermission())) {
                    if (child.getDescription() != null) {
                        String descriptionLine = (index % 2 == 0) ? descriptionLine1 : descriptionLine2;
                        linesToSend.add(descriptionLine.formatted(parent.getFullCommandString(), child.getSubcommand(), child.getDescription()));
                        index++;
                    }
                }
            }

            for (String line : linesToSend) {
                sender.sendMessage(ComponentEditor.decorateComponent(line));
            }

            return true;
        }

    }

}