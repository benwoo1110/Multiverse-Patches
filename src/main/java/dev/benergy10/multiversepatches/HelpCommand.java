package dev.benergy10.multiversepatches;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.commands.PaginatedCoreCommand;
import com.pneumaticraft.commandhandler.multiverse.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Displays a nice help menu.
 */
public class HelpCommand extends PaginatedCoreCommand<Command> {

    private static final Pattern REGEX_SPECIAL_CHARS = Pattern.compile("[.+*?\\[^\\]$(){}=!<>|:-\\\\]");

    public HelpCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Get Help with Multiverse");
        this.setCommandUsage("/mv " + ChatColor.GOLD + "[FILTER] [PAGE #]");
        this.setArgRange(0, 2);
        this.addKey("mv");
        this.addKey("mvh");
        this.addKey("mvhelp");
        this.addKey("mv help");
        this.addKey("mvsearch");
        this.addKey("mv search");
        this.addCommandExample("/mv help ?");
        this.setPermission("multiverse.help", "Displays a nice help menu.", PermissionDefault.TRUE);
        this.setItemsPerPage(7); // SUPPRESS CHECKSTYLE: MagicNumberCheck
    }

    private String cleanFilter(String filter) {
        return REGEX_SPECIAL_CHARS.matcher(filter).replaceAll("\\\\$0");
    }

    @Override
    protected List<Command> getFilteredItems(List<Command> availableItems, String filter) {
        String expression = "(?i).*" + cleanFilter(filter) + ".*";
        List<Command> filtered = new ArrayList<Command>();

        for (Command c : availableItems) {
            if (stitchThisString(c.getKeyStrings()).matches(expression)
                    || c.getCommandName().matches(expression)
                    || c.getCommandDesc().matches(expression)
                    || c.getCommandUsage().matches(expression)
                    || c.getCommandExamples().stream().anyMatch(eg -> eg.matches(expression))) {
                filtered.add(c);
            }
        }
        return filtered;
    }

    @Override
    protected String getItemText(Command item) {
        return ChatColor.AQUA + item.getCommandUsage();
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        sender.sendMessage(ChatColor.AQUA + "====[ Multiverse Help ]====");

        FilterObject filterObject = this.getPageAndFilter(args);

        List<Command> availableCommands = new ArrayList<>(this.plugin.getCommandHandler().getCommands(sender));
        if (filterObject.getFilter().length() > 0) {
            availableCommands = this.getFilteredItems(availableCommands, filterObject.getFilter());
            if (availableCommands.size() == 0) {
                sender.sendMessage(ChatColor.RED + "Sorry... " + ChatColor.WHITE
                        + "No commands matched your filter: " + ChatColor.AQUA + filterObject.getFilter());
                return;
            }
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.AQUA + " Add a '" + ChatColor.DARK_PURPLE + "?" + ChatColor.AQUA + "' after a command to see more about it.");
            for (Command c : availableCommands) {
                sender.sendMessage(ChatColor.AQUA + c.getCommandUsage());
            }
            return;
        }

        int totalPages = (int) Math.ceil(availableCommands.size() / (this.itemsPerPage + 0.0));

        if (filterObject.getPage() > totalPages) {
            filterObject.setPage(totalPages);
        }

        sender.sendMessage(ChatColor.AQUA + " Page " + filterObject.getPage() + " of " + totalPages);
        sender.sendMessage(ChatColor.AQUA + " Add a '" + ChatColor.DARK_PURPLE + "?" + ChatColor.AQUA + "' after a command to see more about it.");

        this.showPage(filterObject.getPage(), sender, availableCommands);
    }
}
