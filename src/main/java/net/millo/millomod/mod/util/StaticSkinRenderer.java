package net.millo.millomod.mod.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.world.ClientWorld;

import java.util.UUID;

public class StaticSkinRenderer extends OtherClientPlayerEntity {
    private final SkinTextures skin;
    private static final UUID uuidVoid = UUID.randomUUID();
    public StaticSkinRenderer(ClientWorld clientWorld, SkinTextures skin) {
        super(clientWorld, new GameProfile(uuidVoid, ""));
        this.skin = skin;
    }

    @Override
    public SkinTextures getSkinTextures() {
        return skin;
    }
}
