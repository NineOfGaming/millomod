package net.millo.millomod.mixin.render;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.millo.millomod.config.Config;
import net.millo.millomod.mod.util.StaticSkinRenderer;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Objects;
import java.util.UUID;

@Mixin(HandledScreen.class)
public class MContainerScreen {


    @Inject(method = "drawMouseoverTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;Ljava/util/Optional;II)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void renderTooltip(DrawContext context, int x, int y, CallbackInfo ci, ItemStack stack) {
        boolean enabled = Config.getInstance().get("preview_skin.enabled");
        if (!enabled) return;

        Item item = stack.getItem();
        if (item instanceof BlockItem) {
            Block block = ((BlockItem) item).getBlock();
            if (block instanceof AbstractSkullBlock) {
                previewHeadSkin(context, stack);
            }
        }
    }

    @Unique
    private void previewHeadSkin(DrawContext context, ItemStack stack) {
        MinecraftClient mc = MinecraftClient.getInstance();

        NbtCompound nbt = stack.getNbt();
        if (nbt == null) return;

        GameProfile profile = readGameProfile(nbt.getCompound("SkullOwner"));
        if (profile == null) return;

        var skin = mc.getSkinProvider().getSkinTextures(profile);
        var entity = new StaticSkinRenderer(mc.world, skin);
        var x = Objects.requireNonNull(mc.currentScreen).width / 5;
        var y = mc.currentScreen.height / 2 + 20;

        InventoryScreen.drawEntity(context, x - 35, y - 50, x + 35, y + 50, 40, 0.0625f, x - 20, y - 20, entity);


//        var entity = new Skin
    }

    @Unique
    private GameProfile readGameProfile(NbtCompound skullOwner) {
        UUID uuid = skullOwner.getUuid("Id");
        String name = skullOwner.getString("Name");

        try {
            GameProfile gameProfile = new GameProfile(uuid, name);
            if (skullOwner.contains("Properties", 10)) {
                NbtCompound properties = skullOwner.getCompound("Properties");

                for (String property : properties.getKeys()) {
                    NbtList list = properties.getList(property, 2);

                    for (int i = 0; i < list.size(); ++i) {
                        NbtCompound tag = list.getCompound(i);
                        String value = tag.getString("Value");
                        if (tag.contains("Signature", 8)) {
                            gameProfile.getProperties().put(property, new Property(property, value, tag.getString("Signature")));
                        } else {
                            gameProfile.getProperties().put(property, new Property(property, value));
                        }
                    }
                }
            }

            return gameProfile;
        } catch (Throwable t) {
            return null;
        }
    }

}














