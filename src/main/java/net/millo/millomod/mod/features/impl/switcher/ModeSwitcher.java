package net.millo.millomod.mod.features.impl.switcher;

import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.millo.millomod.KeybindHandler;
import net.millo.millomod.MilloMod;
import net.millo.millomod.mod.Callback;
import net.millo.millomod.mod.features.Feature;
import net.millo.millomod.mod.features.FeatureHandler;
import net.millo.millomod.mod.features.IRenderable;
import net.millo.millomod.mod.features.Keybound;
import net.millo.millomod.mod.features.impl.util.NotificationTray;
import net.millo.millomod.mod.features.impl.util.Tracker;
import net.millo.millomod.mod.util.GlobalUtil;
import net.millo.millomod.mod.util.RenderInfo;
import net.millo.millomod.mod.util.gui.GUIStyles;
import net.millo.millomod.system.Utility;
import net.millo.millomod.system.Config;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

public class ModeSwitcher extends Feature implements Keybound, IRenderable {

    int x, y, width, height;
    KeyBinding openKey;

    ArrayList<Option> options = new ArrayList<>();
    private int page = 0;
    private float shown = 0f;
    private float rotateBump = 0f;


    // TODO: if player is in spawn, show "favourites" on first page instead


    private static ArrayList<Option> addCommandOption(ArrayList<Option> list, String text, String command) {
        list.add(new Option(Text.of(text), () -> Utility.sendCommand(command)));
        return list;
    }

    @Override
    public boolean scrollInHotbar(double amount) {
        if (shown > 0.1f) {
            setPage((int) (page - amount));
            rotateBump = (float) (1.2f * MathHelper.clamp(amount, -1f, 1f));
            return true;
        }
        return false;
    }

    public void setPage(int page) {
        options = getPage(page);
    }

    private static final ArrayList<ArrayList<Option>> pages;
    static {
        pages = new ArrayList<>();
        pages.add(createPage(
                "Dev", "dev",
                "Play", "play",
                "Build", "build"
                ));

        pages.add(createPage(
                "Not", "not",
                "Cancel", "cancel",
                "Refer", "reference",
                "B.F.S.", "bracket",
                "G. Val", "val",
                "Values", "values"
        ));

        pages.add(createPage(
                "Spawn", "s",
                "Node 1", "server node1",
                "Node 2", "server node2",
                "Node 3", "server node3",
                "Node 4", "server node4",
                "Node 5", "server node5",
                "Node 6", "server node6",
                "Node 7", "server node7",
                "Beta", "server beta"
        ));

        ArrayList<Option> page3 = new ArrayList<>();
        addCommandOption(page3, "C l", "c l");
        addCommandOption(page3, "C g", "c g");
        addCommandOption(page3, "C n", "c n");
        addCommandOption(page3, "C dnd", "c dnd");
        page3.add(new Option(Text.of("Auto @"), () -> {
            FeatureHandler.getFeature("auto_command").toggleEnabled();
            NotificationTray.pushNotification(
                    Text.literal("Toggled"),
                    Text.translatable("config.millo.auto_command"),
                    GUIStyles.getTrueFalse(true)
            );
        }));
        pages.add(page3);

        pages.add(createPage(
                "Creat", "gmc",
                "Adven", "gma",
                "Survi", "gms",
                "Spect", "gmsp"
        ));
    }

    private static ArrayList<Option> createPage(String ...args) {
        ArrayList<Option> page = new ArrayList<>();
        for (int i = 0; i < args.length; i += 2) {
            String name = args[i];
            String command = args[i + 1];
            addCommandOption(page, name, command);
        }
        return page;
    }

    private ArrayList<Option> getPage(int page) {
        this.page = (5 + page) % 5;
        return pages.get(this.page);
    }

    public ModeSwitcher() {
        options = getPage(0);
    }


    @Override
    public void render(RenderInfo info) {
        shown = MathHelper.lerp(info.delta(), shown, openKey.isPressed() ? 1f : 0f);
        if (shown < 0.01f) return;
        rotateBump = MathHelper.lerp(info.delta(), rotateBump, 0f);

        DrawContext context = info.context();
        TextRenderer textRenderer = info.textRenderer();
        int centerX = info.width()/2;
        int centerY = info.height()/2;


        for (int i = 0; i < options.size(); i++) {
            double angle = Math.toRadians(i * (360d / options.size()) - 90) - (1f - shown) * 0.8d + rotateBump;
            int x = centerX + (int) (Math.cos(angle) * 80 * shown);
            int y = centerY + (int) (Math.sin(angle) * 80 * shown);

            Option option = options.get(i);
            option.draw(context, x, y, textRenderer, info.delta(), shown);
        }


        int totalPages = pages.size();
        int w = ((totalPages) * 5) / 2 - 1;
        context.getMatrices().push();
        context.getMatrices().translate(centerX, centerY + 10, 0);
        context.getMatrices().scale(shown, shown, 0);

        for (int i = 0; i < totalPages; i++) {
            if (i == page) {
                context.fill(i * 5 - w - 1, -1, i * 5 + 3 - w, 3, Color.white.hashCode());
            } else {
                context.fill(i * 5 - w, 0, i * 5 + 2 - w, 2, Color.gray.hashCode());
            }
        }

        context.getMatrices().pop();

    }


    boolean cameraLocked = false;
    @Override
    public void onTick() {
        if (!GlobalUtil.isKeyDown(openKey) || MilloMod.MC.currentScreen != null) {
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

        if (!cameraLocked) {
            page = 0;
            options = getPage(0);
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

        public void draw(DrawContext context, int x, int y, TextRenderer textRenderer, float delta, float shown) {
            hover = MathHelper.clampedLerp(hover, isSelected() ? 1f : 0f, delta);

            if (selected) drawMouseLine(context, x, y);

            context.getMatrices().push();
            context.getMatrices().translate(x, y, 0);
            context.getMatrices().scale(shown, shown, 0);
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
        while (GlobalUtil.isKeyPressed(openKey)) {
            page = 0;
            options = getPage(page);
        }
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
