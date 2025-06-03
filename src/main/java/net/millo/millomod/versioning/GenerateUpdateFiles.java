package net.millo.millomod.versioning;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.millo.millomod.mod.features.impl.global.patchnotes.PatchNoteRegistry;
import net.millo.millomod.mod.features.impl.global.patchnotes.PatchNotes;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GenerateUpdateFiles {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Please provide mod version as first argument");
            System.exit(1);
        }

        String modVersion = args[0];
        System.out.println("Generating update files for version: " + modVersion);

        VersionInfo versionInfo = new VersionInfo(modVersion);
        writeJsonToFile(versionInfo, "version/version.json");

        PatchNotes patch = PatchNoteRegistry.get(modVersion);
        if (patch == null) patch = PatchNoteRegistry.getPatchNotes().getLast();

        if (patch != null) {
            writeJsonToFile(patch.serialize(), "version/patch.json");
        } else {
            System.out.println("No patch notes found for version: " + modVersion);
        }
    }

    private static void writeJsonToFile(Object obj, String filename) {
        File file = new File(filename);
        File dir = file.getParentFile();
        if (dir != null && !dir.exists()) {
            dir.mkdirs();
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(obj, writer);
            System.out.println("Written " + filename);
        } catch (IOException e) {
            System.err.println("Error writing " + filename + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    record VersionInfo(String version) {
    }

}
