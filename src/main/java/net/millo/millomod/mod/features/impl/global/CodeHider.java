package net.millo.millomod.mod.features.impl.global;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientBlockEntityEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.impl.event.lifecycle.LoadedChunksCache;
import net.millo.millomod.MilloMod;
import net.millo.millomod.mod.features.Feature;
import net.millo.millomod.mod.features.FeatureHandler;
import net.millo.millomod.mod.features.impl.util.Tracker;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

public class CodeHider extends Feature {


    @Override
    public String getKey() {
        return "code_hider";
    }


    private Vec3d playerPos = new Vec3d(0, 0, 0);

    @Override
    public void onTick() {
        if (MilloMod.MC.player == null) return;
        playerPos = MilloMod.MC.player.getPos();
    }

    public boolean hideBlock(BlockPos pos) {
        boolean withinDev = Tracker.getPlot().isInDevMega(pos.toCenterPos());
        if (!withinDev) return false;

        if (Tracker.mode == Tracker.Mode.PLAY) return true;

        return !pos.toCenterPos().isInRange(playerPos, 15d);
    }

}
