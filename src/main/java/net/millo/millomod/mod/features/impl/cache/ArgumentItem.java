package net.millo.millomod.mod.features.impl.cache;

import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;

public class ArgumentItem {

    final String name;
    final Tooltip tooltip;

    public ArgumentItem(String name, Tooltip tooltip) {
        this.name = name;
        this.tooltip = tooltip;
    }


    public void addTo(LineElement line) {
        line.addComponent(Text.literal(name), tooltip);
    }
}
