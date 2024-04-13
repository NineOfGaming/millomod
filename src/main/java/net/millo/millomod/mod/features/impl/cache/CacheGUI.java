package net.millo.millomod.mod.features.impl.cache;

import net.millo.millomod.mod.hypercube.template.Template;
import net.millo.millomod.mod.hypercube.template.TemplateBlock;
import net.millo.millomod.mod.util.gui.GUI;
import net.millo.millomod.mod.util.gui.elements.ScrollableElement;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;

import java.util.Objects;

public class CacheGUI extends GUI {
    public static CacheGUI lastOpenedGUI;
    Template template;
    int indentation;
    public CacheGUI(Template template) {
        super(Text.of("Cache"));
        this.template = template;
        lastOpenedGUI = this;
    }

    public TextRenderer getTextRenderer() {
        return textRenderer;
    }

    protected void init() {
        super.init();

        System.out.println(template);

        if (template == null) {
            return;
        }

        ScrollableElement lines = new ScrollableElement(paddingX, paddingY, backgroundWidth, backgroundHeight, Text.literal(""));

        int lineNum = 0;
        indentation = 0;
        for (TemplateBlock i : template.blocks) {
            lineNum++;
            if (Objects.equals(i.id, "bracket") && Objects.equals(i.direct, "close")) {
                indentation--;
                if (indentation < 0) indentation = 0;
            }

            LineElement line = i.toLine();
            line.setIndent(indentation);
            line.setLineNum(lineNum);
            line.init(backgroundWidth, 12);
            lines.addDrawableChild(line);

            if (Objects.equals(i.id, "bracket") && Objects.equals(i.direct, "open") || Objects.equals(i.block, "func") || Objects.equals(i.block, "process")) {
                indentation++;
            }
        }


        addDrawableChild(lines);
    }



}
