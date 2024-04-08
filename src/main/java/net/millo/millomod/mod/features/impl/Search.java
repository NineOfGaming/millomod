package net.millo.millomod.mod.features.impl;

import net.millo.millomod.MilloMod;
import net.millo.millomod.mod.features.Feature;
import net.millo.millomod.mod.features.IWorldRenderable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.util.shape.VoxelShape;

import java.awt.*;
import java.util.Objects;

public class Search extends Feature implements IWorldRenderable {
    @Override
    public String getKey() {
        return "search";
    }


    public boolean shouldIGlow(SignBlockEntity sign) {
        String first = sign.getText(true).getMessage(0, false).getString();
        String second = sign.getText(true).getMessage(1, false).getString();

        return false;

        // TODO: this

//        if (Objects.equals(first, "SET VARIABLE")) {
//            return Objects.equals(second, "=") || Objects.equals(second, "+=");
//
//        }
//
//        return false;
    }


    @Override
    public void renderWorld(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, double cameraX, double cameraY, double cameraZ) {
//        BlockPos pos = new BlockPos(2, 60, 2);
//        VoxelShape shape = MilloMod.MC.world.getBlockState(pos).getOutlineShape(MilloMod.MC.world, pos).offset(pos.getX(), pos.getY(), pos.getZ());
//        VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getLines());
//        int color = Color.CYAN.hashCode();
//        WorldRenderer.drawShapeOutline(matrices, consumer, shape, -cameraX, -cameraY, -cameraZ, (float) (color >> 16) / 255, (float) ((color & 0xFF00) >> 8) / 255, (float) (color & 0x0000FF) / 255, 1, true);
    }
}
