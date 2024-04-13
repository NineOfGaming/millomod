package net.millo.millomod.mixin;

import net.millo.millomod.mod.features.FeatureHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class MClientCommonNetworkHandler {

    @Inject(method = "handlePacket", at = @At("HEAD"), cancellable = true)
    private static <T extends PacketListener> void handlePacket(Packet<T> packet, PacketListener listener, CallbackInfo ci) {
        if (FeatureHandler.handlePacket(packet)) ci.cancel();
    }

}
