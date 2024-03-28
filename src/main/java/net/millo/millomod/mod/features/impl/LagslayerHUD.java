package net.millo.millomod.mod.features.impl;

import net.millo.millomod.config.Config;
import net.millo.millomod.mod.features.Feature;
import net.millo.millomod.mod.features.IRenderable;
import net.millo.millomod.mod.features.PacketListener;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.util.math.ColorHelper;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LagslayerHUD extends Feature implements IRenderable {

    private float cpuUsage = 0f;
    private float renderedCpuUsage = 0f;
    private float renderedAlpha = 0f;
    private Date updateTime = new Date();

    private int x, y;
    private final Pattern lsRegex = Pattern.compile("^CPU Usage: \\[▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮] \\((\\d+\\.\\d+)%\\)$");


    @Override
    public void defaultConfig(Config config) {
        super.defaultConfig(config);
    }

    @Override
    public void onConfigUpdate(Config config) {
        super.onConfigUpdate(config);
        updatePosFromConfig(config);
    }

    @Override
    public String getKey() {
        return "lagslayer";
    }

    @PacketListener
    public boolean onActionbar(OverlayMessageS2CPacket packet) {
        if (!enabled) return false;

        // CPU Usage: [▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮] (00.00%)
        String content = packet.getMessage().getString();
        Matcher matcher = lsRegex.matcher(content);
        if (matcher.find()) {
            String cpuUsageStr = matcher.group(1);
            try {
                cpuUsage = Float.parseFloat(cpuUsageStr);
                updateTime = new Date();
            } catch (NumberFormatException e) {
                System.out.println("Error parsing CPU usage: " + e.getMessage());
                enabled = false;
            }
            return true;
        }
        return false;
    }



    private void renderDonut(DrawContext context, float delta, float centerX, float centerY, float innerRadius, float outerRadius, int segments, int color) {
        renderDonut(context, delta, centerX, centerY, innerRadius, outerRadius, segments, color, 0, 1);
    }

    private void renderDonut(DrawContext context, float delta, float centerX, float centerY, float innerRadius, float outerRadius, int segments, int color, float start, float end) {
        Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();
        float alpha = (float) ColorHelper.Argb.getAlpha(color) / 255.0F;
        float red = (float) ColorHelper.Argb.getRed(color) / 255.0F;
        float green = (float) ColorHelper.Argb.getGreen(color) / 255.0F;
        float blue = (float) ColorHelper.Argb.getBlue(color) / 255.0F;

        VertexConsumer vertexConsumer = context.getVertexConsumers().getBuffer(RenderLayer.getGui());

        double startAngle = start * 2 * Math.PI;
        double endAngle = end * 2 * Math.PI;
        double increment = (endAngle - startAngle) / segments;

        for (int i = 0; i < segments; i++) {
            float angle1 = (float) (i * increment);
            float angle2 = (float) ((i + 1) * increment);

            float x1_inner = centerX + (float) (Math.cos(angle1) * innerRadius);
            float y1_inner = centerY + (float) (Math.sin(angle1) * innerRadius);

            float x2_inner = centerX + (float) (Math.cos(angle2) * innerRadius);
            float y2_inner = centerY + (float) (Math.sin(angle2) * innerRadius);

            float x1_outer = centerX + (float) (Math.cos(angle1) * outerRadius);
            float y1_outer = centerY + (float) (Math.sin(angle1) * outerRadius);

            float x2_outer = centerX + (float) (Math.cos(angle2) * outerRadius);
            float y2_outer = centerY + (float) (Math.sin(angle2) * outerRadius);

            vertexConsumer.vertex(matrix4f, x1_outer, y1_outer, 0).color(red, green, blue, alpha).next();
            vertexConsumer.vertex(matrix4f, x2_outer, y2_outer, 0).color(red, green, blue, alpha).next();
            vertexConsumer.vertex(matrix4f, x2_inner, y2_inner, 0).color(red, green, blue, alpha).next();
            vertexConsumer.vertex(matrix4f, x1_inner, y1_inner, 0).color(red, green, blue, alpha).next();

            vertexConsumer.vertex(matrix4f, x1_inner, y1_inner, 0).color(red, green, blue, alpha).next();
            vertexConsumer.vertex(matrix4f, x2_inner, y2_inner, 0).color(red, green, blue, alpha).next();
            vertexConsumer.vertex(matrix4f, x2_outer, y2_outer, 0).color(red, green, blue, alpha).next();
            vertexConsumer.vertex(matrix4f, x1_outer, y1_outer, 0).color(red, green, blue, alpha).next();
        }

        context.draw();
    }
    private float lerp(float a, float b, float f)  {
        return a * (1f - f) + (b * f);
    }
    @Override
    public void render(DrawContext context, float delta, TextRenderer textRenderer) {
        if (!enabled) return;

        double time = (new Date().getTime() - updateTime.getTime()) / 1000d;
        float alpha = (float) Math.max(Math.min(1, 5 - time), 0);
        renderedCpuUsage = lerp(renderedCpuUsage, cpuUsage, delta * 0.15f);
        renderedAlpha = lerp(renderedAlpha, alpha, delta * 0.15f);

        int x = getX() + 10;
        int y = getY() + 10;

        int backgroundColor = new Color(0.25f, 0.25f, 0.25f, renderedAlpha).hashCode();
        Color color = Color.getHSBColor(Math.max(0f, (100f - renderedCpuUsage) / 360f), 1f, 1f);
        int foregroundColor = new Color(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f, renderedAlpha).hashCode();

        if (alpha > 0) {
            renderDonut(context, delta, x, y, 4.9f, 9.9f, 20, backgroundColor);
            renderDonut(context, delta, x, y, 5, 10, 20, foregroundColor, 0.75f - (Math.round(renderedCpuUsage) / 100f), 0.75f);

            context.drawText(textRenderer, Math.round(renderedCpuUsage * 100d) / 100d + "%", x + 12, y - 4, Color.white.hashCode(), true);
        }
    }

    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    public int getWidth() {
        return 54;
    }
    public int getHeight() {
        return 20;
    }

}
