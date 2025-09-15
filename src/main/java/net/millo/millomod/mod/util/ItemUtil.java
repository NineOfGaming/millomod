package net.millo.millomod.mod.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.millo.millomod.MilloMod;
import net.minecraft.command.argument.ItemStringReader;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.util.packrat.PackratParser;
import net.minecraft.world.World;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.RegistryOps;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public enum ItemUtil {

    NOT_ARROW("{count: 1, Slot: 0b, components: {\"minecraft:attribute_modifiers\": {show_in_tooltip: 0b, modifiers: []}, \"minecraft:lore\": ['{\"extra\":[{\"bold\":false,\"color\":\"gray\",\"italic\":false,\"obfuscated\":false,\"strikethrough\":false,\"text\":\"Click on a Condition block with this\",\"underlined\":false}],\"text\":\"\"}', '{\"extra\":[{\"bold\":false,\"color\":\"gray\",\"italic\":false,\"obfuscated\":false,\"strikethrough\":false,\"text\":\"to switch between \\'IF\\' and \\'IF NOT\\'.\",\"underlined\":false}],\"text\":\"\"}'], \"minecraft:custom_name\": '{\"color\":\"#ffd47f\",\"italic\":false,\"text\":\"NOT Arrow\"}', \"minecraft:custom_model_data\": 0, \"minecraft:hide_additional_tooltip\": {}, \"minecraft:damage\": 0}, id: \"minecraft:spectral_arrow\"}")

    ;

    private ItemStack item = null;
    private final String itemNbt;

    public static ItemStack fromNbt(String data) {
        try {
            if (data == null) return ItemStack.EMPTY;
            World world = MilloMod.MC.world;
            if (world == null) return ItemStack.EMPTY;

            String s = data.trim();
            if (s.isEmpty()) return ItemStack.EMPTY;

            if (s.startsWith("{")) {
                // Full SNBT -> NbtElement -> ItemStack via CODEC (1.21+)
                PackratParser<NbtElement> parser = SnbtParsing.createParser(NbtOps.INSTANCE);
                NbtElement element = parser.parse(new StringReader(s));
                var ops = RegistryOps.of(NbtOps.INSTANCE, world.getRegistryManager());
                var result = ItemStack.CODEC.parse(ops, element);
                return result.result().orElse(ItemStack.EMPTY);
            } else {
                // Item string -> Item + components
                ItemStringReader reader = new ItemStringReader(world.getRegistryManager());
                ItemStringReader.ItemResult res = reader.consume(new StringReader(s));

                ItemStack stack = new ItemStack(res.item(), 1);

                var changes = res.components();
                if (changes != null) {
                    stack.applyChanges(changes);
                }

                return stack;
            }

        } catch (CommandSyntaxException e) {
            String input = e.getInput();
            int cursor = e.getCursor();
            if (input != null && cursor >= 0) {
                StringBuilder caret = new StringBuilder();
                for (int i = 0; i < cursor; i++) caret.append(' ');
                caret.append('^');
                System.out.println("Error parsing item NBT: " + e.getMessage() + "\n" + input + "\n" + caret);
            } else {
                System.out.println("Error parsing item NBT: " + e.getMessage());
            }
            return ItemStack.EMPTY;
        } catch (Exception e) {
            System.out.println("Unexpected error parsing item NBT: " + e.getMessage());
            return ItemStack.EMPTY;
        }
    }

    public static Map<String, Object> getItemTags(ItemStack item) {

        NbtCompound pbv = getPBV(item);
        if (pbv == null) return null;

        HashMap<String, Object> result = new HashMap<>();

        pbv.getKeys().forEach(key -> {
            Object value = pbv.get(key);
            result.put(key, value);
        });

        return result;

    }

    public static NbtCompound getPBV(ItemStack stack) {
        ComponentMap components = stack.getComponents();
        if (components == null) return null;

        NbtComponent custom_data = components.get(DataComponentTypes.CUSTOM_DATA);
        if (custom_data == null) return null;

        NbtCompound nbt = custom_data.copyNbt();
        return nbt.getCompound("PublicBukkitValues").orElse(null);
    }


    public static String getPBVString(ItemStack stack, String key) {
        NbtCompound pbv = getPBV(stack);
        if (pbv == null) return null;

        if (!pbv.contains(key)) return null;

        return pbv.getString(key).orElse(null);
    }


    public ItemStack getItem() {
        if (item != null) return item;

        World world = MilloMod.MC.world;
        if (world == null) return ItemStack.EMPTY;

        item = fromNbt(itemNbt);
        return item;
    }

    ItemUtil(String itemNbt) {
        this.itemNbt = itemNbt;
    }


}
