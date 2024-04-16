package net.millo.millomod.mod.commands.impl;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.millo.millomod.mod.commands.ArgBuilder;
import net.millo.millomod.mod.commands.Command;
import net.millo.millomod.mod.features.gui.SettingsGUI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;

public class SettingsCommand extends Command {
    @Override
    public void register(MinecraftClient instance, CommandDispatcher<FabricClientCommandSource> cd, CommandRegistryAccess context) {
        cd.register(ArgBuilder.literal("settings")
                .executes(ctx -> {
                    new SettingsGUI().open();
                    return 1;
                })
        );
    }

    @Override
    public String getKey() {
        return "settings";
    }
}
