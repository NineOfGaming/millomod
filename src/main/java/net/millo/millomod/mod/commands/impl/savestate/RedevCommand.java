package net.millo.millomod.mod.commands.impl.savestate;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.millo.millomod.mod.commands.Command;
import net.millo.millomod.mod.commands.ArgBuilder;
import net.millo.millomod.mod.features.impl.teleport.TeleportHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;

public class RedevCommand extends Command {

    public enum ALLOWED_STATES {
        Alpha,
        Beta,
        Gamma,
        Delta,
        Epsilon
    }

    private static final HashMap<ALLOWED_STATES, SaveState> saveStates = new HashMap<>();

    public static int save(ClientPlayerEntity player) {
        return save(player, ALLOWED_STATES.Alpha);
    }
    public static int save(ClientPlayerEntity player, ALLOWED_STATES stateSlot) {
        Vec3d savedPos = player.getPos();
        float savedPitch = player.getPitch();
        float savedYaw = player.getYaw();

        NbtList savedItems = new NbtList();
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stackInSlot = player.getInventory().getStack(i);
            if (!stackInSlot.isEmpty()) {
                NbtCompound itemTag = new NbtCompound();
                itemTag.putInt("Slot", i);
                NbtCompound itemNbt = new NbtCompound();
                itemNbt.putString("id", stackInSlot.getItem().toString());
                itemNbt.putByte("Count", (byte) stackInSlot.getCount());
                itemNbt.put("tag", stackInSlot.getOrCreateNbt());
                itemTag.put("Item", itemNbt);
                savedItems.add(itemTag);
            }
        }
        saveStates.put(stateSlot, new SaveState(savedPos, savedPitch, savedYaw, savedItems));

        return 1;
    }

    private static void load(ClientPlayerEntity player, boolean loadInventory) {
        load(player, ALLOWED_STATES.Alpha, loadInventory);
    }
    private static void load(ClientPlayerEntity player, ALLOWED_STATES stateSlot, boolean loadInventory) {
        if (!player.isCreative()) return;

        SaveState state = saveStates.get(stateSlot);

        Vec3d savedPos = state.getPos();
        NbtList savedItems = state.getItems();

        Vec3d initialPos = new Vec3d(player.getPos().x, player.getPos().y, player.getPos().z);

        player.getAbilities().flying = true;
        player.setPitch(state.getPitch());
        player.setYaw(state.getYaw());
        player.setVelocity(0, 0, 0);

        if (loadInventory) {
            player.getInventory().clear();
            for (int i = 0; i < savedItems.size(); i++) {
                NbtCompound itemTag = savedItems.getCompound(i);
                int slot = itemTag.getInt("Slot");

                if (slot >= 0 && slot < player.getInventory().size()) {
                    ItemStack stackInSlot = ItemStack.fromNbt(itemTag.getCompound("Item"));
                    player.getInventory().setStack(slot, stackInSlot);
                }
            }
        }

        TeleportHandler.teleportTo(savedPos);
    }


    private int runCmd(MinecraftClient mc, ALLOWED_STATES stateSlot, boolean loadInv) {
        assert mc.player != null;
        if (saveStates.containsKey(stateSlot)) {
            load(mc.player, stateSlot, loadInv);
        }
        return 1;
    }
    @Override
    public void register(MinecraftClient mc, CommandDispatcher<FabricClientCommandSource> cd, CommandRegistryAccess context) {
        LiteralArgumentBuilder<FabricClientCommandSource> builder = ArgBuilder.literal("redev")
                .executes(ctx -> runCmd(mc, ALLOWED_STATES.Alpha, false));

        for (ALLOWED_STATES state : ALLOWED_STATES.values()) {
            LiteralArgumentBuilder<FabricClientCommandSource> subCmd =  ArgBuilder.literal(state.name().toLowerCase())
                    .executes(ctx -> runCmd(mc, state, false))
                    .then(ArgBuilder.literal("-i")
                            .executes(ctx -> runCmd(mc, state, true)));
            builder.then(subCmd);
        }

        cd.register(builder);
    }

    @Override
    public String getKey() {
        return "redev";
    }

}
