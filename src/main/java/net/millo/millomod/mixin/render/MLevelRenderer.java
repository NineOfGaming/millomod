package net.millo.millomod.mixin.render;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.chars.AbstractChar2ShortMap;
import net.millo.millomod.MilloMod;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import java.awt.*;
import java.util.*;
import java.util.List;

// TODO: Currently unused

@Mixin(WorldRenderer.class)
public class MLevelRenderer {

//    @Shadow @Nullable PostEffectProcessor entityOutlinePostProcessor;
//
//    @Unique
//    private final Map<BlockPos, Color> blockEntityOutlineMap = new HashMap<>();
//
//    // Iterator var45 = this.noCullingBlockEntities.iterator();
//    @WrapOperation(method = "render", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD,
//        target = "Lnet/minecraft/client/render/WorldRenderer;noCullingBlockEntities:Ljava/util/Set;",
//        ordinal = 1))
//    private Set<BlockEntity> interceptGlobalBlockEntities(WorldRenderer instance, Operation<Set<BlockEntity>> operation) {
//        Set<BlockEntity> blockEntities = operation.call(instance);
//        if (blockEntities.isEmpty()) return blockEntities;
//
//        return blockEntities;
////        return Set.copyOf(something(blockEntities, null));
//    }
//
//    public void processOutlines(float partialTick) {
//        if (entityOutlinePostProcessor == null || blockEntityOutlineMap.isEmpty()) return;
//        entityOutlinePostProcessor.render(partialTick);
//        MilloMod.MC.getFramebuffer().beginWrite(false);
//    }
//
////    private @NotNull List<BlockEntity> something(Collection< ? extends BlockEntity> blockEntities, ChunkSectionPos sectionPos) {
////
////        blockEntityOutlineMap.putAll()
////
////    }

}
