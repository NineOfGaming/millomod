package net.millo.millomod.mod.features.gui;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.awt.*;

@Environment(EnvType.CLIENT)
public abstract class GUI extends Screen {

    protected int backgroundWidth, backgroundHeight;
    protected int padding = 40;
    private final ElementFadeIn fade = new ElementFadeIn(ElementFadeIn.Direction.UP);

    public GUI(Text title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();
        backgroundWidth = width - padding*2;
        backgroundHeight = height - padding*2;
    }

    public void open() {
        MinecraftClient.getInstance().send(() -> MinecraftClient.getInstance().setScreen(this));
    }

    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {

        int x = padding;
        int y = padding+(int)((1f - fade.getProgress()) * 10f);
        int color = new Color(0, 0, 0, (int)(fade.getProgress() * 150)).hashCode();

        context.getMatrices().push();
        context.getMatrices().translate(0f, 0f, -20f);
        context.fill(x, y, x+backgroundWidth, y+backgroundHeight, 0, color);
        context.getMatrices().pop();
    }
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        fade.fadeIn(delta);

        if (fade.getProgress() >= 1f) {
            super.render(context, mouseX, mouseY, delta);
        } else {
            renderBackground(context, mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public ElementFadeIn getFade() {
        return fade;
    }
}
