package net.millo.millomod.mod.features.impl;

import net.millo.millomod.MilloMod;
import net.millo.millomod.config.Config;
import net.millo.millomod.mod.features.Feature;
import net.millo.millomod.mod.features.PacketListener;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.text.Text;

public class AutoCommand extends Feature {
    @Override
    public String getKey() {
        return "autocommand";
    }

    @PacketListener
    public boolean onChatSend(ChatMessageC2SPacket packet) {
        if (!enabled) return false;

        String message = packet.chatMessage();
        if (message.startsWith("@")) return false;

        assert MilloMod.MC.player != null;
        MilloMod.MC.player.networkHandler.sendChatMessage("@" + message);
        return true;
    }
}
