package net.millo.millomod.mod.commands.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.millo.millomod.mod.commands.ArgBuilder;
import net.millo.millomod.mod.commands.Command;
import net.millo.millomod.mod.features.gui.ColorsGUI;
import net.millo.millomod.mod.features.gui.MilloGUI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;

public class MilloModCommand extends Command {
    @Override
    public void register(MinecraftClient instance, CommandDispatcher<FabricClientCommandSource> cd, CommandRegistryAccess context) {

        LiteralCommandNode<FabricClientCommandSource> node = cd.register(ArgBuilder.literal("millomod")
                .executes(ctx -> {
                    new MilloGUI().open();
                    return 1;
                })
        );
        cd.register(ArgBuilder.literal("millo").redirect(node));
    }

}
