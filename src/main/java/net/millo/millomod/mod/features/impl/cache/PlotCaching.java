package net.millo.millomod.mod.features.impl.cache;

import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.millo.millomod.MilloMod;
import net.millo.millomod.mod.Callback;
import net.millo.millomod.mod.features.impl.Tracker;
import net.millo.millomod.mod.features.impl.teleport.TeleportHandler;
import net.millo.millomod.mod.util.GlobalUtil;
import net.millo.millomod.system.Config;
import net.millo.millomod.mod.features.Feature;
import net.millo.millomod.mod.features.Keybound;
import net.millo.millomod.mod.features.HandlePacket;
import net.millo.millomod.mod.features.impl.NotificationTray;
import net.millo.millomod.mod.hypercube.template.Template;
import net.millo.millomod.mod.util.gui.GUIStyles;
import net.millo.millomod.system.FileManager;
import net.millo.millomod.system.Utility;
import net.minecraft.block.Block;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.SignText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.regex.Pattern;

public class PlotCaching extends Feature implements Keybound {

    Template cachedTemplate;
    private KeyBinding displayKey;
    private int cacheNextItem = 0;
    @Nullable CacheGUI cacheGUI;
    private Vec3d clickedLoc;

    @Override
    public String getKey() {
        return "plot_caching";
    }


    @HandlePacket
    public boolean onMessage(GameMessageS2CPacket message) { // TODO: fix this
        String content = message.content().getString();
        if (cacheNextItem > 0) {
             if (content.equals("Error: Unable to create code template! Exceeded the code data size limit.")) {
                cacheNextItem = 0;
                if (doingFullScan) {
                    scanPlotStep = ScanPlotStep.TELEPORT;
                    return true;
                }
            }
            return content.equals("Note: You can view your past 5 created templates with /templatehistory!");
        }
        return false;
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

        FileManager.writeTemplate(cachedTemplate);

        NotificationTray.pushNotification(
                Text.of("Cached"),
                Text.literal(cachedTemplate.getName()).setStyle(GUIStyles.NAME.getStyle())
        );

        if (MilloMod.MC.getNetworkHandler() != null)
            MilloMod.MC.getNetworkHandler().sendPacket(new CreativeInventoryActionC2SPacket(slot.getSlot(), ItemStack.EMPTY));
        if (cacheGUI != null) cacheGUI.loadTemplate(cachedTemplate);

        if (doingFullScan) {
            scanPlotStep = ScanPlotStep.TELEPORT;
        }

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


    int cacheTries = 0;
    @Override
    public void onTick() {
        if (cacheNextItem > 0) {
            cacheNextItem--;
            if (cacheNextItem == 0) {
                if (doingFullScan) {
                    cacheTries ++;
                    if (cacheTries > 3) {
                        scanPlotStep = ScanPlotStep.TELEPORT;
                        NotificationTray.pushNotification(Text.of("Failed to cache"),
                                Text.literal(clickedLoc.toString()).setStyle(GUIStyles.SCARY.getStyle()));
                    } else cacheMethodFromPosition(BlockPos.ofFloored(clickedLoc));
                } else if (cacheGUI != null) cacheGUI.loadTemplate(cachedTemplate);
            }
        }

        if (doingFullScan) {
            scanTick();
        }
    }


    @Override
    public void triggerKeybind(Config config) {
        if (MilloMod.MC.currentScreen == null && !doingFullScan && GlobalUtil.isKeyPressed(displayKey)) {
            cacheGUI = new CacheGUI();
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
            if (!Pattern.compile("minecraft:(diamond|emerald|lapis|gold)_block").matcher(Registries.BLOCK.getId(block).toString()).matches()){
                blockPos = blockPos.add(1, 0, 0);
            }

            cacheMethodFromPosition(blockPos);
        }
    }

    public void cacheMethodFromPosition(BlockPos position) {
        var interact = MilloMod.MC.interactionManager;
        var net = MilloMod.MC.getNetworkHandler();
        var player = MilloMod.MC.player;

        if (net == null || interact == null || player == null) return;

        clickedLoc = position.toCenterPos();
        cacheNextItem = 40;

        boolean sneaking = player.isSneaking();

        ItemStack item = player.getMainHandStack();

//        player.getInventory().setStack(player.getInventory().selectedSlot, ItemStack.EMPTY);
//        net.sendPacket(new CreativeInventoryActionC2SPacket(player.getInventory().selectedSlot, ItemStack.EMPTY));
//        if (!sneaking) net.sendPacket(new ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
//        interact.interactBlock(player, Hand.MAIN_HAND, new BlockHitResult(
//                clickedLoc, Direction.UP, position, false
//        ));
//        if (!sneaking) net.sendPacket(new ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
//        net.sendPacket(new CreativeInventoryActionC2SPacket(player.getInventory().selectedSlot, item));
//        player.getInventory().setStack(player.getInventory().selectedSlot, item);

        player.getInventory().setStack(player.getInventory().selectedSlot, ItemStack.EMPTY);
        Utility.sendHandItem(ItemStack.EMPTY);
        if (!sneaking) Utility.sendSneak(true);
        Utility.rightClickPos(position);
        if (!sneaking) Utility.sendSneak(false);
        Utility.sendHandItem(item);
        player.getInventory().setStack(player.getInventory().selectedSlot, item);
    }




    private Stack<BlockPos> scanStack;
    private BlockPos scanStepTarget;
    private boolean doingFullScan = false;
    private ScanPlotStep scanPlotStep = ScanPlotStep.NONE;
    private int scanPlotTicksTried = 0;

    public boolean scanPlot() {
        if (doingFullScan) {
            doingFullScan = false;
            return false;
        }
        doingFullScan = true;

        ArrayList<BlockPos> signs = Tracker.getPlot().scanForMethods();

        scanStack = new Stack<>();
        scanStack.addAll(signs);

        scanPlotStep = ScanPlotStep.TELEPORT;
        return true;
    }


    private void scanTick() {
        if (!doingFullScan || MilloMod.MC.player == null) return;

        if (scanStack.isEmpty() && scanPlotStep == ScanPlotStep.TELEPORT) {
            doingFullScan = false;
            MilloMod.MC.player.sendMessage(Text.of("Finished full scan"));
            return;
        }
        scanPlotTicksTried ++;
        if (scanPlotTicksTried > 10) {
            TeleportHandler.abort();
            scanPlotStep = ScanPlotStep.CACHE;
            if (MilloMod.MC.player.getPos().distanceTo(scanStepTarget.toCenterPos()) > 4) {
                scanStack.add(scanStepTarget);
                scanPlotStep = ScanPlotStep.TELEPORT;
            }
        }

        switch (scanPlotStep) {
            default -> {}
            case TELEPORT -> {
                scanPlotTicksTried = 0;
                scanStepTarget = scanStack.pop();
                scanPlotStep = ScanPlotStep.WAIT_FOR_TP;
                TeleportHandler.teleportTo(scanStepTarget.toCenterPos(), () -> {
                    scanPlotStep = ScanPlotStep.CACHE;
                });
            }
            case CACHE -> {
                scanPlotTicksTried = 0;
                scanPlotStep = ScanPlotStep.WAIT_FOR_CACHE;
                cacheTries = 0;
                cacheMethodFromPosition(scanStepTarget);
            }
        }

    }

    private enum ScanPlotStep { NONE, TELEPORT, WAIT_FOR_TP, CACHE, WAIT_FOR_CACHE }


}
