package net.millo.millomod.mod.features.impl.cache;

@SuppressWarnings("unused")
public enum Block {
    CALL_FUNC("call(<data>)"),
    GAME_ACTION("game_action");


    public final String name;

    Block(String name) {
        this.name = name;
    }
}
