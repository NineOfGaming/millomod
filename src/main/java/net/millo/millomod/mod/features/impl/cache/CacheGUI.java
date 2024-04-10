package net.millo.millomod.mod.features.impl.cache;

import net.fabricmc.loader.impl.lib.sat4j.pb.tools.INegator;
import net.millo.millomod.mod.hypercube.template.Template;
import net.millo.millomod.mod.hypercube.template.TemplateBlock;
import net.millo.millomod.mod.util.gui.GUI;
import net.millo.millomod.mod.util.gui.GUIStyles;
import net.millo.millomod.mod.util.gui.elements.ScrollableElement;
import net.millo.millomod.mod.util.gui.elements.TextElement;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.Tooltip;
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

        lines.addDrawableChild(new TextElement(Text.literal(""), textRenderer));

//        LineElement line = new LineElement(backgroundWidth, 12, textRenderer);
//        line.addComponent(Text.literal("Hello! "));
//        line.addComponent(Text.literal("Woah!! "), Tooltip.of(Text.literal("This is awesome")));
//        line.addComponent(Text.literal("Click me!!! "), button -> {
//            System.out.println("Clicked!!!");
//        });
//        line.addComponent(Text.literal("I didn't know this could happen.."), Tooltip.of(Text.literal("Holy shit")), button -> {
//            System.out.println("Even this worked??");
//        });
//        lines.addDrawableChild(line);

        indentation = 0;
        for (TemplateBlock i : template.blocks) {
            if (Objects.equals(i.id, "bracket") && Objects.equals(i.direct, "close")) {
                indentation--;
                if (indentation < 0) indentation = 0;
            }

            LineElement line = i.toLine();
            line.setIndent(indentation);
            line.init(backgroundWidth, 12);
            lines.addDrawableChild(line);
//            TextElement text = new TextElement(0, 0, backgroundWidth, 12,
//                    Text.literal("   ".repeat(indentation)).append(i.toText()).setStyle(GUIStyles.TITLE.getStyle()), textRenderer);
//            text.alignLeft();
//            lines.addDrawableChild(text);


            if (Objects.equals(i.id, "bracket") && Objects.equals(i.direct, "open") || Objects.equals(i.block, "func") || Objects.equals(i.block, "process")) {
                indentation++;
            }
        }


        addDrawableChild(lines);
    }



}
