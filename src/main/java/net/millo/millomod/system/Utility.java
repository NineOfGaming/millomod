package net.millo.millomod.system;

import net.millo.millomod.MilloMod;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class Utility {

    public static void sendCommand(String command) {
        if (MilloMod.MC.getNetworkHandler() == null) return;

        MilloMod.MC.getNetworkHandler().sendCommand(command);
    }

    public static void sendHandItem(ItemStack item) {
        MilloMod.MC.getNetworkHandler().sendPacket(new CreativeInventoryActionC2SPacket(36 + MilloMod.MC.player.getInventory().selectedSlot, item));
    }

    public static void rightClickPos(BlockPos pos) {
        MilloMod.MC.interactionManager.interactBlock(MilloMod.MC.player, Hand.MAIN_HAND, new BlockHitResult(
                pos.toCenterPos(), Direction.UP, pos, false
        ));
    }

    public static void sendSneak(boolean sneaking) {
        MilloMod.MC.getNetworkHandler().sendPacket(
                new ClientCommandC2SPacket(MilloMod.MC.player,
                        sneaking ?
                                ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY :
                                ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY)
        );
    }

    public static void sendOffhandItem(ItemStack itemStack) {
        MilloMod.MC.getNetworkHandler().sendPacket(new CreativeInventoryActionC2SPacket(45, itemStack));
        MilloMod.MC.player.getInventory().setStack(45, itemStack);
    }
}
