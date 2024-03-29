package net.millo.millomod.mod.features.impl;

import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.millo.millomod.MilloMod;
import net.millo.millomod.config.Config;
import net.millo.millomod.mod.features.Feature;
import net.millo.millomod.mod.features.Keybound;
import net.millo.millomod.mod.features.PacketListener;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;

public class AutoCommand extends Feature implements Keybound {
    @Override
    public String getKey() {
        return "auto_command";
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


    KeyBinding toggle;

    @Override
    public void loadKeybinds() {
        toggle = KeyBindingRegistryImpl.registerKeyBinding(
                new KeyBinding(
                        "key.millo.toggle_auto_command",
                        InputUtil.Type.KEYSYM,
                        -1,
                        "key.category.millo"
                )
        );
    }

    @Override
    public void triggerKeybind(Config config) {
        while (toggle.wasPressed()) {
            boolean state = config.get(getKey() + ".enabled");
            config.set(getKey() + ".enabled", !state);
        }
    }

    @Override
    public boolean disabledByDefault() {
        return true;
    }
}
