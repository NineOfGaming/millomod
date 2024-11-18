package net.millo.millomod.mod.features.gui;

import net.millo.millomod.mod.util.gui.GUI;
import net.millo.millomod.mod.util.gui.GUIStyles;
import net.millo.millomod.mod.util.gui.elements.TextElement;
import net.minecraft.text.Text;

public class FriendsGui extends GUI {

    public FriendsGui() {
        super(Text.literal("Friends"));
    }

    @Override
    protected void init() {
        super.init();

        int x = paddingX;
        int y = paddingY;
        addDrawableChild(new TextElement(x, y + 10, backgroundWidth, 16,
                Text.literal("Friends").setStyle(GUIStyles.NAME.getStyle()), textRenderer));



    }
}
