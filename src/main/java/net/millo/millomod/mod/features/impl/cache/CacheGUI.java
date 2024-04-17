package net.millo.millomod.mod.features.impl.cache;

import net.millo.millomod.mod.features.impl.Tracker;
import net.millo.millomod.mod.hypercube.template.Template;
import net.millo.millomod.mod.hypercube.template.TemplateBlock;
import net.millo.millomod.mod.util.gui.GUI;
import net.millo.millomod.mod.util.gui.GUIStyles;
import net.millo.millomod.mod.util.gui.elements.ButtonElement;
import net.millo.millomod.mod.util.gui.elements.ScrollableElement;
import net.millo.millomod.mod.util.gui.elements.TextElement;
import net.millo.millomod.mod.util.gui.elements.TextFieldElement;
import net.millo.millomod.system.FileManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

public class CacheGUI extends GUI {
    public static CacheGUI lastOpenedGUI;
    private Template template;
    private boolean hierarchyOpen = true;
    private double hierarchyX = paddingX;

    private ScrollableElement lines;
    private ButtonElement hierarchyButton;
    private TextElement plotIdText;
    private ScrollableElement templates;
    private TextFieldElement searchBar;

    private ArrayList<String> methodNames = new ArrayList<>();


    private int plotId;

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
        lines.setFade(getFade());
        templates.setFade(getFade());
        searchBar.setFade(getFade());
        hierarchyButton.setFade(getFade());
        plotIdText.setFade(getFade());
    }

    public TextRenderer getTextRenderer() {
        return textRenderer;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        double desiredHierarchyX = hierarchyOpen ? width / 5d : paddingX;
        hierarchyX = MathHelper.lerp(delta, hierarchyX, desiredHierarchyX);

        if (lines != null) {
            lines.setX((int) hierarchyX);
            lines.setWidth(backgroundWidth - (int) hierarchyX + paddingX);
        }

        hierarchyButton.setX((int) hierarchyX);
        hierarchyButton.setRealX((int) hierarchyX);

        plotIdText.visible = hierarchyOpen;
        plotIdText.setWidth((int) (hierarchyX - paddingX));


        templates.setWidth((int) (hierarchyX - paddingX));
        templates.getDrawables().forEach(i -> {
            if (i instanceof ButtonElement) {
                ((ButtonElement) i).setWidth((int) (hierarchyX - paddingX) - 8);
            }
        });

        searchBar.setVisible(hierarchyOpen);
        searchBar.setWidth((int) (hierarchyX - paddingX - 8));

        super.render(context, mouseX, mouseY, delta);
    }

    protected void init() {
        super.init();
        plotId = Tracker.getPlotId();

        // Toolbar
        int toolbarSize = 20;
        hierarchyButton = new ButtonElement(
                paddingX, paddingY, toolbarSize, toolbarSize, Text.of(">"),
                (button) -> {
                    if (hierarchyOpen) button.setText(Text.of(">"));
                    else button.setText(Text.of("<"));
                    hierarchyOpen = !hierarchyOpen;
                },
                textRenderer);
        addDrawableChild(hierarchyButton);



        // template exists
        if (template == null) {
            addDrawableChild(new TextElement(paddingX, paddingY+ toolbarSize, backgroundWidth, backgroundHeight - toolbarSize,
                    Text.literal("Empty").setStyle(GUIStyles.COMMENT.getStyle()),
                    textRenderer));
        }
        plotIdText = new TextElement(paddingX, paddingY, 0, 20,
                Text.literal("Plot: "+plotId).setStyle(GUIStyles.HEADER.getStyle()),
                textRenderer);
        addDrawableChild(plotIdText);



        // list of plot templates
        searchBar = new TextFieldElement(textRenderer, paddingX + 4, paddingY + toolbarSize, 50, 16, Text.of(""));
        searchBar.setPlaceholder(Text.literal("Search...").setStyle(GUIStyles.COMMENT.getStyle()));
        searchBar.setChangedListener(s -> updateTemplateList());
        addDrawableChild(searchBar);

        templates = new ScrollableElement(paddingX, paddingY + toolbarSize + 16, 50, backgroundHeight - toolbarSize, Text.literal(""));
        methodNames = (ArrayList<String>) FileManager.getTemplatesFromPlot(plotId);
        updateTemplateList();

        addDrawableChild(templates);

        if (template == null) {
            return;
        }

        // The lines of code
        lines = new ScrollableElement(paddingX, paddingY + toolbarSize, backgroundWidth, backgroundHeight - toolbarSize, Text.literal(""));

        int worldProgress = 0; // keeps track of how many in world blocks have gone by
        int lineNum = 0;
        int indentation = 0;
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

    private void updateTemplateList() {
        templates.clear();
        String[] match = searchBar.getText().trim().toLowerCase().split(" ");
        for (String methodName : methodNames) {
            if (!Arrays.stream(match).allMatch(i -> methodName.toLowerCase().contains(i)) && !searchBar.getText().trim().isEmpty()) {
                continue;
            }

            templates.addDrawableChild(new ButtonElement(0, 0, 50, 16, Text.of(methodName), (button) -> {

                Template template = FileManager.readTemplate(plotId, methodName);
                this.loadTemplate(template);

                System.out.println(methodName);
            }, textRenderer));
        }
    }


}
