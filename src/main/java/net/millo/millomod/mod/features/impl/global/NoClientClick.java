package net.millo.millomod.mod.features.impl.global;

import net.millo.millomod.mod.features.Feature;
import net.millo.millomod.mod.features.impl.util.Tracker;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import java.util.Set;

public class NoClientClick extends Feature {
    @Override
    public String getKey() {
        return "no_client_click";
    }

    public boolean shouldCancel(ItemStack stack) {
        if (!isEnabled() || Tracker.mode != Tracker.Mode.PLAY) return false;

        NbtCompound nbt = stack.getNbt();
        if (nbt == null) return false;
        NbtCompound pbv = nbt.getCompound("PublicBukkitValues");
        if (pbv == null) return false;

        Set<String> keys = pbv.getKeys();
        if (keys.isEmpty()) return false;

        return keys.contains("hypercube:noclientclick");
    }


}
