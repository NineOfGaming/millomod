package net.millo.millomod.mod.features.impl.global.sidechat;

import net.millo.millomod.system.Config;

public class ChatRule {

    private final String regex, key;
    private boolean enabled = false;
    public ChatRule(String key, String regex) {
        this.regex = regex;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void update(Config config) {
        enabled = config.get("side_chat.private_chat");
    }

    public boolean match(String str) {
        if (!enabled) return false;
        return str.matches(regex);
    }
}
