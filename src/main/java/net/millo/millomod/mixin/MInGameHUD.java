package net.millo.millomod.mixin;

import net.millo.millomod.mod.features.FeatureHandler;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(net.minecraft.client.gui.hud.InGameHud.class)
public abstract class MInGameHUD {


    @Shadow public abstract TextRenderer getTextRenderer();

    @Inject(method = "render", at = @At("RETURN"))
    private void render(DrawContext context, float tickDelta, CallbackInfo ci) {
        FeatureHandler.renderHUD(context, tickDelta, getTextRenderer());
    }

}
