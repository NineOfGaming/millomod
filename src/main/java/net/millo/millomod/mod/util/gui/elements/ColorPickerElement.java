package net.millo.millomod.mod.util.gui.elements;

import net.millo.millomod.mod.util.gui.ElementFadeIn;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

public class ColorPickerElement implements Drawable, Element, Widget, Selectable {

    int x, y, width, height;

    int wheelX, wheelY, wheelW, wheelH;

    int sliderX, sliderY, sliderW, sliderH;

    static Color selectedColor;
    static float hue, sat, bri;

    boolean hoveringWheel = false, hoveringSlider = false;

    private static ArrayList<Point> wheel;

    ElementFadeIn fade = new ElementFadeIn(ElementFadeIn.Direction.DOWN);

    public void setSelectedColor(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        hue = hsb[0];
        sat = hsb[1];
        bri = hsb[2];
        selectedColor = color;

        updateColor();
    }

    public ElementFadeIn getFade() {
        return fade;
    }



    static class Point {
        public int x;
        private final float hue;
        public Point(int x, float hue) {
            this.x = x;
            this.hue = hue;
        }

        public int get(float saturation, float brightness) {
            return Color.getHSBColor(hue, saturation, brightness).hashCode();
        }
    }

    public ColorPickerElement(int x, int y, int width, int height) {
        if (selectedColor == null) {
            selectedColor = Color.RED;
            hue = 0;
            sat = 1;
            bri = 1f;
        }

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        wheelX = x + 35;
        wheelY = y + 10;
        wheelW = width - 45;
        wheelH = height - 20;

        sliderX = x + 10;
        sliderY = y + 10;
        sliderW = 20;
        sliderH = height - 20;

        if (wheel == null) {
            wheel = new ArrayList<>();

            for (int dx = 0; dx < wheelW; dx++) {
                float hue = (float) dx / wheelW;
                wheel.add(new Point(dx, hue));
            }
        }
    }


    public void updateColor() {
        selectedColor = Color.getHSBColor(hue, sat, bri);

        TFhue.setText(String.valueOf((int)Math.floor(hue * 360)));
        TFsat.setText(String.valueOf((int)Math.floor(sat * 100)));
        TFbri.setText(String.valueOf((int)Math.floor(bri * 100)));

        TFred.setText(String.valueOf(selectedColor.getRed()));
        TFgre.setText(String.valueOf(selectedColor.getGreen()));
        TFblu.setText(String.valueOf(selectedColor.getBlue()));

        String hex = String.format("#%02x%02x%02x", selectedColor.getRed(), selectedColor.getGreen(), selectedColor.getBlue());
        TFhex.setText(hex);

        TFsample.setEditableColor(selectedColor.hashCode());
    }

    boolean editHSB = false, editRGB = false, editHex = false;
    TextFieldElement TFhue, TFsat, TFbri, TFred, TFgre, TFblu, TFhex, TFsample;
    public void setTextFields(TextFieldElement hue, TextFieldElement sat, TextFieldElement bri, TextFieldElement red, TextFieldElement gre, TextFieldElement blu, TextFieldElement hex, TextFieldElement sample) {
        TFhue = hue;
        TFsat = sat;
        TFbri = bri;

        TFred = red;
        TFgre = gre;
        TFblu = blu;

        TFhex = hex;
        TFsample = sample;
    }

    public void tick() {
        editHSB = editHSB || TFhue.isFocused() || TFsat.isFocused() || TFbri.isFocused();
        editRGB = editRGB || TFred.isFocused() || TFgre.isFocused() || TFblu.isFocused();
        editHex = editHex || TFhex.isFocused();
        if (!TFhue.isFocused() && !TFsat.isFocused() && !TFbri.isFocused() && editHSB) {
            editHSB = false;
            updateFromHSB(TFhue.getText(), TFsat.getText(), TFbri.getText());
        }
        if (!TFred.isFocused() && !TFgre.isFocused() && !TFblu.isFocused() && editRGB) {
            editRGB = false;
            updateFromRGB(TFred.getText(), TFgre.getText(), TFblu.getText());
        }
        if (editHex && !TFhex.isFocused()) {
            editHex = false;
            updateFromHex(TFhex.getText());
        }
    }

    private void updateFromHex(String hexStr) {
        try {
            Color hex = Color.decode(hexStr);

            float[] hsb = Color.RGBtoHSB(hex.getRed(), hex.getGreen(), hex.getBlue(), null);

            hue = hsb[0];
            sat = hsb[1];
            bri = hsb[2];
            selectedColor = hex;
            updateColor();
        } catch (Exception ignored) {}
    }

