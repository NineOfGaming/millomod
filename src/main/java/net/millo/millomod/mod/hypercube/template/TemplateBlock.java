package net.millo.millomod.mod.hypercube.template;

import com.google.gson.JsonObject;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.lang.reflect.Array;
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
                result.append(getArguments());
                result.append(split[1]);
                continue;
            }
            MutableText text = Text.literal(part);
            result.append(text);
        }

        return result;
    }

    public MutableText getArguments() {
        return Text.literal(args.getAsJsonArray("items").size()+"");
    }


}
