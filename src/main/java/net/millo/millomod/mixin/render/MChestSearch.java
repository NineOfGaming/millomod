package net.millo.millomod.mixin.render;

import net.millo.millomod.config.Config;
import net.millo.millomod.mixin.render.ChestSearchAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

@Mixin(HandledScreen.class)
public abstract class MChestSearch extends Screen {

    @Shadow @Final protected Text playerInventoryTitle;
    @Shadow protected int x;
    @Shadow protected int y;
    @Shadow protected int backgroundWidth;

    protected MChestSearch(Text title) {
        super(title);
    }

    @Unique
    private final int SEARCH_BOX_WIDTH = 96;
    @Unique
    private boolean isChestScreen;
    @Unique
    private boolean isEmptyString, enabled;
    @Unique
    private static TextFieldWidget itemSearchBox;

    @Inject(at = @At("RETURN"), method = "init()V")
    private void addSearchBox(CallbackInfo info) {
        enabled = Config.getInstance().get("menu_search.enabled");
        if (!enabled) return;

        isChestScreen = ((ChestSearchAccessor) this).getHandler() instanceof GenericContainerScreenHandler handler && handler.getInventory() instanceof SimpleInventory;
        if (!isChestScreen) return;
//        isChestScreen = title.getString().contains("Functions") || title.getString().contains("Processes");

        itemSearchBox = new TextFieldWidget(MinecraftClient.getInstance().textRenderer,
                this.x + backgroundWidth - SEARCH_BOX_WIDTH - 8,
                this.y - 16,
                SEARCH_BOX_WIDTH,
                16,
                itemSearchBox,
                Text.literal(""));
        itemSearchBox.setPlaceholder(Text.literal("Search.."));
        addSelectableChild(itemSearchBox);
        itemSearchBox.setChangedListener(str -> isEmptyString = str.trim().isEmpty());
        isEmptyString = itemSearchBox.getText().trim().isEmpty();

//        resetButton = new ButtonWidget(SEARCH_BOX_WIDTH + 8, this.height - 24, 36, 20,
//                Text.literal("Reset"), button -> itemSearchBox.setText(""), );
//        this.addSelectableChild(resetButton);

    }

    @Inject(at = @At("TAIL"), method = "drawSlot(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/screen/slot/Slot;)V")
    private void renderMatchingResults(DrawContext context, Slot slot, CallbackInfo info) {
        if (!enabled) return;
        if (!isChestScreen || isEmptyString) return;

        int color;
        String itemName = slot.getStack().getName().getString().toLowerCase();
        String[] searchText = itemSearchBox.getText().trim().toLowerCase().split(" ");
        if (Arrays.stream(searchText).allMatch(itemName::contains)) {
            color = 1090519039;
        } else {
            color = -2147483648;
        }

        context.getMatrices().push();
        context.getMatrices().translate(0f, 0f, 100f);
        context.fillGradient(slot.x, slot.y, slot.x + 16, slot.y + 16, -20, color, color);
        context.getMatrices().pop();
    }

    @Inject(at = @At("HEAD"), method = "keyPressed(III)Z", cancellable = true)
    private void checkKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> info) {
        if (enabled) {
            if (keyCode == 257) {
                itemSearchBox.setFocused(false);
            }

            if (modifiers == 2 && keyCode == 70) {
                itemSearchBox.setEditable(true);
                itemSearchBox.setSelectionStart(0);
                itemSearchBox.setSelectionEnd(itemSearchBox.getText().length());
                this.setFocused(itemSearchBox);
                info.cancel();
                return;
            }
        }

        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            info.setReturnValue(true);
        } else if (isChestScreen && itemSearchBox.isActive()) {
            itemSearchBox.keyPressed(keyCode, scanCode, modifiers);
            info.setReturnValue(true);
        }
    }

    @Inject(at = @At("RETURN"), method = "close")
    private void onCloseReset(CallbackInfo ci) {
        if (itemSearchBox == null) return;
        itemSearchBox.setText("");
    }

    @Inject(at = @At("RETURN"), method = "render(Lnet/minecraft/client/gui/DrawContext;IIF)V")
    private void renderSearchBox(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo info) {
        if (!enabled) return;
        if (!isChestScreen) return;
        itemSearchBox.render(context, mouseX, mouseY, delta);

    }

}
