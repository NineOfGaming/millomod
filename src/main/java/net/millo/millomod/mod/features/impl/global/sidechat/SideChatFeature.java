package net.millo.millomod.mod.features.impl.global.sidechat;

import net.millo.millomod.mod.features.Feature;
import net.millo.millomod.mod.features.FeatureHandler;
import net.millo.millomod.system.Config;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;

public class SideChatFeature extends Feature {


    private static String filter;
    private static boolean simpleFilter;

    private static final ArrayList<ChatRule> rules = new ArrayList<>();

    public SideChatFeature() {
        rules.add(new ChatRule("side_chat.private_chat", "\\[\\w+ → \\w+\\].*"));
        rules.add(new ChatRule("side_chat.mod_chat", "\\[MOD\\].*"));
        rules.add(new ChatRule("side_chat.admin_chat", "\\[ADMIN\\].*"));
        rules.add(new ChatRule("side_chat.support_chat", "\\[SUPPORT\\].*"));
    }

    @Override
    public String getKey() {
        return "side_chat";
    }

    @Override
    public void defaultConfig(Config config) {
        super.defaultConfig(config);
        config.set("side_chat.filter", "");
        config.set("side_chat.simple_filter", true);

        rules.forEach(rule -> config.set(rule.getKey(), true));
    }

    @Override
    public void onConfigUpdate(Config config) {
        super.onConfigUpdate(config);
        filter = config.get("side_chat.filter");
        simpleFilter = config.get("side_chat.simple_filter");

        rules.forEach(rule -> rule.update(config));
    }

    public static boolean fitsFilter(Text text) {
        String str = text.getString();
        if (!FeatureHandler.getFeature("side_chat").isEnabled()) return false;

        for (ChatRule rule : rules) if (rule.match(str)) return true;

        if (str.startsWith("! Incoming Report")) return true;

        // Custom filter
        if (filter.isEmpty()) return false;
        if (simpleFilter) {
            String[] searchText = filter.split("[ ,]");
            return (Arrays.stream(searchText).anyMatch(str::contains));
        }
        return Pattern.compile(filter).matcher(str).find();


//        if (!text.getSiblings().isEmpty()) {
//            Text sibling = text.getSiblings().get(0);
//            TextColor color = sibling.getStyle().getColor();
//            if (color != null) {
//                String hexCode = color.getHexCode();
//            }
//
////            if ("[SUPPORT] ".equals(sibling.getString()) &&
////               .equals("#557FD4")) {
////                return true;
////            }
//
//        }

        // SUPPORT #557FD4
        //


    }

}
