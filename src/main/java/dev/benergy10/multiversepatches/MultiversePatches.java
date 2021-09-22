package dev.benergy10.multiversepatches;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.pneumaticraft.commandhandler.multiverse.Command;
import com.pneumaticraft.commandhandler.multiverse.CommandHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.List;

public final class MultiversePatches extends JavaPlugin {

    @Override
    public void onEnable() {
        MultiverseCore multiverseCore = (MultiverseCore) Bukkit.getPluginManager().getPlugin("Multiverse-Core");
        CommandHandler commandHandler = multiverseCore.getCommandHandler();

        Field allCommandsField;
        try {
            allCommandsField = commandHandler.getClass().getDeclaredField("allCommands");
            allCommandsField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return;
        }

        List<Command> allCommands;
        try {
            //noinspection unchecked
            allCommands = (List<Command>) allCommandsField.get(commandHandler);
        } catch (IllegalAccessException | ClassCastException e) {
            e.printStackTrace();
            return;
        }

        allCommands.remove(0);
        allCommands.add(0, new HelpCommand(multiverseCore));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