    private void updateFromRGB(String r, String g, String b) {
        try {
            int red = Integer.parseInt(r);
            int gre = Integer.parseInt(g);
            int blu = Integer.parseInt(b);

            if (red < 0 || red > 255) return;
            if (gre < 0 || gre> 255) return;
            if (blu < 0 || blu  > 255) return;

            float[] hsb = Color.RGBtoHSB(red, gre, blu, null);
            if (hsb[0] == hue && hsb[1] == sat && hsb[2] == bri) return;

            hue = hsb[0];
            sat = hsb[1];
            bri = hsb[2];
            selectedColor = Color.getHSBColor(hue, sat, bri);
            updateColor();
        } catch (Exception ignored) {}
    }

    private void updateFromHSB(String h, String s, String b) {
        try {
            int newHue = Integer.parseInt(h);
            int newSat = Integer.parseInt(s);
            int newBri = Integer.parseInt(b);

            if (newHue/360f == hue && newSat/100f == sat && newBri/100f == bri) return;
            if (newHue < 0 || newHue > 360) return;
            if (newSat < 0 || newSat > 100) return;
            if (newBri < 0 || newBri > 100) return;

            hue = newHue / 360f;
            sat = newSat / 100f;
            bri = newBri / 100f;
            selectedColor = Color.getHSBColor(hue, sat, bri);
            updateColor();
        } catch (Exception ignored) {}
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        fade.fadeIn(delta);
        context.getMatrices().push();
        context.getMatrices().translate(
                fade.getXOffset(),
                fade.getYOffset(),
                0
        );

        wheel.forEach(p -> context.fillGradient(
                wheelX + p.x,
                wheelY,
                wheelX + p.x + 1,
                wheelY + wheelH,
                p.get(1, bri),
                p.get(0, bri)
        ));

        hoveringWheel = mouseX > wheelX && mouseX < wheelX + wheelW && mouseY > wheelY && mouseY < wheelY + wheelH;


        int cusorColor = Color.black.hashCode();
        if (bri < 0.5f) cusorColor = Color.white.hashCode();

        int wheelCursorX = (int)(wheelX + wheelW * hue);
        int wheelCursorY = (int)(wheelY + wheelH * (1f - sat));
        context.fill(wheelCursorX-1, wheelCursorY, wheelCursorX+2, wheelCursorY+1, cusorColor);
        context.fill(wheelCursorX, wheelCursorY-1, wheelCursorX+1, wheelCursorY+2, cusorColor);


        hoveringSlider  = mouseX > sliderX && mouseX < sliderX + sliderW && mouseY > sliderY && mouseY < sliderY + sliderH;
        context.fillGradient(sliderX,
                sliderY,
                sliderX + sliderW,
                sliderY + sliderH,
                Color.getHSBColor(hue, sat, 1).hashCode(),
                Color.getHSBColor(hue, sat, 0).hashCode()
                );
        context.drawBorder(sliderX, sliderY, sliderW, sliderH, Color.BLACK.hashCode());
        int yyy = (int) (sliderY + ((1f - bri) * sliderH));
        context.fill(sliderX-2, yyy-1, sliderX + sliderW + 2, yyy, Color.WHITE.hashCode());


        int xx = x + width - 30;
        int yy = y - 20;
        context.fill(xx, yy, xx+20, yy+20, selectedColor.hashCode());
        context.drawBorder(xx, yy, 20, 20, Color.black.hashCode());

        context.getMatrices().pop();
    }

    public static Color getSelectedColor() {
        return selectedColor;
    }

    String focus = "none";
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (hoveringWheel) {
            focus = "wheel";
            hue = (float) ((int) mouseX - wheelX) / wheelW;
            sat = 1f - (float) ((int) mouseY - wheelY) / wheelH;
            updateColor();
            return true;
        }
        if (hoveringSlider) {
            focus = "slider";
            bri = 1f - (float) ((mouseY - sliderY) / sliderH);
            updateColor();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (Objects.equals(focus, "wheel")) {
            int wheelMouseX = MathHelper.clamp((int) mouseX, wheelX, wheelX + wheelW);
            int wheelMouseY = MathHelper.clamp((int) mouseY, wheelY, wheelY + wheelH);
            hue = (float) (wheelMouseX - wheelX) / wheelW;
            sat = 1f - (float) (wheelMouseY - wheelY) / wheelH;
            updateColor();
        }
        if (Objects.equals(focus, "slider")) {
            bri = 1f - (float) ((mouseY - sliderY) / sliderH);
            bri = Math.max(Math.min(bri, 1f), 0f);
            updateColor();
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        updateColor();
        focus = "none";
        return false;
    }

    @Override
    public void setFocused(boolean focused) {}

    @Override
    public boolean isFocused() {
        return false;
    }

    @Override
    public ScreenRect getNavigationFocus() {
        return new ScreenRect(this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }




    public void appendNarrations(NarrationMessageBuilder builder) {}
    public void setX(int x) {
        this.x = x;
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
