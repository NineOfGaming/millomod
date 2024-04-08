package net.millo.millomod.mixin;

import net.millo.millomod.mod.features.FeatureHandler;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugRenderer.class)
public class MDebugRenderer {

    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
        FeatureHandler.onRender(matrices, vertexConsumers, cameraX, cameraY, cameraZ);
    }

}
