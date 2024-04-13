package net.millo.millomod.mod.features.impl.cache;

import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.millo.millomod.mod.util.gui.GUIStyles;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Collectors;

public class ArgumentItem {

    final String id;
    final JsonObject data;
    public ArgumentItem(String id, JsonObject data) {
        this.id = id;
        this.data = data;
    }

    private String get(String name) {
        return data.has(name) ? data.get(name).getAsString() : "";
    }

    public void addTo(LineElement line) {
        String scope = get("scope");
        String name = get("name");
        String tag = get("tag");
        String option = get("option");
        String type = get("type");
        String target = get("target");

        boolean plural = data.has("plural") && data.get("plural").getAsBoolean();
        boolean optional = data.has("optional") && data.get("optional").getAsBoolean();
        String description = get("description");


        switch (id) {
            case "txt" -> line.addComponent(Text.literal("\"" + name + "\"").setStyle(GUIStyles.TEXT.getStyle()));
            case "num" -> line.addComponent(Text.literal(name).setStyle(GUIStyles.NUMBER.getStyle()));
            case "var" -> line.addComponent(Text.literal(name).setStyle(GUIStyles.VARIABLE.getStyle()),
                                Tooltip.of(Text.literal(scope.toUpperCase()).setStyle(GUIStyles.valueOf(scope.toUpperCase()).getStyle())));
            case "bl_tag" -> line.addComponent(Text.literal(option).setStyle(GUIStyles.BLOCK_TAG.getStyle()),
                                Tooltip.of(Text.literal(tag).setStyle(GUIStyles.COMMENT.getStyle())));
            case "g_val" -> line.addComponent(Text.literal(type + (Objects.equals(target, "Default") ? "" : (" [" + target.charAt(0) + "]"))).setStyle(GUIStyles.GAME_VALUE.getStyle()),
                                Tooltip.of(Text.literal(target).setStyle(GUIStyles.COMMENT.getStyle())));
            case "item" -> {
                try {
                    String nbtString = get("item");
                    ItemStack item = ItemStack.fromNbt(NbtHelper.fromNbtProviderString(nbtString));
                    MutableText tooltipTxt = Text.empty();
                    var nbt = item.getNbt();
                    if (nbt != null && nbt.contains("display")) {
                        var display = nbt.getCompound("display");
                        if (display.contains("Name")) {
                            String itemName = display.getString("Name");
                            try {
                                tooltipTxt.append(Text.Serialization.fromJson(itemName));
                            } catch (Exception e) {
                                tooltipTxt.append(Text.of(itemName));
                            }
                        }
                    }

                    tooltipTxt.append(Text.literal("\n" + item.getItem().toString()).setStyle(GUIStyles.COMMENT.getStyle()));
                    line.addComponent(Text.literal(item.toString()).setStyle(GUIStyles.ITEM.getStyle()), Tooltip.of(tooltipTxt));
                } catch (CommandSyntaxException e) {
                    e.printStackTrace();
                }
            }
            case "pn_el" -> line.addComponent(Text.literal(name).setStyle(GUIStyles.PARAMETER.getStyle()),
                    Tooltip.of(
                            Text.literal((optional ? "*" : "") + type + (plural ? "(s)" : "")).setStyle(GUIStyles.valueOf(type.toUpperCase()).getStyle())
                                    .append(Text.literal(" - " + description).setStyle(Style.EMPTY.withColor(Color.white.hashCode())))
                    ));
            case "part" -> {
                String particle = get("particle");
                ArrayList<Text> tooltip = new ArrayList<>();
                tooltip.add(Text.literal("Particle\n").setStyle(GUIStyles.PARTICLE.getStyle()));

                JsonObject cluster = data.get("cluster").getAsJsonObject();
                int amount = cluster.get("amount").getAsInt();
                double horizontal = cluster.get("horizontal").getAsDouble();
                double vertical = cluster.get("vertical").getAsDouble();

                line.addComponent(Text.literal(name).setStyle(GUIStyles.NUMBER.getStyle()));


                JsonObject pData = data.get("data").getAsJsonObject();

                HashMap<String, String> pDataMap = pData.keySet().stream().collect(
                        Collectors.toMap(key -> key, key -> pData.get(key).toString(), (a, b) -> b, HashMap::new));

                pDataMap.put("Amount", String.valueOf(amount));
                pDataMap.put("Spread", horizontal + " " + vertical);

                if (pDataMap.containsKey("x")) {
                    String x = pDataMap.get("x");
                    String y = pDataMap.get("y");
                    String z = pDataMap.get("z");

                    pDataMap.remove("x");
                    pDataMap.remove("y");
                    pDataMap.remove("z");

                    pDataMap.put("Motion", String.format("%s %s %s", x, y, z));
                }

                int color;
                if (pDataMap.containsKey("rgb")) {
                    color = Integer.parseInt(pDataMap.get("rgb"));

                    pDataMap.remove("rgb");

                    pDataMap.put("Color", "#"+Integer.toHexString(color));
                }
                if (pDataMap.containsKey("motionVariation"))
                    pDataMap.put("Motion Variation", pDataMap.get("motionVariation") + "%");
                if (pDataMap.containsKey("colorVariation"))
                    pDataMap.put("Color Variation", pDataMap.get("colorVariation") + "%");
                if (pDataMap.containsKey("sizeVariation"))
                    pDataMap.put("Size Variation", pDataMap.get("sizeVariation") + "%");
                if (pDataMap.containsKey("roll"))
                    pDataMap.put("Roll", pDataMap.get("roll"));
                if (pDataMap.containsKey("size"))
                    pDataMap.put("Size", pDataMap.get("size"));
                if (pDataMap.containsKey("material"))
                    pDataMap.put("Material", pDataMap.get("material"));


                boolean newline = false;
                String[] keys = new String[] {"Amount", "Spread", "n", "Motion", "Motion Variation", "Material", "Roll", "Color", "Color Variation", "Size", "Size Variation"};
                for (String key : keys) {
                    if (key.equals("n")) newline = true;
                    if (!pDataMap.containsKey(key)) continue;

                    if (newline) tooltip.add(Text.of("\n"));
                    newline = false;

                    String value = pDataMap.get(key);
                    Style style = GUIStyles.DEFAULT.getStyle();

                    tooltip.add(Text.literal(key+": ").setStyle(GUIStyles.UNSAVED.getStyle()));
                    tooltip.add(Text.literal(value).setStyle(style));
                    tooltip.add(Text.of("\n"));
                }
                tooltip.remove(tooltip.size()-1);

                MutableText tooltipText = Text.empty();
                tooltip.forEach(tooltipText::append);
                line.addComponent(Text.literal(particle).setStyle(GUIStyles.PARTICLE.getStyle()),
                        Tooltip.of(tooltipText));
            }
            case "comp" -> line.addComponent(Text.literal(name).setStyle(GUIStyles.COMPONENT.getStyle()));
            case "vec" -> line.addComponent(Text.literal(
                    String.format("<%.2f, %.2f, %.2f>",
                            data.get("x").getAsDouble(),
                            data.get("y").getAsDouble(),
                            data.get("z").getAsDouble()))
                    .setStyle(GUIStyles.VECTOR.getStyle()));
            case "loc" -> {
                boolean isBlock = data.get("isBlock").getAsBoolean();
                if (!isBlock) {
                    JsonObject loc = data.get("loc").getAsJsonObject();
                    double x = loc.get("x").getAsDouble();
                    double y = loc.get("y").getAsDouble();
                    double z = loc.get("z").getAsDouble();
                    double pitch = loc.get("pitch").getAsDouble();
                    double yaw = loc.get("yaw").getAsDouble();
                    line.addComponent(Text.literal("[").setStyle(GUIStyles.LOCATION.getStyle()))
                            .addComponent(Text.literal(String.format(
                                    "%.2f, %.2f, %.2f, %.2f, %.2f",
                                    x, y, z, pitch, yaw)).setStyle(GUIStyles.DEFAULT.getStyle()))
                            .addComponent(Text.literal("]").setStyle(GUIStyles.LOCATION.getStyle()));
                    return;
                }
                line.addComponent(Text.literal(data.toString()).setStyle(GUIStyles.NUMBER.getStyle()));
            }
            case "snd" -> line.addComponent(Text.literal(get("sound")).setStyle(GUIStyles.SOUND.getStyle()),
                    Tooltip.of(
                            Text.literal("Pitch: ").setStyle(GUIStyles.UNSAVED.getStyle())
                                    .append(Text.literal(String.valueOf(data.get("pitch").getAsDouble())).setStyle(GUIStyles.DEFAULT.getStyle()))
                                    .append(Text.literal("\nVolume: ").setStyle(GUIStyles.UNSAVED.getStyle()))
                                    .append(Text.literal(String.valueOf(data.get("vol").getAsDouble())).setStyle(GUIStyles.DEFAULT.getStyle()))));
            case "pot" -> line.addComponent(Text.literal(get("pot")).setStyle(GUIStyles.POTION.getStyle()),
                    Tooltip.of(
                            Text.literal("Amplifier: ").setStyle(GUIStyles.UNSAVED.getStyle())
                                    .append(Text.literal(String.valueOf(data.get("amp").getAsInt())).setStyle(GUIStyles.DEFAULT.getStyle()))
                                    .append(Text.literal("\nDuration: ").setStyle(GUIStyles.UNSAVED.getStyle()))
                                    .append(Text.literal(formatTicks(data.get("dur").getAsInt())).setStyle(GUIStyles.DEFAULT.getStyle())))
            );
            default -> line.addComponent(Text.literal(id));
        }
    }


    String formatTicks(int ticks) {
        if (ticks < 20) return ticks+" ticks";
        int seconds = ticks / 20;
        int minutes = seconds / 60;
        if (minutes > 0) {
            seconds -= minutes * 60;
            return minutes + "m" + seconds +"s";
        }
        return seconds + "s";
    }
}
