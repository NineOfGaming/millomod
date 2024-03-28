package net.millo.millomod.mod.util.gui.elements;

import net.millo.millomod.config.Config;
import net.millo.millomod.mod.features.IRenderable;
import net.millo.millomod.mod.util.gui.ElementFadeIn;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
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
    public MoveableElement(IRenderable renderable, String name, TextRenderer textRenderer) {
        super(renderable.getX(), renderable.getY(), renderable.getWidth(), renderable.getHeight(), Text.literal(name));
        this.renderable = renderable;
        textWidget = new TextWidget(getX(), getY(), renderable.getWidth(), renderable.getHeight(), Text.literal(name), textRenderer);
        textWidget.alignCenter();
    }


    double onMouseX, onMouesY;
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible) return false;
        if (hovered && button == 0) {
            onMouseX = getX() - mouseX;
            onMouesY = getY() - mouseY;
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
            setX((int) (mouseX + onMouseX));
            setY((int) (mouseY + onMouesY));

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

            context.getMatrices().push();
            context.drawBorder(x, y, width, height, color);
            context.getMatrices().pop();

            textWidget.setX(x);
            textWidget.setY(y);
            textWidget.render(context, mouseX, mouseY, delta);

        }
    }
}
