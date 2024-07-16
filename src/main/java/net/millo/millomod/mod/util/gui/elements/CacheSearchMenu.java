package net.millo.millomod.mod.util.gui.elements;

import net.millo.millomod.MilloMod;
import net.millo.millomod.mod.features.impl.coding.cache.CacheGUI;
import net.millo.millomod.mod.util.gui.ElementFadeIn;
import net.millo.millomod.mod.util.gui.GUIStyles;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.function.Consumer;

public class CacheSearchMenu implements Drawable, Element, Widget, Selectable {


    ElementFadeIn fade = new ElementFadeIn(ElementFadeIn.Direction.LEFT);
    private int x, y, width, height;
    private int targetX;

    private CacheGUI cacheGUI;

    private TextFieldElement searchTextField;

    // [x,y] in parameters is the top right corner
    public CacheSearchMenu(CacheGUI cacheGUI, int x, int y) {
        this.cacheGUI = cacheGUI;

        this.x = x;
        this.y = y;
        this.width = cacheGUI.width/5;
        this.height = 80;

        targetX = x - width;

        searchTextField = new TextFieldElement(cacheGUI.getTextRenderer(), 0, 0, width, 16, Text.literal("Search"));
        cacheGUI.addDrawableChild(searchTextField);
    }



    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        fade.fadeIn(delta);

        x = (int) MathHelper.clampedLerp(x, targetX, MilloMod.MC.getLastFrameDuration());

        context.getMatrices().push();
        context.getMatrices().translate(fade.getXOffset(), fade.getYOffset(), 0);
        context.getMatrices().translate(x, y, 10);

        context.fill(0, 0, width, height, 0x96000000);
        context.drawBorder(0, 0, width, height, 0xFFFFFFFF);

        context.getMatrices().pop();
    }




    @Override
    public void setFocused(boolean focused) {

    }

    @Override
    public boolean isFocused() {
        return false;
    }

    @Override
    public ScreenRect getNavigationFocus() {
        return new ScreenRect(getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }

    public void appendNarrations(NarrationMessageBuilder builder) {

    }
    public void setX(int x) {
        this.x =x;
    }
    public void setY(int y) {
        this.y = y;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public void forEachChild(Consumer<ClickableWidget> consumer) {}
}
