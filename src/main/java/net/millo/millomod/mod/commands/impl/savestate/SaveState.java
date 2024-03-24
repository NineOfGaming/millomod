package net.millo.millomod.mod.commands.impl.savestate;

import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.Vec3d;

public class SaveState {

    private Vec3d savedPos;
    private float savedPitch, savedYaw;
    private NbtList savedItems;

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
