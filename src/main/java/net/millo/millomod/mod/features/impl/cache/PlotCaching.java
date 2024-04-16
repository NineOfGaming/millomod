package net.millo.millomod.mod.features.impl.cache;

import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.millo.millomod.MilloMod;
import net.millo.millomod.config.Config;
import net.millo.millomod.mod.features.Feature;
import net.millo.millomod.mod.features.Keybound;
import net.millo.millomod.mod.features.HandlePacket;
import net.millo.millomod.mod.features.impl.NotificationTray;
import net.millo.millomod.mod.hypercube.template.Template;
import net.millo.millomod.mod.util.gui.GUIStyles;
import net.minecraft.block.Block;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class PlotCaching extends Feature implements Keybound {

    Template cachedTemplate;
    private KeyBinding displayKey;
    private int cacheNextItem = 0;
    CacheGUI cacheGUI;
    private Vec3d clickedLoc;

    @Override
    public String getKey() {
        return "plot_caching";
    }


    @HandlePacket
    public boolean slotUpdate(ScreenHandlerSlotUpdateS2CPacket slot) {
        if (cacheNextItem == 0) return false;
        cacheNextItem = 0;

        NbtCompound nbt = slot.getStack().getNbt();
        if (nbt == null) return false;


        NbtCompound bukkitValues = nbt.getCompound("PublicBukkitValues");
        if (bukkitValues == null || !bukkitValues.contains("hypercube:codetemplatedata", NbtElement.STRING_TYPE))
            return false;

        String codeTemplateData = bukkitValues.getString("hypercube:codetemplatedata");

        cachedTemplate = Template.parseItem(codeTemplateData);
        cachedTemplate.startPos = clickedLoc;

        String templateName = cachedTemplate.blocks.get(0).data;
        if (templateName == null) templateName = cachedTemplate.blocks.get(0).action;

        NotificationTray.pushNotification(
                Text.of("Cached"),
                Text.literal(templateName).setStyle(GUIStyles.NAME.getStyle())
        );

        cacheGUI.loadTemplate(cachedTemplate);
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
    public void onTick() {
        if (cacheNextItem > 0) {
            cacheNextItem--;
            if (cacheNextItem == 0) {
                cacheGUI.loadTemplate(cachedTemplate);
            }
        }
    }

    @Override
    public void triggerKeybind(Config config) {
        while (displayKey.wasPressed()) {
            cacheGUI = new CacheGUI(cachedTemplate);
            cacheGUI.open();

            MinecraftClient mc = MilloMod.MC;
            ClientPlayerEntity player = mc.player;

            if (mc.getNetworkHandler() == null || player == null || mc.world == null) return;

            BlockHitResult rayHit = mc.world.raycast(new RaycastContext(player.getEyePos(),
                    player.getEyePos().add(player.getRotationVector().multiply(5d)),
                    RaycastContext.ShapeType.OUTLINE,
                    RaycastContext.FluidHandling.NONE,
                    ShapeContext.absent()
            ));

            if (rayHit.getType() == HitResult.Type.MISS) return;

            var blockPos = rayHit.getBlockPos();
            Block block = mc.world.getBlockState(blockPos).getBlock();
            if (!"minecraft:diamond_block minecraft:emerald_block minecraft:lapis_block minecraft:gold_block".contains(Registries.BLOCK.getId(block).toString())) {
                blockPos = blockPos.add(1, 0, 0);
            }

            clickedLoc = blockPos.toCenterPos();
            cacheNextItem = 10;

            boolean sneaking = player.isSneaking();

            if (!sneaking) mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
            mc.interactionManager.interactBlock(player, Hand.MAIN_HAND, new BlockHitResult(
                    clickedLoc, Direction.UP, blockPos, false
            ));
            if (!sneaking) mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
        }
    }
}
