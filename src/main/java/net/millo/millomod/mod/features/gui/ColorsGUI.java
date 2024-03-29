package net.millo.millomod.mod.features.gui;

import net.millo.millomod.mod.util.gui.ElementFadeIn;
import net.millo.millomod.mod.util.gui.GUI;
import net.millo.millomod.mod.util.gui.GUIStyles;
import net.millo.millomod.mod.util.gui.elements.ButtonElement;
import net.millo.millomod.mod.util.gui.elements.ColorPickerElement;
import net.millo.millomod.mod.util.gui.elements.TextElement;
import net.millo.millomod.mod.util.gui.elements.TextFieldElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

public class ColorsGUI extends GUI {

    // TODO: add a close button

    private ColorPickerElement colorPicker;
    private static ArrayList<Color> recentColors = new ArrayList<>();
    private ArrayList<Element> recentColorElements;
    public ColorsGUI() {
        super(Text.literal("Colors"));
    }

    @Override
    protected void init() {
        super.init();
        backgroundWidth = 256;
        backgroundHeight = 256;

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        colorPicker = new ColorPickerElement(x, y + backgroundHeight/2, backgroundWidth, backgroundHeight/2);

        recentColorElements = new ArrayList<>();

        addDrawable(new TextElement(x, y+5, 25, 20, Text.literal("H").setStyle(GUIStyles.COMMENT.getStyle()), textRenderer));
        addDrawable(new TextElement(x, y+25, 25, 20, Text.literal("S").setStyle(GUIStyles.COMMENT.getStyle()), textRenderer));
        addDrawable(new TextElement(x, y+45, 25, 20, Text.literal("B").setStyle(GUIStyles.COMMENT.getStyle()), textRenderer));
        addDrawable(new TextElement(x, y+70, 25, 20, Text.literal("R").setStyle(GUIStyles.COMMENT.getStyle()), textRenderer));
        addDrawable(new TextElement(x, y+90, 25, 20, Text.literal("G").setStyle(GUIStyles.COMMENT.getStyle()), textRenderer));
        addDrawable(new TextElement(x, y+110, 25, 20, Text.literal("B").setStyle(GUIStyles.COMMENT.getStyle()), textRenderer));

        TextFieldElement hue = new TextFieldElement(textRenderer, x+25, y+5, 30, 16, Text.literal("0"));
        TextFieldElement sat = new TextFieldElement(textRenderer, x+25, y+25, 30, 16, Text.literal("255"));
        TextFieldElement bri = new TextFieldElement(textRenderer, x+25, y+45, 30, 16, Text.literal("255"));
        TextFieldElement r = new TextFieldElement(textRenderer, x+25, y+70, 30, 16, Text.literal("0"));
        TextFieldElement g = new TextFieldElement(textRenderer, x+25, y+90, 30, 16, Text.literal("255"));
        TextFieldElement b = new TextFieldElement(textRenderer, x+25, y+110, 30, 16, Text.literal("255"));
        TextFieldElement hex = new TextFieldElement(textRenderer, x+60, y+90, 50, 16, Text.literal("255"));
        TextFieldElement sample = new TextFieldElement(textRenderer, x+60, y+5, backgroundWidth - 70, 16, Text.literal(""));
        sample.setText("Sample Text");

        colorPicker.setTextFields(hue, sat, bri, r, g, b, hex, sample);
        colorPicker.updateColor();

        addDrawableChildren(getCopyButton(x, y));

        addDrawableChildren(hue, sat, bri, r, g, b, hex, sample);
        addDrawableChild(colorPicker);


        updateRecentColors();
    }

    @NotNull
    private ButtonElement getCopyButton(int x, int y) {
        ButtonElement copyButton = new ButtonElement(x+60, y + 110, 50, 16, Text.literal("Copy"), (button) -> {
            Color color = ColorPickerElement.getSelectedColor();
            String hexString = String.format("<#%02x%02x%02x>", color.getRed(), color.getGreen(), color.getBlue());
            if (hasShiftDown()) hexString = "&" + String.join("&", String.format("x%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue()).split(""));
            if (hasControlDown()) hexString = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
            MinecraftClient.getInstance().keyboard.setClipboard(hexString);

            recentColors.remove(color);
            recentColors.add(0, color);
            recentColors = new ArrayList<>(recentColors.subList(0, Math.min(recentColors.size(), 25)));
            updateRecentColors();

        }, textRenderer);
        copyButton.setTooltip(Text.literal("Click to copy, Shift for old variant, Ctrl for hex"));
        return copyButton;
    }

    private void updateRecentColors() {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        recentColorElements.forEach(this::remove);
        recentColorElements = new ArrayList<>();
        Iterator<Color> iter = recentColors.iterator();
        for (int i = 0; iter.hasNext(); ++i) {
            ColorTemplateElement element = new ColorTemplateElement(iter.next(),
                    this, colorPicker,
                    (i % 5) * 20 + x + 120,
                    (int) ((double) (i / 5) * 20 + y + 26),
                    15,
                    15);
            ElementFadeIn fade = new ElementFadeIn(ElementFadeIn.Direction.DOWN);
            fade.setProgress(colorPicker.getFade().getProgress());
            element.setFade(fade);
            addDrawableChild(element);
            recentColorElements.add(element);
        }

    }

    protected void removeRecentColor(Color color) {
        recentColors.remove(color);
        updateRecentColors();
    }

    @SafeVarargs
    private <T extends Element & Drawable & Selectable> void addDrawableChildren(T ...drawableElement) {
        for (T t : drawableElement) {
            addDrawableChild(t);
        }
    }

    @Override
    public void tick() {
        if (colorPicker != null) {
            colorPicker.tick();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Iterator<? extends Element> iter = this.children().iterator();

        Element element;
        if (!iter.hasNext()) {
            this.setFocused(false);
            return false;
        }

        element = iter.next();
        while (!element.mouseClicked(mouseX, mouseY, button)) {
            if (!iter.hasNext()) {
                if (this.getFocused() != null) this.getFocused().setFocused(false);
                this.setFocused(false);
                return false;
            }

            element = iter.next();
        }

        this.setFocused(element);
        if (button == 0) {
            this.setDragging(true);
        }

        return true;
    }

    public static class ColorTemplateElement extends ButtonElement {
        ElementFadeIn fade = new ElementFadeIn(ElementFadeIn.Direction.UP);
        private final Color color;
        private final ColorPickerElement colorPicker;
        private final ColorsGUI colorsGUI;
        public ColorTemplateElement(Color color, ColorsGUI colorsGUI, ColorPickerElement colorPicker, int x, int y, int width, int height) {
            super(x, y, width, height, (button) -> {});
            this.colorPicker = colorPicker;
            this.color = color;
            this.colorsGUI = colorsGUI;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!isHovered()) return false;
            if (button == 1) {
                colorsGUI.removeRecentColor(color);
                return true;
            }
            colorPicker.setSelectedColor(color);
            return true;
        }

        @Override
        public void setFade(ElementFadeIn fade) {
            this.fade = fade;
        }

        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            fade.fadeIn(delta);

            int x = getX() + fade.getXOffset();
            int y = getY() + fade.getYOffset();

            int color = this.color.hashCode();
            int borderColor = new Color(51, 51, 51).hashCode();

            context.fill(x, y, x+width, y+height, 0, color);
            if (this.isHovered()) context.drawBorder(x, y, width, height, borderColor);
        }
    }
}
