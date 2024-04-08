package net.millo.millomod.mod.features.impl.cache;

import net.millo.millomod.mod.hypercube.template.Template;
import net.millo.millomod.mod.util.gui.GUI;
import net.millo.millomod.mod.util.gui.GUIStyles;
import net.millo.millomod.mod.util.gui.elements.ButtonElement;
import net.millo.millomod.mod.util.gui.elements.ScrollableElement;
import net.millo.millomod.mod.util.gui.elements.TextElement;
import net.minecraft.text.Text;

import java.util.Objects;

public class CacheGUI extends GUI {
    Template template;
    int indentation;
    public CacheGUI(Template template) {
        super(Text.of("Cache"));
        this.template = template;
    }

    protected void init() {
        super.init();

        System.out.println(template);

        if (template == null) {
            return;
        }

        ScrollableElement lines = new ScrollableElement(paddingX, paddingY, backgroundWidth, backgroundHeight, Text.literal(""));


        indentation = 0;
        template.blocks.forEach(i -> {
            if (Objects.equals(i.id, "bracket") && Objects.equals(i.direct, "close")) {
                indentation--;
                if (indentation < 0) indentation = 0;
            }

            TextElement text = new TextElement(0, 0, backgroundWidth, 12,
                    Text.literal("   ".repeat(indentation)).append(i.toText()).setStyle(GUIStyles.TITLE.getStyle()), textRenderer);
            text.alignLeft();
            lines.addDrawableChild(text);

            if (Objects.equals(i.id, "bracket") && Objects.equals(i.direct, "open") || Objects.equals(i.block, "func") || Objects.equals(i.block, "process")) {
                indentation++;
            }
        });


        addDrawableChild(lines);
    }

}
