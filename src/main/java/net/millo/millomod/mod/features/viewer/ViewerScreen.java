package net.millo.millomod.mod.features.viewer;

import net.millo.millomod.mod.features.gui.GUI;
import net.millo.millomod.mod.features.gui.GUIStyles;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.*;
import net.minecraft.text.Text;

import java.awt.*;

public class ViewerScreen extends GUI {
    protected int backgroundWidth = 230;
    protected int backgroundHeight = 219;
    private final int padding = 40;
    private float fadeIn = 0f;

    public ViewerScreen() {
        super(Text.literal("Yeah this"));
    }


    protected void init() {
        super.init();

        TextWidget text = new TextWidget(padding, padding, backgroundWidth, backgroundHeight,
                Text.literal("Empty").setStyle(GUIStyles.COMMENT.getStyle()), textRenderer);
        text.alignCenter();
        addDrawableChild(text);
//
//        TextElement text2 = new TextElement(padding, padding, backgroundWidth, backgroundHeight,
//                Text.literal("Whats up chuckle nuts!"), textRenderer);
//        addElement(text2);

//        button1 = ButtonWidget.builder(Text.literal("Button A"), button -> {
//                    System.out.println("You clicked button1!");
//                })
//                .dimensions(width / 2 - 205, 30, 200, 20)
//                .tooltip(Tooltip.of(Text.literal("Tooltip of button1")))
//                .build();
//        button2 = ButtonWidget.builder(Text.literal("Button B"), button -> {
//                    System.out.println("You clicked button2!");
//                })
//                .dimensions(width / 2 + 5, 50, 200, 20)
//                .tooltip(Tooltip.of(Text.literal("Tooltip of button2")))
//                .build();



//        ScrollableTextWidget sText = new ElementListWidget<TextWidget>(padding+1, padding+1,backgroundWidth-2, backgroundHeight-2,
//                Text.literal("bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla blab la bla bla bla bla bla bla bla bla bla bla bla bla bla bl abl abl abl abl abla bla blab lab l abl abl ab la blablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablablabla"),
//                textRenderer);
//        addDrawableChild(sText);

    }


    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {

        int x = padding;
        int y = padding+(int)((1f - fadeIn) * 10f);
        int color = new Color(0, 0, 0, (int)(fadeIn * 150)).hashCode();
//        int borderColor = new Color(51, 51, 51, (int)(fadeIn * 255)).hashCode();

        context.getMatrices().push();
        context.getMatrices().translate(0f, 0f, -20f);
        context.fill(x, y, x+backgroundWidth, y+backgroundHeight, 0, color);
//        context.drawBorder(x, y, backgroundWidth, backgroundHeight, borderColor);
        context.getMatrices().pop();
    }


    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        fadeIn = Math.min(fadeIn + delta * 0.2f, 1f);
        if (fadeIn >= 1f) {
            super.render(context, mouseX, mouseY, delta);
        }
    }


}
