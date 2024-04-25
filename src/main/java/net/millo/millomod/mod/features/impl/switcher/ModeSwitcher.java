package net.millo.millomod.mod.features.impl.switcher;

import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.millo.millomod.MilloMod;
import net.millo.millomod.mod.Callback;
import net.millo.millomod.mod.features.Feature;
import net.millo.millomod.mod.features.IRenderable;
import net.millo.millomod.mod.features.Keybound;
import net.millo.millomod.mod.util.RenderInfo;
import net.millo.millomod.system.Config;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.ArrayList;

public class ModeSwitcher extends Feature implements Keybound, IRenderable {

    int x, y, width, height;
    KeyBinding openKey;

    ArrayList<Option> options = new ArrayList<>();
    public ModeSwitcher() {
        options.add(new Option(Text.of("Dev"), () -> MilloMod.MC.getNetworkHandler().sendCommand("dev")));
        options.add(new Option(Text.of("Play"), () -> MilloMod.MC.getNetworkHandler().sendCommand("play")));

//        options.add(new Option(Text.of("Alpha")));
//        options.add(new Option(Text.of("Beta")));
//        options.add(new Option(Text.of("Gamma")));

        options.add(new Option(Text.of("Build"), () -> MilloMod.MC.getNetworkHandler().sendCommand("build")));
    }

    @Override
    public void render(RenderInfo info) {
        if (!openKey.isPressed()) return;
        DrawContext context = info.context();
        TextRenderer textRenderer = info.textRenderer();
        int centerX = info.width()/2;
        int centerY = info.height()/2;



        for (int i = 0; i < options.size(); i++) {
            double angle = Math.toRadians(i * (360d / options.size()) - 90);
            int x = centerX + (int) (Math.cos(angle) * 80);
            int y = centerY + (int) (Math.sin(angle) * 80);

            Option option = options.get(i);
            option.draw(context, x, y, textRenderer, info.delta());
        }

//        context.fill(x, y, x+20, y+20, Color.PINK.hashCode());
    }


    boolean cameraLocked = false;
    @Override
    public void onTick() {
        if (!openKey.isPressed()) {
            if (cameraLocked) {
                MilloMod.MC.mouse.lockCursor();
                cameraLocked = false;

                for (Option option : options) {
                    if (option.isSelected()) {
                        option.trigger();
                        return;
                    }
                }
            }
            return;
        }

        cameraLocked = true;
        MilloMod.MC.mouse.unlockCursor();

        double mouseX = MilloMod.MC.mouse.getX();
        double mouseY = MilloMod.MC.mouse.getY();

        int centerX = MilloMod.MC.getWindow().getWidth() / 2;
        int centerY = MilloMod.MC.getWindow().getHeight() / 2;

        int dx = (int) (mouseX - centerX);
        int dy = (int) (mouseY - centerY);
        float dist = (float) Math.sqrt(dx*dx + dy*dy);
        if (dist < 40) {
            options.forEach(i -> i.setSelected(false));
            return;
        }


        double mouseAngle = Math.atan2(mouseY - centerY, mouseX - centerX);
        if (mouseAngle < 0) {
            mouseAngle += 2 * Math.PI;
        }

        double angleSize = Math.toRadians(360d / options.size());
        for (int i = 0; i < options.size(); i++) {
            double angle = i * angleSize - Math.toRadians(90);

            if (angle < 0) {
                angle += 2 * Math.PI;
            }

            options.get(i).setSelected(angle > mouseAngle - angleSize / 2d && angle < mouseAngle + angleSize / 2d);
        }
    }

    private static class Option {
        private boolean selected = false;
        private final Text text;
        private float hover = 0f;
        private Callback callback;
        public Option(Text text, Callback callback) {
            this.text = text;
            this.callback = callback;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public boolean isSelected() {
            return selected;
        }

        public void draw(DrawContext context, int x, int y, TextRenderer textRenderer, float delta) {
            hover = MathHelper.lerp(delta, hover, isSelected() ? 1f : 0f);

            if (selected) drawMouseLine(context, x, y);

            context.getMatrices().push();
            context.getMatrices().translate(x, y, 0);
            context.getMatrices().scale(hover*0.2f+1f, hover*0.2f+1f, 1);

            int color = new Color(0f, 0f, 0f, 0.2f + hover * 0.3f).hashCode();
            int borderCol = new Color(1f-hover, 1f, 1f, 1f).hashCode();
            context.fill(-20, -20, 20, 20, color);
            context.drawBorder(-20, -20, 40, 40, borderCol);

            int w = textRenderer.getWidth(text);
            context.drawText(textRenderer, text, -w / 2, -5, Color.WHITE.hashCode(), true);

            context.getMatrices().pop();
        }

        private void drawMouseLine(DrawContext context, int x, int y) {
            var window = MilloMod.MC.getWindow();
            double mouseX = MilloMod.MC.mouse.getX() / window.getWidth() * window.getScaledWidth();
            double mouseY = MilloMod.MC.mouse.getY() / window.getHeight() * window.getScaledHeight();;

            double dx = (x - mouseX);
            double dy = (y - mouseY);

            double dist = Math.sqrt(dx*dx + dy*dy);
            dx /= dist;
            dy /= dist;

            for (int j = 0; j < 20; j++) {
                mouseX += dx;
                mouseY += dy;

                int color = new Color(1f, 1f, 1f, (1f - j/20f) * hover).hashCode();
                context.fill((int) mouseX, (int) mouseY, (int) (mouseX+1), (int) (mouseY+1), color);
            }
        }

        public void trigger() {
            callback.run();
        }
    }

    @Override
    public String getKey() {
        return "mode_switcher";
    }

    @Override
    public void loadKeybinds() {
        openKey = KeyBindingRegistryImpl.registerKeyBinding(
                new KeyBinding(
                        "key.millo.mode_switcher",
                        InputUtil.Type.KEYSYM,
                        -1,
                        "key.category.millo"
                )
        );
    }

    public void triggerKeybind(Config config) {
    }

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

}
