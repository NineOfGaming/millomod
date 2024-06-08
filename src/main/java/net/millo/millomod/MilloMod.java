package net.millo.millomod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.millo.millomod.system.Config;
import net.millo.millomod.mod.commands.CommandHandler;
import net.millo.millomod.mod.features.FeatureHandler;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MilloMod implements ClientModInitializer {

    public static final String MOD_ID = "millomod";
    public static final String MOD_VERSION = "1.4.4";

    @SuppressWarnings("unused")
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static MinecraftClient MC = MinecraftClient.getInstance();


    @Override
    public void onInitializeClient() {
        Config.getInstance();

        FeatureHandler.load();
        ClientCommandRegistrationCallback.EVENT.register(CommandHandler::load);

        KeybindHandler.load();
        SoundHandler.load();


        // Idle clicker game while bored


        // TODO: Able to get number/text/vars whilst in a menu (maybe send a command)

        // TODO: Auto %var() brackets
        // TODO: Add a `ticks -> 0m 0s` option for numbers (endersaltz)
        // TODO: Side chat
        // TODO: reference book arguments in GUI
        // TODO: /search
        // TODO: socket server, for item-give, maybe more

        // TODO: Paywall

        // TODO: in menu search press enter to r/c item if only one is highlighted

        // TODO: a way to set plot spawn uniquely for build


    }



}
