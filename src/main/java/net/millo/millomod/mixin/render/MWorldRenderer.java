package net.millo.millomod.mixin.render;


import net.millo.millomod.mod.features.FeatureHandler;
import net.millo.millomod.mod.features.impl.global.CodeHider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.*;
import java.util.stream.Collectors;


@Mixin(WorldRenderer.class)
public class MWorldRenderer {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/chunk/ChunkBuilder$ChunkData;getBlockEntities()Ljava/util/List;"))
    private List<BlockEntity> getEntities(ChunkBuilder.ChunkData instance) {
        List<BlockEntity> entities = instance.getBlockEntities();
        CodeHider codeHider = (CodeHider) FeatureHandler.getFeature("code_hider");
        if (!codeHider.isEnabled()) return entities;

        return entities.parallelStream()
                .filter(entity -> !codeHider.hideBlock(entity.getPos()))
                .collect(Collectors.toList());
    }

}
