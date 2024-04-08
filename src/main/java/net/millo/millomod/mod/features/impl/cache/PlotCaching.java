package net.millo.millomod.mod.features.impl.cache;

import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.millo.millomod.config.Config;
import net.millo.millomod.mod.features.Feature;
import net.millo.millomod.mod.features.Keybound;
import net.millo.millomod.mod.features.PacketListener;
import net.millo.millomod.mod.hypercube.template.Template;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;

public class PlotCaching extends Feature implements Keybound {

    Template cachedTemplate;
    private KeyBinding displayKey;

    @Override
    public String getKey() {
        return "plot_caching";
    }




    @PacketListener
    public boolean slotUpdate(ScreenHandlerSlotUpdateS2CPacket slot) {
        NbtCompound nbt = slot.getStack().getNbt();
        if (nbt == null) return false;

        NbtCompound bukkitValues = nbt.getCompound("PublicBukkitValues");
        if (bukkitValues == null || !bukkitValues.contains("hypercube:codetemplatedata", NbtElement.STRING_TYPE)) return false;
        String codeTemplateData = bukkitValues.getString("hypercube:codetemplatedata");

        cachedTemplate = Template.parseItem(codeTemplateData);

        return true;
    }

    @Override
    public void loadKeybinds() {
        displayKey = KeyBindingRegistryImpl.registerKeyBinding(
                new KeyBinding(
                        "key.millo.display_cache",
                        InputUtil.Type.KEYSYM,
                        -1,
                        "key.category.millo"
                )
        );
    }

    @Override
    public void triggerKeybind(Config config) {
        while (displayKey.wasPressed()) {
            new CacheGUI(cachedTemplate).open();
        }
    }
}
