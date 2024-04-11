package net.millo.millomod.mod.features.impl.cache;

import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.millo.millomod.mod.util.gui.GUIStyles;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.Text;

import java.util.Objects;

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
            case "txt" -> line.addComponent(Text.literal(name).setStyle(GUIStyles.TEXT.getStyle()));
            case "num" -> line.addComponent(Text.literal(name).setStyle(GUIStyles.NUMBER.getStyle()));
            case "var" -> line.addComponent(Text.literal(name).setStyle(GUIStyles.VARIABLE.getStyle()),
                                Tooltip.of(Text.literal(scope.toUpperCase()).setStyle(GUIStyles.valueOf(scope.toUpperCase()).getStyle())));
            case "bl_tag" -> line.addComponent(Text.literal(option).setStyle(GUIStyles.BLOCK_TAG.getStyle()),
                                Tooltip.of(Text.literal(tag).setStyle(GUIStyles.COMMENT.getStyle())));
            case "g_val" -> line.addComponent(Text.literal((Objects.equals(target, "Default") ? "" : target.charAt(0) + ":") + type).setStyle(GUIStyles.GAME_VALUE.getStyle()),
                                Tooltip.of(Text.literal(target).setStyle(GUIStyles.COMMENT.getStyle())));
            case "item" -> {
                try {
                    String nbt = get("item");
                    ItemStack item = ItemStack.fromNbt(NbtHelper.fromNbtProviderString(nbt));
                    line.addComponent(Text.literal(item.toString()).setStyle(GUIStyles.ITEM.getStyle()));
                } catch (CommandSyntaxException e) {
                    e.printStackTrace();
                }
            }
            case "pn_el" -> {
                line.addComponent(Text.literal(name).setStyle(GUIStyles.PARAMETER.getStyle()),
                        Tooltip.of(
                                Text.literal(type + (plural ? "(s)" : "")).setStyle(GUIStyles.valueOf(type.toUpperCase()).getStyle())
                                        .append(Text.of(" - " + description))
                        ));
            }
            default -> line.addComponent(Text.literal(id));
        }


    }

}
