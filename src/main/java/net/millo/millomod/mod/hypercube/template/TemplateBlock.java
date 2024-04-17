package net.millo.millomod.mod.hypercube.template;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.millo.millomod.mod.features.impl.cache.ArgumentItem;
import net.millo.millomod.mod.features.impl.cache.LineElement;
import net.millo.millomod.mod.util.gui.GUIStyles;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.*;

@SuppressWarnings("unused")
public class TemplateBlock {
    public String id;
    public String direct;
    public String type;
    public String block;
    public String data;
    public String action;
    public JsonObject args;
    public String target;
    public String attribute;
    public String subAction;

    public String toString() {
        return "TemplateBlock{" +
                "id='" + id + '\'' +
                ", direct='" + direct + '\'' +
                ", type='" + type + '\'' +
                ", block='" + block + '\'' +
                ", data='" + data + '\'' +
                ", action='" + action + '\'' +
                ", args='" + args + '\'' +
                ", target='" + target + '\'' +
                '}';
    }



    // TODO: Target, like default or victim on player action, and on selects.
    enum Blocks {
        ENTITY_EVENT((block) -> generateEventLine(block, "entity_event")),
        EVENT((block) -> generateEventLine(block, "event")),
        FUNC((block) -> generateFlowLine(block, "function")),
        PROCESS((block) -> generateFlowLine(block, "process")),
        START_PROCESS((block) -> generateFlowLine(block, "start")),
        CALL_FUNC((block) -> generateFlowLine(block, "call")),
        IF_VAR((block) -> {
            ArrayList<ArgumentItem> items = block.getArguments();
            LineElement line = new LineElement()
                    .addComponent(Text.of("if (" + (block.isNot() ? (block.action.equals("=") ? "" : "!") : "")));
            items.get(0).addTo(line);
            line.addSpace()
                    .addComponent(Text.literal(block.action.equals("=") ? (block.isNot() ? "!=" : "==") : block.action).setStyle(GUIStyles.ACTION.getStyle()))
                    .addSpace();

            items.remove(0);

            Iterator<ArgumentItem> args = items.iterator();
            while (args.hasNext()) {
                ArgumentItem arg = args.next();
                arg.addTo(line);
                if (args.hasNext()) line.addComponent(Text.literal(", "));
            }

            line.addComponent(Text.of(")"));

            return line;
        }),
        IF_PLAYER((block) -> generateConditionLine(block, "player.")),
        IF_ENTITY((block) -> generateConditionLine(block, "entity.")),
        IF_GAME((block) -> generateConditionLine(block, "game.")),
        ELSE((block) -> new LineElement().addComponent(Text.of("else"))),
        REPEAT((block) -> new LineElement()
                .addComponent(Text.literal("repeat").setStyle(GUIStyles.VECTOR.getStyle()))
                .addSpace()
                .addComponent(Text.of(block.action))
                .addArguments(block.getArguments())),
        SET_VAR(Blocks::generateSetVarLine),
        PLAYER_ACTION((block) -> generateCommonLine(block, "player")),
        ENTITY_ACTION((block) -> generateCommonLine(block, "entity")),
        GAME_ACTION((block) -> generateCommonLine(block, "game")),

        CONTROL((block) -> {
            if (block.action.equals("StopRepeat"))
                return new LineElement().addComponent(Text.literal("break").setStyle(GUIStyles.CONTROL.getStyle()));
            if (block.action.equals("Skip"))
                return new LineElement().addComponent(Text.literal("continue").setStyle(GUIStyles.CONTROL.getStyle()));
            if (block.action.equals("Return"))
                return new LineElement().addComponent(Text.literal("return").setStyle(GUIStyles.CONTROL.getStyle()));

            return new LineElement()
                    .addComponent(Text.literal(block.action).setStyle(GUIStyles.CONTROL.getStyle()))
                    .addArguments(block.getArguments());
        }),
        SELECT_OBJ((block) -> new LineElement()
                .addComponent(Text.literal("select").setStyle(GUIStyles.SELECT.getStyle()))
                .addComponent(Text.of("."))
                .addComponent(Text.of((block.isNot() ? "!" : "") + block.action))
                .addComponent(Text.of(block.subAction == null ? "" : "." + block.subAction))
                .addArguments(block.getArguments())),


        ;
        final BlockToLine btl;
        Blocks(BlockToLine btl) {
            this.btl = btl;
        }

