package net.millo.millomod.mixin.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.millo.millomod.MilloMod;
import net.millo.millomod.mod.features.FeatureHandler;
import net.millo.millomod.mod.features.impl.global.sidechat.HudWithSideChat;
import net.millo.millomod.mod.features.impl.global.sidechat.SideChat;
import net.millo.millomod.mod.features.impl.global.sidechat.SideChatFeature;
import net.millo.millomod.mod.util.RenderInfo;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(net.minecraft.client.gui.hud.InGameHud.class)
public abstract class MInGameHUD implements HudWithSideChat {


    @Shadow public abstract TextRenderer getTextRenderer();

    @Shadow private int scaledWidth;

    @Shadow private int scaledHeight;

    @Unique
    private final SideChat sideChat = new SideChat();

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

    @WrapOperation(method = "render", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/hud/ChatHud;render(Lnet/minecraft/client/gui/DrawContext;III)V"))
    private void renderSideChat(ChatHud mainChat, DrawContext context, int tickDelta, int x, int y, Operation<Void> operation) {
        operation.call(mainChat, context, tickDelta, x, y);

        if (FeatureHandler.getFeature("side_chat").isEnabled()) sideChat.render(context, tickDelta, x, y);
    }


    @NotNull
    public SideChat millomod$getSideChat() {
        return sideChat;
    }
}
