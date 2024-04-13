package net.millo.millomod.mod.commands.impl.savestate;

import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.Vec3d;

public class SaveState {

    private final Vec3d savedPos;
    private final float savedPitch, savedYaw;
    private final NbtList savedItems;

    public SaveState(Vec3d pos, float pitch, float yaw, NbtList items) {
        savedPos = pos;
        savedPitch = pitch;
        savedYaw = yaw;
        savedItems = items;
    }

    public NbtList getItems() {
        return savedItems;
    }

    public float getPitch() {
        return savedPitch;
    }

    public float getYaw() {
        return savedYaw;
    }

    public Vec3d getPos() {
        return savedPos;
    }
}
