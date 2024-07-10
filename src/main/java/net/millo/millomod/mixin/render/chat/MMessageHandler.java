package net.millo.millomod.mixin.render.chat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.millo.millomod.MilloMod;
import net.millo.millomod.mod.features.impl.global.sidechat.HudWithSideChat;
import net.millo.millomod.mod.features.impl.global.sidechat.SideChat;
import net.millo.millomod.mod.features.impl.global.sidechat.SideChatFeature;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MessageHandler.class)
public class MMessageHandler {

    @WrapOperation(method = "processChatMessageInternal", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V"))
    private void theCourtRoom(ChatHud hud, Text text, MessageSignatureData msd, MessageIndicator indicator, Operation<Void> operation) {
        if (matchSideFilter(text)) {
            getSideChat().addMessage(text, msd, indicator);
            return;
        }
        operation.call(hud, text, msd, indicator);
    }


    @WrapOperation(method={"onGameMessage", "method_45745"}, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;)V"))
    private void theBlackCourtRoom(ChatHud hud, Text text, Operation<Void> operation) {
        if (matchSideFilter(text)) {
            getSideChat().addMessage(text);
            return;
        }
        operation.call(hud, text);
    }

    @Unique
    private boolean matchSideFilter(Text text) {
        return (SideChatFeature.fitsFilter(text));
    }

    @Unique
    private SideChat getSideChat() {
        HudWithSideChat gui = (HudWithSideChat) MilloMod.MC.inGameHud;
        return gui.millomod$getSideChat();
    }


}
