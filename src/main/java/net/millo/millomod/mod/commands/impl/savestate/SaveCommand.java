package net.millo.millomod.mod.commands.impl.savestate;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.millo.millomod.mod.commands.Command;
import net.millo.millomod.mod.commands.ArgBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;

public class SaveCommand extends Command {
    @Override
    public void register(MinecraftClient mc, CommandDispatcher<FabricClientCommandSource> cd, CommandRegistryAccess context) {
        LiteralArgumentBuilder<FabricClientCommandSource> builder = ArgBuilder.literal("save")
                .executes(ctx -> {
                    assert mc.player != null;
                    return RedevCommand.save(mc.player, RedevCommand.ALLOWED_STATES.Alpha);
                });

        for (RedevCommand.ALLOWED_STATES state : RedevCommand.ALLOWED_STATES.values()) {
            LiteralArgumentBuilder<FabricClientCommandSource> subCmd =  ArgBuilder.literal(state.name().toLowerCase())
                    .executes(ctx -> {
                        assert mc.player != null;
                        return RedevCommand.save(mc.player, state);
                    });
            builder.then(subCmd);
        }

        cd.register(builder);


        /*
        cd.register(ArgBuilder.literal("save")
                .then(ArgBuilder.literal("Alpha")
                        .executes(ctx -> RedevCommand.save(mc.player, RedevCommand.ALLOWED_STATES.Alpha)))
                .then(ArgBuilder.literal("Beta")
                        .executes(ctx -> RedevCommand.save(mc.player, RedevCommand.ALLOWED_STATES.Beta)))
                .then(ArgBuilder.literal("Gamma")
                        .executes(ctx -> RedevCommand.save(mc.player, RedevCommand.ALLOWED_STATES.Gamma)))
                .then(ArgBuilder.literal("Delta")
                        .executes(ctx -> RedevCommand.save(mc.player, RedevCommand.ALLOWED_STATES.Delta)))
                .then(ArgBuilder.literal("Epsilon")
                        .executes(ctx -> RedevCommand.save(mc.player, RedevCommand.ALLOWED_STATES.Epsilon)))
                .executes(ctx -> RedevCommand.save(mc.player)));*/
    }
}
