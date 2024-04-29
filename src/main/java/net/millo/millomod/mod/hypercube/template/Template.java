package net.millo.millomod.mod.hypercube.template;

import com.google.gson.Gson;
import net.millo.millomod.mod.features.impl.util.Tracker;
import net.minecraft.util.math.Vec3d;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.zip.GZIPInputStream;

public class Template {

    public Vec3d startPos;
    public String b64Code;
    public int plotId;
    public ArrayList<TemplateBlock> blocks;

    public static Template parseItem(String codeTemplateData) {
        CodeTemplateData templateData = new Gson().fromJson(codeTemplateData, CodeTemplateData.class);
        return parse(templateData.code);
    }

    public static Template parse(String data) {
        try {
            byte[] decompressed = decompress(Base64.getDecoder().decode(data));

            Template template = new Gson().fromJson(new String(decompressed), Template.class);
            template.b64Code = data;
            template.plotId = Tracker.getPlot().getPlotId();
            return template;
        } catch (IOException e) {
            return null;
        }
    }


    private static byte[] decompress(byte[] compressedData) throws IOException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(compressedData);
             GZIPInputStream gis = new GZIPInputStream(bis);
             ByteArrayOutputStream bos = new ByteArrayOutputStream())
        {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = gis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }

            return bos.toByteArray();
        }
    }


    public String getFileName() {
        return getName().replaceAll("[<>:\"/\\|?*]", "") + "." + getMethodName();
    }
    public String getMethodName() {
        return blocks.get(0).block;
    }
    public String getName() {
        String name = blocks.get(0).data;
        if (name == null) name = blocks.get(0).action;
        if (name.isEmpty()) name = blocks.get(0).block +" " + startPos;
        return name;
    }
}
