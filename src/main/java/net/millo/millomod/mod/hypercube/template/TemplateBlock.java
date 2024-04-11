package net.millo.millomod.mod.hypercube.template;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.millo.millomod.mod.features.impl.cache.ArgumentItem;
import net.millo.millomod.mod.features.impl.cache.LineElement;
import net.millo.millomod.mod.util.gui.GUIStyles;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.*;

public class TemplateBlock {
    public String id;
    public String direct;
    public String type;
    public String block;
    public String data;
    public String action;
    public JsonObject args;

    public String toString() {
        return "TemplateBlock{" +
                "id='" + id + '\'' +
                ", direct='" + direct + '\'' +
                ", type='" + type + '\'' +
                ", block='" + block + '\'' +
                ", data='" + data + '\'' +
                ", action='" + action + '\'' +
                ", args='" + args + '\'' +
                '}';
    }


    enum Blocks {
        FUNC((block) -> generatePipelineLine(block, "function")),
        PROCESS((block) -> generatePipelineLine(block, "process")),
        START_PROCESS((block) -> generatePipelineLine(block, "start")),
        CALL_FUNC((block) -> generatePipelineLine(block, "call")),
        IF_VAR((block) -> {
            ArrayList<ArgumentItem> items = block.getArguments();
            LineElement line = new LineElement()
                    .addComponent(Text.of("if ("));
            items.get(0).addTo(line);
            line.addSpace()
                    .addComponent(Text.literal(block.action).setStyle(GUIStyles.ACTION.getStyle()))
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
        SET_VAR((block) -> {
            Map<String, String> actionSymbols = new HashMap<>();
            actionSymbols.put("=", "+");
            actionSymbols.put("+=", "+");
            actionSymbols.put("-=", "+");
            actionSymbols.put("+", "+");
            actionSymbols.put("-", "-");
            actionSymbols.put("x", "x"); // no clue if this is correct
            actionSymbols.put("/", "+");


            ArrayList<ArgumentItem> items = block.getArguments();
            LineElement line = new LineElement();
            if (actionSymbols.containsKey(block.action)) {
                String symbol = actionSymbols.get(block.action);
                items.get(0).addTo(line);
                line.addComponent(Text.of(" " + block.action + " "));

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
        }),
        PLAYER_ACTION((block) -> generateCommonLine(block, "player")),
        ENTITY_ACTION((block) -> generateCommonLine(block, "entity")),
        GAME_ACTION((block) -> generateCommonLine(block, "game")),

        CONTROL((block) -> new LineElement()
                .addComponent(Text.of(block.action))
                .addArguments(block.getArguments())),
        SELECT_OBJECT((block) -> new LineElement()
                .addComponent(Text.literal("select").setStyle(GUIStyles.SELECT.getStyle()))
                .addComponent(Text.of(block.action))
                .addArguments(block.getArguments())),


        ;
        final BlockToLine btl;
        Blocks(BlockToLine btl) {
            this.btl = btl;
        }

        private static LineElement generatePipelineLine(TemplateBlock block, String keyword) {
            return new LineElement()
                    .addComponent(Text.of(keyword))
                    .addSpace()
                    .addComponent(Text.literal(block.data).setStyle(GUIStyles.NAME.getStyle()))
                    .addArguments(block.getArguments());
        }
        private static LineElement generateCommonLine(TemplateBlock block, String keyword) {
            return new LineElement()
                    .addComponent(Text.of(keyword))
                    .addSpace()
                    .addComponent(Text.literal(block.action))
                    .addArguments(block.getArguments());
        }
    }

    private interface BlockToLine {
        LineElement parse(TemplateBlock block);
    }


    public LineElement toLine() {
        if (Objects.equals(id, "bracket")) {
            if (Objects.equals(direct, "open")) return new LineElement().addComponent(Text.literal("{").setStyle(Style.EMPTY.withColor(new Color(134, 161, 218).hashCode())));
            return new LineElement().addComponent(Text.literal("}").setStyle(Style.EMPTY.withColor(new Color(134, 161, 218).hashCode())));
        }
        try {
            Blocks b = Blocks.valueOf(block.toUpperCase());
            return b.btl.parse(this);
        } catch (IllegalArgumentException e) {
            return new LineElement().addComponent(Text.literal(toString()));
        }
    }

    public MutableText toText() {
        if (Objects.equals(block, "process")) return parse("#block ", "`#data`", "(#args)");
        if (Objects.equals(block, "func")) return parse("#block ", "`#data`", "(#args)");
        if (Objects.equals(block, "call_func")) return parse("call `#data`", "(#args)");

        if (Objects.equals(block, "game_action")) return parse("#block.", "#action", "(#args)");
        if (Objects.equals(block, "entity_action")) return parse("#block.", "#action", "(#args)");
        if (Objects.equals(block, "player_action")) return parse("#block.", "#action", "(#args)");

        if (Objects.equals(block, "select_obj")) return parse("select.", "#action", "(#args)");
        if (Objects.equals(block, "control")) return parse("control.", "#action", "(#args)");

        if (Objects.equals(block, "if_var")) return parse("if `#action`", "(#args)");
        if (Objects.equals(block, "if_player")) return parse("if `#action`", "(#args)");

        if (Objects.equals(id, "bracket")) {
            if (Objects.equals(direct, "open")) return Text.literal("{");
            return Text.literal("}");
        }


        return Text.literal(toString());
    }


    public MutableText parse(String ...parts) {
        MutableText result = Text.empty();

        for (String part : parts) {
            if (block != null) part = part.replace("#block", block);
            if (data != null) part = part.replace("#data", data);
            if (action != null) part = part.replace("#action", action);
            if (args != null && part.contains("#args")) {
                String[] split = part.split("#args");
                result.append(split[0]);
                result.append(getArgCount());
                result.append(split[1]);
                continue;
            }
            MutableText text = Text.literal(part);
            result.append(text);
        }

        return result;
    }

    public MutableText getArgCount() {
        return Text.literal(args.getAsJsonArray("items").size()+"");
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
