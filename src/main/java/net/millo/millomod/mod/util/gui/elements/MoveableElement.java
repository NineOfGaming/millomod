package net.millo.millomod.mod.util.gui.elements;

import net.millo.millomod.mod.features.gui.PositionsGUI;
import net.millo.millomod.system.Config;
import net.millo.millomod.mod.features.IRenderable;
import net.millo.millomod.mod.util.gui.ElementFadeIn;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;

import java.awt.*;

public class MoveableElement extends ClickableWidget implements Drawable, Element {
    private boolean dragged = false;
    private final ElementFadeIn fade = new ElementFadeIn(ElementFadeIn.Direction.UP);
    TextWidget textWidget;

    IRenderable renderable;

    private final PositionsGUI gui;
    public MoveableElement(IRenderable renderable, String name, TextRenderer textRenderer, PositionsGUI gui) {
        super(renderable.getX(), renderable.getY(), renderable.getWidth(), renderable.getHeight(), Text.literal(name));
        this.renderable = renderable;
        this.gui = gui;
        textWidget = new TextWidget(getX(), getY(), renderable.getWidth(), renderable.getHeight(), Text.literal(name), textRenderer);
        textWidget.alignCenter();
    }


    double onMouseX, onMouseY;
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible) return false;
        if (hovered && button == 0) {
            onMouseX = getX() - mouseX;
            onMouseY = getY() - mouseY;
            dragged = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (dragged) {
                Config config = Config.getInstance();
                String key = "hud."+renderable.getKey();
                config.set(key+".x", getX());
                config.set(key+".y", getY());

                renderable.setX(getX());
                renderable.setY(getY());
            }
            dragged = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }


    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.visible && this.isFocused() && dragged) {

            int x = (int) (mouseX + onMouseX);
            int y = (int) (mouseY + onMouseY);

            if (!Screen.hasShiftDown()) {
                int snapDist = 5;
                // Snapping
                for (MoveableElement element : gui.getMoveables()) {
                    if (element == this) continue;

                    int elementX = element.getX();
                    int elementY = element.getY();
                    int elementWidth = element.getWidth();
                    int elementHeight = element.getHeight();

                    // Snap horizontally
                    if (Math.abs(x - elementX) <= snapDist) x = elementX;
                    else if (Math.abs(x + getWidth() - (elementX + elementWidth)) <= snapDist)
                        x = elementX + elementWidth - getWidth();

                    // Snap vertically
                    if (Math.abs(y - elementY) <= snapDist) y = elementY;
                    else if (Math.abs(y + getHeight() - (elementY + elementHeight)) <= snapDist)
                        y = elementY + elementHeight - getHeight();
                }

                // Snap to screen edges
                if (Math.abs(x) <= snapDist) x = 0;
                if (Math.abs(y) <= snapDist) y = 0;
                if (Math.abs(x + getWidth() - gui.width) <= snapDist) x = gui.width - getWidth();
                if (Math.abs(y + getHeight() - gui.height) <= snapDist) y = gui.height - getHeight();
            }

            setX(x);
            setY(y);
            renderable.setX(getX());
            renderable.setY(getY());
            return true;
        }
        return false;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.visible) {
            this.hovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;

            fade.fadeIn(delta);

            int x = getX();
            int y = getY();
            int color = new Color(0, 255, 255, (int)(fade.getProgress() * 150)).hashCode();
            if (isHovered()) color = new Color(255, 200, 9, (int)(fade.getProgress() * 200)).hashCode();

            if (dragged) color = Color.orange.hashCode();

            context.getMatrices().pushMatrix();
            context.drawBorder(x, y, width, height, color);
            context.getMatrices().popMatrix();

            textWidget.setX(x);
            textWidget.setY(y);
            textWidget.render(context, mouseX, mouseY, delta);

        }
    }
}
