package net.millo.millomod.mod.features.impl.coding;

import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.millo.millomod.mod.features.Feature;
import net.millo.millomod.mod.features.Keybound;
import net.millo.millomod.mod.util.GlobalUtil;
import net.millo.millomod.system.Config;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class ShowTags extends Feature implements Keybound {

    private KeyBinding showKey;

    @Override
    public String getKey() {
        return "show_tags";
    }

    public boolean isPressed() {
        return GlobalUtil.isKeyDown(showKey);
    }

    @Override
    public void loadKeybinds() {
        showKey = KeyBindingRegistryImpl.registerKeyBinding(
                new KeyBinding(
                        "key.millo.show_tags",
                        InputUtil.Type.KEYSYM,
                        -1,
                        "key.category.millo"
                )
        );
    }

    @Override
    public void triggerKeybind(Config config) {}
}
