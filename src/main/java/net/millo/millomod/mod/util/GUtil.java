package net.millo.millomod.mod.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GUtil {


    /**
     * Prepares a text object for use in an item's display tag
     * @return Usable in lore and as a name in nbt.
     */
    public static NbtString nbtify(Text text) {
        JsonElement json = Text.Serialization.toJsonTree(text);
        if(json.isJsonObject()) {
            JsonObject obj = (JsonObject) json;

            if(!obj.has("color")) obj.addProperty("color","white");
            if(!obj.has("italic")) obj.addProperty("italic",false);
            if(!obj.has("bold")) obj.addProperty("bold",false);

            return NbtString.of(obj.toString());
        }
        else return NbtString.of(json.toString());
    }

    /**
     * Parses § formatted strings.
     * @param text § formatted string.
     * @return Text with all parsed text as siblings.
     */
    public static MutableText textFromString(String text) {
        MutableText output = Text.empty().setStyle(Text.empty().getStyle().withColor(TextColor.fromRgb(0xFFFFFF)).withItalic(false));
        MutableText component = Text.empty();

        Matcher m = Pattern.compile("§(([0-9a-kfmnolr])|x(§[0-9a-f]){6})|[^§]+").matcher(text);
        while (m.find()) {
            String data = m.group();
            if(data.startsWith("§")) {
                if(data.startsWith("§x")) {
                    component = component.setStyle(component.getStyle().withColor(Integer.valueOf(data.replaceAll("§x|§",""), 16)));
                }
                else {
                    component = component.formatted(Formatting.byCode(data.charAt(1)));
                }
            }
            else {
                component.append(data);
                output.append(component);
                component = Text.empty().setStyle(component.getStyle());
            }
        }
        return output;
    }

}
