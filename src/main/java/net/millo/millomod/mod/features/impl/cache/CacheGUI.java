package net.millo.millomod.mod.features.impl.cache;

import net.millo.millomod.mod.features.impl.Tracker;
import net.millo.millomod.mod.hypercube.template.Template;
import net.millo.millomod.mod.hypercube.template.TemplateBlock;
import net.millo.millomod.mod.util.MathUtil;
import net.millo.millomod.mod.util.gui.GUI;
import net.millo.millomod.mod.util.gui.GUIStyles;
import net.millo.millomod.mod.util.gui.elements.ButtonElement;
import net.millo.millomod.mod.util.gui.elements.ScrollableElement;
import net.millo.millomod.mod.util.gui.elements.TextElement;
import net.millo.millomod.mod.util.gui.elements.TextFieldElement;
import net.millo.millomod.system.FileManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CacheGUI extends GUI {
    public static CacheGUI lastOpenedGUI;
    private static Template template;
    private boolean hierarchyOpen = true;
    private double hierarchyX = paddingX;

    private ScrollableElement lines;
    private ButtonElement hierarchyButton;
    private TextElement plotIdText;
    private ScrollableElement templates;
    private TextFieldElement searchBar;

    private ArrayList<String> methodNames = new ArrayList<>();

    private int plotId;
    int toolbarSize = 20;


    // + TODO: Click on callfunction / startprocess to open that  // (+ Forward / Backward navigation)
    // TODO: Scan entire plot
    // -- TODO: Add the ability to REMOVE cached functions
    // TODO: Search for actions
    // TODO: ^ Search for usages of method
    // + TODO: Add colours depending on what method type to the methods list
    // + TODO: Only update `lines` when changing method
    // TODO: Folders.
    //

    public CacheGUI() {
        super(Text.of("Cache"));
        lastOpenedGUI = this;
    }

    private boolean pendingTemplateListUpdate = false;
    public void loadTemplate(@NotNull Template template){
        if (CacheGUI.template != null) {
            if (template.getFileName().equals(CacheGUI.template.getFileName())) return;
            historyStack.push(CacheGUI.template.getFileName());
            futureStack.clear();
        }
        loadTemplateLines(template);
        pendingTemplateListUpdate = true;   // pending to prevent concurrency error
    }

    private void loadTemplateLines(Template template) {
        boolean reload = CacheGUI.template == null;
        CacheGUI.template = template;
        if (reload) {
            clearChildren();
            init();
            return;
        }
        addLinesFromTemplate();
        lines.setFade(getFade());
    }

    public TextRenderer getTextRenderer() {
        return textRenderer;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        double desiredHierarchyX = hierarchyOpen ? width / 5d : paddingX;
        hierarchyX = MathUtil.clampLerp(hierarchyX, desiredHierarchyX, delta);

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

    @Override
    public void tick() {
        if (pendingTemplateListUpdate) {
            pendingTemplateListUpdate = false;
            updateTemplateList();
        }
    }

    protected void init() {
        super.init();
        plotId = Tracker.getPlotId();

        // Toolbar
        hierarchyButton = new ButtonElement(
                paddingX, paddingY, toolbarSize, toolbarSize, Text.of("<"),
                (button) -> {
                    if (hierarchyOpen) button.setText(Text.of(">"));
                    else button.setText(Text.of("<"));
                    hierarchyOpen = !hierarchyOpen;
                },
                textRenderer);
        addDrawableChild(hierarchyButton);


        // template exists
        if (template == null) {
            addEmpty();
        }
        plotIdText = new TextElement(paddingX, paddingY, 0, 20,
                Text.literal("Plot: "+plotId).setStyle(GUIStyles.HEADER.getStyle()),
                textRenderer);
        addDrawableChild(plotIdText);

        // list of plot templates (hierarchy)
        searchBar = new TextFieldElement(textRenderer, paddingX + 4, paddingY + toolbarSize, 50, 16, Text.of(""));
        searchBar.setPlaceholder(Text.literal("Search...").setStyle(GUIStyles.COMMENT.getStyle()));
        searchBar.setChangedListener(s -> updateTemplateList());
        addDrawableChild(searchBar);

        templates = new ScrollableElement(paddingX, paddingY + toolbarSize + 16, 50, backgroundHeight - toolbarSize - 20, Text.literal(""));
        updateMethodNamesList();
        updateTemplateList();

        addDrawableChild(templates);

        if (template == null) {
            return;
        }

        addLinesFromTemplate();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 3) historyBack();
        if (button == 4) historyForward();
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void historyBack() {
        Template template = null;
        while (template == null && !historyStack.isEmpty()) {
            template = FileManager.readTemplate(plotId, historyStack.pop());
        }
        if (template == null) return;
        if (CacheGUI.template != null) futureStack.push(CacheGUI.template.getFileName());
        loadTemplateLines(template);
    }
    private void historyForward() {
        Template template = null;
        while (template == null && !futureStack.isEmpty()) {
            template = FileManager.readTemplate(plotId, futureStack.pop());
        }
        if (template == null) return;
        if (CacheGUI.template != null) historyStack.push(CacheGUI.template.getFileName());
        loadTemplateLines(template);
    }

    private static final Stack<String> historyStack = new Stack<>();
    private static final Stack<String> futureStack = new Stack<>();

    private void addLinesFromTemplate() {

        if (lines != null) remove(lines);
        lines = new ScrollableElement(paddingX, paddingY + toolbarSize, backgroundWidth, backgroundHeight - toolbarSize, Text.literal(""));

        if (template == null) return;

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


    private void updateMethodNamesList() {
        methodNames = (ArrayList<String>) FileManager.getTemplatesFromPlot(plotId);
    }
    private void updateTemplateList() {
        templates.clear();
        String[] match = searchBar.getText().trim().toLowerCase().split(" ");
        for (String methodName : methodNames) {
            if (!Arrays.stream(match).allMatch(i -> methodName.toLowerCase().contains(i)) && !searchBar.getText().trim().isEmpty()) {
                continue;
            }

            MethodElement b = new MethodElement(16, plotId, methodName, (button) -> {
                Template template = FileManager.readTemplate(plotId, methodName);
                loadTemplate(template);
            }, textRenderer);

            b.setFade(getFade());
            templates.addDrawableChild(b);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (modifiers == 2 && keyCode == 70) {
            searchBar.setEditable(true);
            searchBar.setSelectionStart(0);
            searchBar.setSelectionEnd(searchBar.getText().length());
            this.setFocused(searchBar);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public void findMethod(String methodName) {
        methodName = methodName.replaceAll("(?<=\\.)(start|call)_(?=(func|process))", "");
        Template template = FileManager.readTemplate(plotId, methodName);
        if (template != null) loadTemplate(template);
    }

    public void reload() {
        updateMethodNamesList();
        updateTemplateList();
        remove(lines);
        lines = null;
        template = null;
        addEmpty();
    }

    private void addEmpty() {
        addDrawableChild(new TextElement(paddingX, paddingY + toolbarSize, backgroundWidth, backgroundHeight - toolbarSize,
                Text.literal("Empty").setStyle(GUIStyles.COMMENT.getStyle()),
                textRenderer));
    }
}
