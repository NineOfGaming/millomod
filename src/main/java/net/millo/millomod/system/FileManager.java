package net.millo.millomod.system;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.millo.millomod.MilloMod;
import net.millo.millomod.mod.hypercube.template.Template;
import net.minecraft.util.math.Vec3d;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FileManager {

    public static File of(String filename) {
        Path path = FabricLoader.getInstance().getConfigDir();
        return path.resolve(filename).toFile();
    }

    public static Path getTemplatePath() {
        Path path = MilloMod.MC.runDirectory.toPath().resolve(MilloMod.MOD_ID).resolve("cache");
        path.toFile().mkdirs();
        return path;
    }
    public static Path getTemplatePlotPath(int plotId) {
        Path path = getTemplatePath().resolve(String.valueOf(plotId));
        path.toFile().mkdirs();
        return path;
    }

    public static File getTemplateFile(int plotId, String templateName) {
        Path path = getTemplatePlotPath(plotId);
        return path.resolve(templateName).toFile();
    }

    // fix writing first
    public static void writeTemplate(Template template) {
        JsonObject inf = new JsonObject();
        var pos = new JsonArray();
        pos.add(template.startPos.x);
        pos.add(template.startPos.y);
        pos.add(template.startPos.z);
        inf.add("pos", pos);
        inf.addProperty("code", template.b64Code);

        try {
            File file = getTemplateFile(template.plotId, template.getFileName());
            Files.deleteIfExists(file.toPath());
            Files.createFile(file.toPath());
            if (!file.exists()) file.createNewFile();
            Files.write(file.toPath(), inf.toString().getBytes(), StandardOpenOption.WRITE);
        } catch (IOException e) {
            System.out.println("Couldn't save template: " + e);
        }
    }

    public static void deleteTemplateFile(int plotId, String fileName) {
        File file = getTemplateFile(plotId, fileName);
        try {
            Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
            System.out.println("Failed to delete: " + e);
        }
    }

    public static boolean isPlotCached(String plotId) {
        return Files.exists(getTemplatePath().resolve(plotId));
    }


    static class ReadTemplate {
        public String code;
        public ArrayList<Double> pos;
    }
    public static Template readTemplate(int plotId, String templateName) {
        try {
            String readData = Files.readString(getTemplateFile(plotId, templateName).toPath());
            ReadTemplate inf = new Gson().fromJson(readData, ReadTemplate.class);
            Template template = Template.parse(inf.code);
            if (template == null) return null;
            template.startPos = new Vec3d(inf.pos.get(0), inf.pos.get(1), inf.pos.get(2));
            return template;
        } catch (IOException e) {
            return null;
        }
    }

    public static List<String> getTemplatesFromPlot(int plotId) {
        return Arrays.stream(Objects.requireNonNull(
                getTemplatePlotPath(plotId).toFile().listFiles()))
                .map(File::getName)
                .collect(Collectors.toList());
    }



}
