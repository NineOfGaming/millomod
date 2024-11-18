package net.millo.millomod.mod.commands.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.millo.millomod.mod.commands.ArgBuilder;
import net.millo.millomod.mod.commands.Command;
import net.millo.millomod.system.PlayerUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.ItemStack;

public class DfGiveCommand extends Command {
    @Override
    public void register(MinecraftClient mc, CommandDispatcher<FabricClientCommandSource> cd, CommandRegistryAccess context) {
        cd.register(ArgBuilder.literal("dfgive")
                        .then(ArgBuilder.argument("item", ItemStackArgumentType.itemStack(context))
                                .then(ArgBuilder.argument("count", IntegerArgumentType.integer(1))
                                        .executes(ctx -> {
                                            if (mc.player == null) return -1;
                                            giveItem(mc.player, ItemStackArgumentType.getItemStackArgument(ctx, "item").createStack(IntegerArgumentType.getInteger(ctx, "count"), false));
                                            return 1;
                                        })
                                )
                                .executes(ctx -> {
                                    if (mc.player == null) return -1;
                                    giveItem(mc.player, ItemStackArgumentType.getItemStackArgument(ctx, "item").createStack(1, false));
                                    return 1;
                                })
                        )
                        .then(ArgBuilder.literal("clipboard")
                                .executes(ctx -> {
                                    String clipboard;
                                    clipboard = mc.keyboard.getClipboard();
                                    clipboard = clipboard.replaceAll("^\\/?(df)?(give )?(@p )?", "");

                                    if (mc.player == null) return -1;
                                    mc.player.networkHandler.sendCommand("dfgive " + clipboard.trim());

                                    return 1;
                                })
                        )
        );
    }

    private void giveItem(ClientPlayerEntity player, ItemStack item) {
        if (!player.isCreative()) return;
        item.setCount(Math.min(item.getCount(), item.getMaxCount()));
        PlayerUtil.giveItem(item);
    }

    @Override
    public String getKey() {
        return "dfgive";
    }
}
