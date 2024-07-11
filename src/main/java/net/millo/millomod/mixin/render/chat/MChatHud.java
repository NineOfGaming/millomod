package net.millo.millomod.mixin.render.chat;


import net.millo.millomod.MilloMod;
import net.millo.millomod.mod.features.impl.global.sidechat.HudWithSideChat;
import net.millo.millomod.mod.features.impl.global.sidechat.SideChat;
import net.minecraft.client.gui.hud.ChatHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class MChatHud {

    @ModifyVariable(method = "toChatLineX", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private double overrideToChatLineX(double x) {
        if (isMainChat()) return x;
        x -= getSideChat().getXOffset();
        return x;
    }

    @Inject(method = "tickRemovalQueueIfExists", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (isMainChat()) getSideChat().tickRemovalQueueIfExists();
    }

    @Inject(method = "scroll", at = @At("HEAD"))
    private void scroll(int scroll, CallbackInfo ci) {
        if (isMainChat()) getSideChat().scroll(scroll);
    }

    @Inject(method = "clear", at = @At("HEAD"))
    private void clear(boolean clearHistory, CallbackInfo ci) {
        if (isMainChat()) getSideChat().clear(clearHistory);
    }

    @Unique
    private boolean isMainChat() {
        return (Object) this == MilloMod.MC.inGameHud.getChatHud();
    }
    @Unique
    private SideChat getSideChat() {
        return ((HudWithSideChat) MilloMod.MC.inGameHud).millomod$getSideChat();
    }

}
