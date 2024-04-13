package net.millo.millomod.mixin;

import net.millo.millomod.config.Config;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(ChatScreen.class)
public class MChatScreen extends Screen {

    protected MChatScreen(Text title) {
        super(title);
    }

    @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true)
    private static String openChat(String originalChatText) {
        boolean enabled = Config.getInstance().get("auto_command.enabled");
        if (!enabled || !originalChatText.isEmpty()) return originalChatText;
        return "@";
    }

    @Inject(method = "render", at = @At("RETURN"))
    public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        boolean enabled = Config.getInstance().get("auto_command.enabled");
        if (!enabled) return;

        context.fill(0, height - 14, 2, height - 2, Color.PINK.hashCode());
    }
}
