package net.millo.millomod.mixin.player;

import net.millo.millomod.mod.features.FeatureHandler;
import net.millo.millomod.mod.features.impl.global.NoClientClick;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public abstract class MSlot {

    @Shadow public abstract ItemStack getStack();

    @Inject(method = "canTakeItems", at = @At("HEAD"), cancellable = true)
    private void canTakeItems(PlayerEntity playerEntity, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(!shouldCancel(getStack()));
    }

    private boolean shouldCancel(ItemStack stack) {
        return ((NoClientClick) FeatureHandler.getFeature("no_client_click")).shouldCancel(stack);
    }

}
