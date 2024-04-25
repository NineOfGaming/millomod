package net.millo.millomod.mod.util;

import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public record RenderInfo(
        DrawContext context,
        float tickDelta,
        CallbackInfo ci,
        int width,
        int height,
        float delta,
        net.minecraft.client.font.TextRenderer textRenderer
) {}
