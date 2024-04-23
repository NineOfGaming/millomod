package net.millo.millomod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.millo.millomod.system.Config;
import net.millo.millomod.mod.commands.CommandHandler;
import net.millo.millomod.mod.features.FeatureHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MilloMod implements ClientModInitializer {

    public static final String MOD_ID = "millomod";
    public static final String MOD_VERSION = "1.4.2";
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

        // TODO: Add CS:GO to savestates (quick menu)
        // TODO: Side chat
        // TODO: reference book arguments in GUI
        // TODO: /search
        // TODO: socket server, for item-give, maybe more

        // TODO: Paywall


    }



}
