package net.millo.millomod.mod.features.gui.elements;

import net.millo.millomod.mod.features.gui.ClickableElementI;
import net.millo.millomod.mod.features.gui.ElementFadeIn;
import net.millo.millomod.mod.features.gui.ScrollableEntryI;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.ArrayList;

public class ScrollableElement extends ClickableWidget implements Drawable, Element {

    private static final Identifier SCROLLER_TEXTURE = new Identifier("widget/scroller");
    private static final int SCROLLER_WIDTH = 4;
    private double scrollY;
    private boolean scrollbarDragged;

    private final ArrayList<ScrollableEntryI> drawables = new ArrayList<>();



    // Message is for narrator
    public ScrollableElement(int x, int y, int width, int height, Text message) {
        super(x, y, width, height, message);
    }

    public <T extends Element & ScrollableEntryI & Selectable> void addDrawableChild(T drawableElement) {
        this.drawables.add(drawableElement);
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.visible) {
            return false;
        }
        boolean withinBounds = this.isWithinBounds(mouseX, mouseY);
        boolean onScrollbar = this.overflows() && mouseX >= (double)(this.getX() + this.width - SCROLLER_WIDTH) && mouseX <= (double)(this.getX() + this.width) && mouseY >= (double)this.getY() && mouseY < (double)(this.getY() + this.height);

        if (withinBounds) {
            for (ScrollableEntryI drawable : drawables) {
                if (!(drawable instanceof ClickableElementI)) continue;
                if (!((ClickableElementI) drawable).isHovered()) continue;
                ((ClickableElementI) drawable).onPress();
            }
        }

        if (onScrollbar && button == 0) {
            this.scrollbarDragged = true;
            return true;
        }
        return withinBounds || onScrollbar;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            this.scrollbarDragged = false;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.visible && this.isFocused() && this.scrollbarDragged) {
            if (mouseY < (double)this.getY()) {
                this.setScrollY(0.0);
            } else if (mouseY > (double)(this.getY() + this.height)) {
                this.setScrollY(this.getMaxScrollY());
            } else {
                int i = this.getScrollbarThumbHeight();
                double d = Math.max(1, this.getMaxScrollY() / (this.height - i));
                this.setScrollY(this.scrollY + deltaY * d);
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!this.visible) {
            return false;
        } else {
            this.setScrollY(this.scrollY - verticalAmount * this.getDeltaYPerScroll());
            return true;
        }
    }

    private double getDeltaYPerScroll() {
        return 18;
    }


    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.visible) {

//            this.drawBox(context);
            context.enableScissor(this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1);
            context.getMatrices().push();
            context.getMatrices().translate(0, -this.scrollY, 0.0);
            this.renderContents(context, mouseX, mouseY, delta);
            context.getMatrices().pop();
            context.disableScissor();
            this.renderOverlay(context);
        }
    }

    private void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {

        int i = this.getY() + this.getPadding();
        int j = this.getX() + this.getPadding();

        int dx = j;
        int dy = i + (int) (-this.scrollY);

        context.getMatrices().push();
        context.getMatrices().translate(j, i, 0.0);
        for (ScrollableEntryI drawable : drawables) {
            drawable.setRealX(drawable.getX() + dx);
            drawable.setRealY(drawable.getY() + dy);
            drawable.render(context, mouseX, mouseY, delta);

            context.getMatrices().translate(0, drawable.getHeight(), 0.0);
            dy += drawable.getHeight();
        }
        context.getMatrices().pop();
    }

    private int getPadding() {
        return 4;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }
    protected void renderOverlay(DrawContext context) {
        if (this.overflows()) {
            this.drawScrollbar(context);
        }
    }
    private void drawScrollbar(DrawContext context) {
        int i = this.getScrollbarThumbHeight();
        int j = this.getX() + this.width;
        int k = Math.max(this.getY(), (int)this.scrollY * (this.height - i) / this.getMaxScrollY() + this.getY());
//        context.drawGuiTexture(SCROLLER_TEXTURE, j, k, 8, i);

        int color = new Color(81, 91, 101, 150).hashCode();
        context.fill(j-SCROLLER_WIDTH, k, j, k+i, 0, color);


    }


    protected boolean isVisible(int top, int bottom) {
        return (double)bottom - this.scrollY >= (double)this.getY() && (double)top - this.scrollY <= (double)(this.getY() + this.height);
    }

    protected boolean isWithinBounds(double mouseX, double mouseY) {
        return mouseX >= (double)this.getX() && mouseX < (double)(this.getX() + this.width) && mouseY >= (double)this.getY() && mouseY < (double)(this.getY() + this.height);
    }
    protected boolean overflows() {
        return this.getContentsHeight() > this.getHeight();
    }
    protected double getScrollY() {
        return this.scrollY;
    }

    protected void setScrollY(double scrollY) {
        this.scrollY = MathHelper.clamp(scrollY, 0.0, this.getMaxScrollY());
    }

    protected int getMaxScrollY() {
        return Math.max(0, this.getContentsHeightWithPadding() - (this.height - 4));
    }
    private int getScrollbarThumbHeight() {
        return MathHelper.clamp((int)((float)(this.height * this.height) / (float)this.getContentsHeightWithPadding()), 32, this.height);
    }
    private int getContentsHeightWithPadding() {
        return this.getContentsHeight() + getPadding();
    }

    private int getContentsHeight() {
        int height = 0;
        for (ScrollableEntryI i : drawables) {
            height += i.getHeight();
        }
        return height;
    }

}
