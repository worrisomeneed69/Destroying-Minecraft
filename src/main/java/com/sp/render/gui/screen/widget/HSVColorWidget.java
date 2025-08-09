package com.sp.render.gui.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sp.render.gui.HSVColorTextureManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector3f;

public class HSVColorWidget extends ClickableWidget {
    private final float scale;
    private boolean pickHue;

    private float hue;
    private float saturation;
    private float value;
    private double colorPickerX;
    private double colorPickerY;

    private final Vector3f outputColor;

    private final SetColor setColorFunction;


    public HSVColorWidget(int x, int y, float scale, float currentRed, float currentGreen, float currentBlue, SetColor setColorFunction) {
        super(x, y, (int) (281 * scale), (int) (255 * scale), Text.literal(""));
        this.scale = scale;
        this.outputColor = new Vector3f(currentRed, currentGreen, currentBlue);
        this.setColorFunction = setColorFunction;
        this.setInitialValues();
        this.updateTextures();
    }

    private void setInitialValues() {
        float max = Math.max(Math.max(outputColor.x, outputColor.y), outputColor.z);
        float min = Math.min(Math.min(outputColor.x, outputColor.y), outputColor.z);
        float delta = max - min;

        if (max == outputColor.x) {
            this.hue = 60 * (((outputColor.y - outputColor.z) / delta) % 6) / 360;
        } else if (max == outputColor.y) {
            this.hue = 60 * (((outputColor.z - outputColor.x) / delta) + 2) / 360;
        } else if (max == outputColor.z) {
            this.hue = 60 * (((outputColor.x - outputColor.y) / delta) + 4) / 360;
        }


        this.saturation = max == 0 ? 0 : delta / max;
        this.value = max;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        context.getMatrices().push();
        context.getMatrices().translate(this.getX(), this.getY(), 0.0f);
        context.getMatrices().scale(this.scale, this.scale, this.scale);

        context.drawTexture(HSVColorTextureManager.getHsvTextureIdentifier(), 0, 0, 255, 255, 255, 255);
        context.drawTexture(HSVColorTextureManager.getHueTextureIdentifier(), 260, 0, 20, 255, 20, 255);

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        int width = textRenderer.getWidth("o");
        int xPos = (int) (this.saturation * 255);
        int yPos = (int) ((1.0 - this.value) * 255);
        context.drawText(
                textRenderer,
                "o",
                xPos - width/2,
                yPos -  textRenderer.fontHeight/2,
                Colors.WHITE,
                true
        );

        context.fill(258, (int) ((1.0 - this.hue) * 255 - 2), 282, (int) ((1.0 - this.hue) * 255 + 2), Colors.WHITE);

        context.getMatrices().pop();
    }

    @Override
    protected boolean clicked(double mouseX, double mouseY) {
        double mouseXPos = mouseX - this.getX();
        if (mouseXPos > 255 * this.scale && mouseXPos < 260 * this.scale) {
            return false;
        }
        return super.clicked(mouseX, mouseY);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        double mouseXPos = mouseX - this.getX();
        double mouseYPos = mouseY - this.getY();

        this.pickHue = mouseXPos >= 260 * this.scale;

        if (!this.pickHue) {
            this.colorPickerX = Math.clamp(mouseXPos / this.scale, 0.0, 255.0);
            this.colorPickerY = Math.clamp(mouseYPos / this.scale, 0.0, 255.0);
        }

        this.updateColors((float) (mouseYPos / this.scale));
        super.onClick(mouseX, mouseY);
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        double mouseXPos = (mouseX - this.getX()) / this.scale;
        double mouseYPos = (mouseY - this.getY()) / this.scale;

        if (!this.pickHue) {
            this.colorPickerX = Math.clamp(mouseXPos, 0.0, 255.0);
            this.colorPickerY = Math.clamp(mouseYPos, 0.0, 255.0);
        }

        this.updateColors((float) mouseYPos);
        super.onDrag(mouseX, mouseY, deltaX, deltaY);
    }

    private void updateColors(float mouseYPos) {
        if (this.pickHue) {
            this.hue = Math.clamp((255.0f - mouseYPos) / 255.0f, 0.0f, 0.999f);
            this.updateTextures();
        } else {
            this.value = (float) Math.clamp((255 - this.colorPickerY) / 255, 0.0f, 1.0f);
            this.saturation = (float) Math.clamp(this.colorPickerX / 254, 0.0f, 1.0f);
        }
        int color = MathHelper.hsvToArgb(this.hue, this.saturation, this.value, 255);

        outputColor.x = ColorHelper.Argb.getRed(color) / 255.0f;
        outputColor.y = ColorHelper.Argb.getGreen(color) / 255.0f;
        outputColor.z = ColorHelper.Argb.getBlue(color) / 255.0f;

        this.setColorFunction.setColor(outputColor.x, outputColor.y, outputColor.z);
    }

    private void updateTextures() {
        NativeImage hsvImage = HSVColorTextureManager.getHsvImage();
        NativeImage hueImage = HSVColorTextureManager.getHueImage();


        if (hsvImage == null || hueImage == null) return;

        for (int x = 0; x < 255; x++) {
            for (int y = 0; y < 255; y++) {
                float value = (float) (255 - y) / 255;
                float saturation = (float) (x) / 254;

                int color = MathHelper.hsvToArgb(this.hue, saturation, value, 255);

                int red = ColorHelper.Argb.getRed(color);
                int blue = ColorHelper.Argb.getBlue(color);
                int green = ColorHelper.Argb.getGreen(color);

                // Alpha Blue Green Red ??????
                hsvImage.setColor(x, y, 255 << 24 | blue << 16 | green << 8 | red);

                //Hue texture
                if (x < 20) {
                    color = MathHelper.hsvToArgb(value - 0.01f, 1.0f, 1.0f, 0);

                    red = ColorHelper.Argb.getRed(color);
                    blue = ColorHelper.Argb.getBlue(color);
                    green = ColorHelper.Argb.getGreen(color);
                    hueImage.setColor(x, y, 255 << 24 | blue << 16 | green << 8 | red);
                }
            }
        }

        HSVColorTextureManager.upload();
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

    public interface SetColor {
        void setColor(float red, float green, float blue);
    }
}
