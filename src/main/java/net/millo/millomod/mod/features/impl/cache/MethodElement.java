package net.millo.millomod.mod.features.impl.cache;

import net.millo.millomod.SoundHandler;
import net.millo.millomod.mod.util.gui.GUIStyles;
import net.millo.millomod.mod.util.gui.elements.ButtonElement;
import net.millo.millomod.mod.util.gui.elements.ContextElement;
import net.millo.millomod.system.FileManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.Arrays;

public class MethodElement extends ButtonElement {

    private enum Method {
        EVENT(0xFF52EACA),
        ENTITY_EVENT(0xFFEAD536),
        PROCESS(0xFF48EA2B),
        FUNC(0xFF028AEA),
        UNKNOWN(0xFFFFFFFF);


        private final int color;
        Method(int color) {
            this.color = color;
        }

        public int getColor() {
            return color;
        }
    }

    private final String name, filename;
    private final Method method;
    private final int plotId;
    public MethodElement(int height, int plotId, String filename, PressAction onPress, TextRenderer textRenderer) {
        super(0, 0, 10, height,
                Text.of(filename.replaceAll("\\.(event|func|process|entity_event)$", "")),
                onPress, textRenderer);
        name = filename.replaceAll("\\.(event|func|process|entity_event)$", "");
        this.filename = filename;
        this.plotId = plotId;

        textWidget.alignLeft();

        if (filename.equals(name)) {
            method = Method.UNKNOWN;
            return;
        }
        String filetype = filename.substring(name.length() + 1).toUpperCase();
        method = Arrays.stream(Method.values()).filter(i -> i.name().equals(filetype)).findFirst().orElse(Method.UNKNOWN);
    }


    @Override
    public void onPress(double mouseX, double mouseY, int button) {
        if (button == 0) super.onPress(mouseX, mouseY, button);
        if (button == 1) {
            CacheGUI.lastOpenedGUI.openContext(mouseX, mouseY,
                    new ContextElement(100, textRenderer)
                            .add(Text.literal("Delete").setStyle(GUIStyles.SCARY.getStyle()), (b) -> {
                                SoundHandler.playClick();
                                FileManager.deleteTemplateFile(plotId, filename);
                                CacheGUI.lastOpenedGUI.reload();
                                CacheGUI.lastOpenedGUI.closeContext();
                            })
                            .add(Text.literal("Refactor"), (b) -> {
                                SoundHandler.playClick();
                            })
            );
        }
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        fade.fadeIn(delta);

        int x = getX() + fade.getXOffset();
        int y = getY() + fade.getYOffset();

        int color = new Color(0, 0, 0, (int)(fade.getProgress() * 150)).hashCode();
        if (isHovered()) color = new Color(12, 11, 9, (int)(fade.getProgress() * 150)).hashCode();

        context.fill(x, y, x+width, y+height, 0, color);
        context.fill(x, y, x+2, y+height, 0, method.getColor());

        if (textWidget == null) return;
        textWidget.setX(x+10);
        textWidget.setY(y);
        textWidget.render(context, mouseX, mouseY, delta);
    }

}

