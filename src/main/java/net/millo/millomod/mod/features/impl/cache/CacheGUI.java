package net.millo.millomod.mod.features.impl.cache;

import net.millo.millomod.mod.hypercube.template.Template;
import net.millo.millomod.mod.hypercube.template.TemplateBlock;
import net.millo.millomod.mod.util.gui.GUI;
import net.millo.millomod.mod.util.gui.GUIStyles;
import net.millo.millomod.mod.util.gui.elements.ScrollableElement;
import net.millo.millomod.mod.util.gui.elements.TextElement;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;

import java.util.Objects;

public class CacheGUI extends GUI {
    public static CacheGUI lastOpenedGUI;
    Template template;
    int indentation;
    public CacheGUI() {
        super(Text.of("Cache"));
        lastOpenedGUI = this;
    }
    public CacheGUI(Template template) {
        this();
        this.template = template;
    }

    public void loadTemplate(Template template){
        this.template = template;
        clearChildren();
        init();
    }

    public TextRenderer getTextRenderer() {
        return textRenderer;
    }

    protected void init() {
        super.init();

        System.out.println(template);

        if (template == null) {
            addDrawableChild(new TextElement(paddingX, paddingY, backgroundWidth, backgroundHeight,
                    Text.literal("Empty").setStyle(GUIStyles.COMMENT.getStyle()),
                    textRenderer));
            return;
        }

        ScrollableElement lines = new ScrollableElement(paddingX, paddingY, backgroundWidth, backgroundHeight, Text.literal(""));

        int worldProgress = 0; // keeps track of how many in world blocks have gone by
        int lineNum = 0;
        indentation = 0;
        for (TemplateBlock i : template.blocks) {
            lineNum++;
            if (Objects.equals(i.id, "bracket"))
                if (Objects.equals(i.direct, "close")) {
                    indentation--;
                    if (indentation < 0) indentation = 0;
                } else worldProgress -= 2;

            LineElement line = i.toLine();
            line.setIndent(indentation);
            line.setLineNum(lineNum, template.startPos.add(-1, 0, worldProgress));
            line.init(backgroundWidth, 12);
            lines.addDrawableChild(line);

            if (Objects.equals(i.id, "bracket") && Objects.equals(i.direct, "open") ||
                    Objects.equals(i.block, "func") || Objects.equals(i.block, "process") ||
                    Objects.equals(i.block, "event") || Objects.equals(i.block, "entity_event")) {
                indentation++;
            }
            worldProgress += 2;
        }


        addDrawableChild(lines);
    }



}
