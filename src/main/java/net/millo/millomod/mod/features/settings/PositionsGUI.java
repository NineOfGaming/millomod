package net.millo.millomod.mod.features.settings;

import net.millo.millomod.mod.features.Feature;
import net.millo.millomod.mod.features.FeatureHandler;
import net.millo.millomod.mod.features.Renderable;
import net.millo.millomod.mod.features.gui.GUI;
import net.millo.millomod.mod.features.gui.elements.MoveableElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class PositionsGUI extends GUI {
    Screen parent;
    public PositionsGUI(Screen parent) {
        super(Text.literal("Positions"));
        this.parent = parent;
        getFade().fadeIn(5f);
    }

    @Override
    protected void init() {
        super.init();

        for (Feature feature : FeatureHandler.getFeatures()) {
            if (feature instanceof Renderable) {
                addMoveable((Renderable) feature);
            }
        }
    }

    private void addMoveable(Renderable feature) {
        MoveableElement m = new MoveableElement(feature, feature.getKeyName(), textRenderer);
        addDrawableChild(m);
    }


    @Override
    public void close() {
        client.setScreen(parent);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {}
}
