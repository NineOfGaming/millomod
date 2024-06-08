package net.millo.millomod.mod.commands.impl;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.millo.millomod.mod.commands.ArgBuilder;
import net.millo.millomod.mod.commands.Command;
import net.millo.millomod.mod.features.gui.MilloGUI;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.util.math.BlockPos;

public class ScanGayCommand extends Command {
    @Override
    public void register(MinecraftClient instance, CommandDispatcher<FabricClientCommandSource> cd, CommandRegistryAccess context) {

        cd.register(ArgBuilder.literal("gay")
                .executes(ctx -> {
                    var p = ctx.getSource().getPlayer();
                    var world = p.getWorld();
                    for (int x = -9943; x < -9850; x++) {
                        for (int y = 24; y < 85; y++) {
                            for (int z = 9945; z < 10046; z++) {
                                var block = world.getBlockEntity(new BlockPos(x, y, z));
                                if (block instanceof SignBlockEntity sign) {
                                    System.out.println(sign);
                                    System.out.println(sign.getText(true));
                                }
                            }
                        }
                    }

                    return 1;
                })
        );
    }

    @Override
    public String getKey() {
        return "gay";
    }

}
