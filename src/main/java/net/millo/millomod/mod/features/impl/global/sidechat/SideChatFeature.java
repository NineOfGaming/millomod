package net.millo.millomod.mod.features.impl.global.sidechat;

import net.millo.millomod.mod.features.Feature;
import net.millo.millomod.mod.features.FeatureHandler;
import net.millo.millomod.system.Config;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;

public class SideChatFeature extends Feature {


    private static String filter;
    private static boolean simpleFilter;
    private static boolean privateChat, supportChat, modChat, adminChat;

    @Override
    public String getKey() {
        return "side_chat";
    }

    @Override
    public void defaultConfig(Config config) {
        super.defaultConfig(config);
        config.set("side_chat.filter", "");
        config.set("side_chat.simple_filter", true);

        config.set("side_chat.private_chat", true);
        config.set("side_chat.support_chat", true);
        config.set("side_chat.mod_chat", true);
        config.set("side_chat.admin_chat", true);
    }

    @Override
    public void onConfigUpdate(Config config) {
        super.onConfigUpdate(config);
        filter = Config.getInstance().get("side_chat.filter");
        simpleFilter = Config.getInstance().get("side_chat.simple_filter");

        privateChat = Config.getInstance().get("side_chat.private_chat");
        supportChat = Config.getInstance().get("side_chat.support_chat");
        modChat = Config.getInstance().get("side_chat.mod_chat");
        adminChat = Config.getInstance().get("side_chat.admin_chat");
    }

    public static boolean fitsFilter(Text text) {
        String str = text.getString();
        if (!FeatureHandler.getFeature("side_chat").isEnabled()) return false;


        if (privateChat && str.matches("\\[\\w+ → \\w+\\].*")) return true;
        if (modChat && str.startsWith("[MOD] ")) return true;
        if (adminChat && str.startsWith("[ADMIN] ")) return true;

        if (supportChat) {
            Text sibling = text.getSiblings().get(0);
            var color = sibling.getStyle().getColor();
            if ("[SUPPORT] ".equals(sibling.getString()) &&
                    color != null && color.getHexCode().equals("#557FD4")) {
                return true;
            }
        }



        // Custom filter
        if (filter.isEmpty()) return false;
        if (simpleFilter) {
            String[] searchText = filter.split("[ ,]");
            return (Arrays.stream(searchText).anyMatch(str::contains));
        }
        return Pattern.compile(filter).matcher(str).find();
    }

}
