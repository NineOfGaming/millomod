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
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static MinecraftClient MC = MinecraftClient.getInstance();


    @Override
    public void onInitializeClient() {
        Config.getInstance();

        FeatureHandler.load();
        ClientCommandRegistrationCallback.EVENT.register(CommandHandler::load);

        KeybindHandler.load();

        // TODO: tag display
        // TODO: Side chat
        // TODO: /dfgive
        // TODO: reference book arguments in GUI
        // TODO: /search
    }

}
