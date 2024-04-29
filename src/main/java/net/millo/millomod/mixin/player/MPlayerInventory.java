package net.millo.millomod.mixin.player;


import net.millo.millomod.mod.features.FeatureHandler;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public class MPlayerInventory {

    @Inject(method = "scrollInHotbar", at = @At("HEAD"), cancellable = true)
    public void scrollInHotBar(double scrollAmount, CallbackInfo ci) {
        if (FeatureHandler.scrollInHotBar(scrollAmount)) ci.cancel();
    }

}
