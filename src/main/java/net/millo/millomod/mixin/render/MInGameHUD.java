package net.millo.millomod.mixin.render;

import net.millo.millomod.MilloMod;
import net.millo.millomod.mod.features.FeatureHandler;
import net.millo.millomod.mod.util.RenderInfo;
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

    @Shadow private int scaledWidth;

    @Shadow private int scaledHeight;

    @Inject(method = "render", at = @At("RETURN"))
    private void render(DrawContext context, float tickDelta, CallbackInfo ci) {
        FeatureHandler.renderHUD(new RenderInfo(context,
                tickDelta,
                ci,
                scaledWidth,
                scaledHeight,
                MilloMod.MC.getLastFrameDuration(),
                getTextRenderer()));
    }

}
