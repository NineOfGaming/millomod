package net.millo.millomod.mod.hypercube.template;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.millo.millomod.mod.features.impl.cache.ArgumentItem;
import net.millo.millomod.mod.features.impl.cache.LineElement;
import net.millo.millomod.mod.util.gui.GUIStyles;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

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
        FUNC((block) -> generateCommonLine(block, "function")),
        PROCESS((block) -> generateCommonLine(block, "process")),
        START((block) -> generateCommonLine(block, "start")),
        CALL_FUNC((block) -> generateCommonLine(block, "call")),
        IF_VAR((block) -> {
            ArrayList<ArgumentItem> items = block.getArguments();
            LineElement line = new LineElement()
                    .addComponent(Text.literal("if ("));
            items.get(0).addTo(line);

            return line;
        });


        final BlockToLine btl;
        Blocks(BlockToLine btl) {
            this.btl = btl;
        }

        private static LineElement generateCommonLine(TemplateBlock block, String keyword) {
            return new LineElement()
                    .addComponent(Text.literal(keyword))
                    .addSpace()
                    .addComponent(Text.literal(block.data).setStyle(GUIStyles.NAME.getStyle()))
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
            JsonObject data = item.getAsJsonObject("data");

            argumentItems.add(new ArgumentItem(id, Tooltip.of(Text.empty())));
        }

        return argumentItems;
    }


}
