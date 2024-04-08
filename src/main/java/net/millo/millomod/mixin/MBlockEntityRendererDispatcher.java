package net.millo.millomod.mixin;


import com.mojang.blaze3d.systems.VertexSorter;
import net.millo.millomod.MilloMod;
import net.millo.millomod.mod.features.FeatureHandler;
import net.millo.millomod.mod.features.impl.Search;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.awt.*;
import java.util.Optional;

@Mixin(BlockEntityRenderDispatcher.class)
public class MBlockEntityRendererDispatcher {


    // net/minecraft/client/gui/DrawContext
    @ModifyVariable(method = "render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    public VertexConsumerProvider render(VertexConsumerProvider provider, BlockEntity blockEntity) {
        if (blockEntity instanceof SignBlockEntity) {
            boolean glow = ((Search) FeatureHandler.getFeature("search")).shouldIGlow((SignBlockEntity) blockEntity);

            if (MilloMod.MC.player != null && blockEntity.getPos().isWithinDistance(MilloMod.MC.player.getEyePos(), 2.5d)) {
                glow = false;
            }

            if (glow) {
                return withOutline(provider, Color.YELLOW);
            }
        }
        return provider;
    }


    @Unique
    private VertexConsumerProvider withOutline(VertexConsumerProvider source, Color color) {
        return (type) -> {
            VertexConsumer buffer = source.getBuffer(type);
            Optional<RenderLayer> outline = type.getAffectedOutline();
            if (outline.isPresent()) {
                OutlineVertexConsumerProvider outlineSource = MilloMod.MC.getBufferBuilders().getOutlineVertexConsumers();
                outlineSource.setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
                VertexConsumer outlineBuffer = outlineSource.getBuffer(outline.get());
                return VertexConsumers.union(outlineBuffer, buffer);
            }
            return buffer;
        };
    }
}