        private static LineElement generateEventLine(TemplateBlock block, String keyword) {
            return new LineElement()
                    .addComponent(Text.of(keyword))
                    .addSpace()
                    .addComponent(Text.literal(block.action).setStyle(GUIStyles.HEADER.getStyle()))
                    .addArguments(block.getArguments())
                    .addComponent(Text.of(block.lsCancel() ? ".lsCancel()" : ""));
        }

        private static LineElement generateFlowLine(TemplateBlock block, String keyword) {
            return new LineElement()
                    .addComponent(Text.of(keyword))
                    .addSpace()
                    .addComponent(Text.literal(block.data).setStyle(GUIStyles.NAME.getStyle()))
                    .addArguments(block.getArguments());
        }
        private static LineElement generateCommonLine(TemplateBlock block, String keyword) {
            return new LineElement()
                    .addComponent(Text.of(keyword))
                    .addComponent(Text.of("."))
                    .addComponent(Text.literal(block.action.trim()))
                    .addArguments(block.getArguments());
        }

        private static LineElement generateSetVarLine(TemplateBlock block) {
            ArrayList<ArgumentItem> items = block.getArguments();
            LineElement line = new LineElement();

            if (items.isEmpty()) return generateCommonLine(block, "set_var");

            if (items.size() == 1) {
                if (block.action.equals("+=")) {
                    items.get(0).addTo(line);
                    line.addComponent(Text.of("++"));
                    return line;
                }
                if (block.action.equals("-=")) {
                    items.get(0).addTo(line);
                    line.addComponent(Text.of("--"));
                    return line;
                }
            }

            Map<String, String> actionSymbols = new HashMap<>();
            actionSymbols.put("=", "+");
            actionSymbols.put("+=", "+");
            actionSymbols.put("-=", "+");
            actionSymbols.put("+", "+");
            actionSymbols.put("-", "-");
            actionSymbols.put("x", "x");


            if (actionSymbols.containsKey(block.action)) {
                String symbol = actionSymbols.get(block.action);

                items.get(0).addTo(line);

                if (block.action.contains("=")) line.addComponent(Text.of(" " + block.action + " "));
                else line.addComponent(Text.of(" = "));

                items.remove(0);
                Iterator<ArgumentItem> args = items.iterator();
                while (args.hasNext()) {
                    ArgumentItem arg = args.next();
                    arg.addTo(line);
                    if (args.hasNext()) line.addComponent(Text.literal(" " + symbol + " "));
                }

                return line;
            }

            return generateCommonLine(block, "set_var");
        }

        private static LineElement generateConditionLine(TemplateBlock block, String key) {
            return new LineElement()
                    .addComponent(Text.of("if ("+(block.isNot() ? "!" : "")+key))
                    .addComponent(Text.literal(block.action).setStyle(GUIStyles.ACTION.getStyle()))
                    .addArguments(block.getArguments())
                    .addComponent(Text.of(")"));
        }
    }

    private boolean isNot() {
        return attribute != null && attribute.equals("NOT");
    }
    private boolean lsCancel() {
        return attribute != null && attribute.equals("LS-CANCEL");
    }

    private interface BlockToLine {
        LineElement parse(TemplateBlock block);
    }


    public LineElement toLine() {
        if (id.equals("bracket")) {
            if (direct.equals("open")) return new LineElement().addComponent(Text.literal("{").setStyle(Style.EMPTY.withColor(new Color(134, 161, 218).hashCode())));
            return new LineElement().addComponent(Text.literal("}").setStyle(Style.EMPTY.withColor(new Color(134, 161, 218).hashCode())));
        }
        try {
            Blocks b = Blocks.valueOf(block.toUpperCase());
            LineElement line = b.btl.parse(this);
            if (target != null) {
                line.addComponent(Text.of("."))
                        .addComponent(Text.of(target))
                        .addComponent(Text.of("()"));
            }
            return line;
        } catch (IllegalArgumentException e) {
            return new LineElement().addComponent(Text.literal(toString()));
        }
    }

    public ArrayList<ArgumentItem> getArguments() {
        ArrayList<ArgumentItem> argumentItems = new ArrayList<>();

        JsonArray items = args.getAsJsonArray("items");
        for (JsonElement itemSlot : items) {
            JsonObject item = itemSlot.getAsJsonObject().getAsJsonObject("item");
            String id = item.get("id").getAsString();
            if (Objects.equals(id, "hint")) continue; // remove this shit

            JsonObject data = item.getAsJsonObject("data");

            argumentItems.add(new ArgumentItem(id, data));
        }

        return argumentItems;
    }


}
