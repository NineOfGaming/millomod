package net.millo.millomod.mod.commands.impl;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.millo.millomod.mod.commands.Command;
import net.millo.millomod.mod.commands.ArgBuilder;
import net.millo.millomod.mod.features.viewer.ViewerScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;

public class ViewerCommand extends Command {

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<FabricClientCommandSource> cd, CommandRegistryAccess context) {
        cd.register(ArgBuilder.literal("viewer")
                        .executes(ctx -> {

                            MinecraftClient.getInstance().send(() -> {
                                MinecraftClient.getInstance().setScreen(new ViewerScreen());
                            });

                            return 1;
                        })
        );

    }

}
