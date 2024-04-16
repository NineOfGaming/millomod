package net.millo.millomod.mod.features.gui;

import net.millo.millomod.mod.features.Feature;
import net.millo.millomod.mod.features.FeatureHandler;
import net.millo.millomod.mod.features.IRenderable;
import net.millo.millomod.mod.util.gui.GUI;
import net.millo.millomod.mod.util.gui.elements.MoveableElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class PositionsGUI extends GUI {
    Screen parent;

    // TODO: Snapping

    public PositionsGUI(Screen parent) {
        super(Text.literal("Positions"));
        this.parent = parent;
        getFade().fadeIn(5f);
    }

    @Override
    protected void init() {
        super.init();

        for (Feature feature : FeatureHandler.getFeatures()) {
            if (feature instanceof IRenderable) {
                addMovable((IRenderable) feature);
            }
        }
    }

    private void addMovable(IRenderable feature) {
        MoveableElement m = new MoveableElement(feature, feature.getKey(), textRenderer);
        addDrawableChild(m);
    }


    @Override
    public void close() {
        assert client != null;
        client.setScreen(parent);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {}
}
