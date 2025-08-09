package com.sp.render.gui.screen;

import com.sp.block.entity.custom.LimboSquareBlockEntity;
import com.sp.block.entity.custom.PhysicsDoorBlockEntity;
import com.sp.networking.C2S.UpdateLimboSquareBlockPacket;
import com.sp.networking.C2S.UpdatePhysicsDoorPacket;
import com.sp.render.SelectionHandler;
import com.sp.render.gui.screen.widget.HSVColorWidget;
import com.sp.util.RenderUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector3f;

public class LimboSquareBlockScreen extends Screen {
    private final LimboSquareBlockEntity limboSquareBlockEntity;
    private static final Text RED = Text.literal("Red:");
    private static final Text GREEN = Text.literal("Green:");
    private static final Text BLUE = Text.literal("Blue:");
    private static final Text HEIGHT_TEXT = Text.literal("Height:");
    private static final Text SIZE_TEXT = Text.literal("Size:");
    private int centerWidth;
    private int centerHeight;

    private TextFieldWidget heightInupt;
    private TextFieldWidget sizeInput;

    private HSVColorWidget colorInput;

    private Vector3f prevColor;
    private float prevSize;
    private float prevHeight;


    public LimboSquareBlockScreen(LimboSquareBlockEntity limboSquareBlockEntity) {
        super(Text.literal("Limbo Square Block"));
        this.limboSquareBlockEntity = limboSquareBlockEntity;
    }

    @Override
    protected void init() {
        this.centerWidth = this.width / 2;
        this.centerHeight = this.height / 2;

        //Save the values in case canceled (It's the only one set when the button is pushed)
        this.prevColor = new Vector3f(this.limboSquareBlockEntity.getColor());
        this.prevSize = this.limboSquareBlockEntity.getSize();
        this.prevHeight = this.limboSquareBlockEntity.getHeight();

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Save"), button -> this.done()).dimensions(this.centerWidth - 400, this.centerHeight + 190, 80, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Cancel"), button -> this.cancel()).dimensions(this.centerWidth - 300, this.centerHeight + 190, 80, 20).build());

        Vector3f color = this.limboSquareBlockEntity.getColor();

//        281, 255
        this.colorInput = new HSVColorWidget(this.centerWidth - 400, this.centerHeight + 30, 0.45f, color.x, color.y, color.z, (red, green, blue) -> {
            color.x = red;
            color.y = green;
            color.z = blue;
        });
        this.addDrawableChild(this.colorInput);


        this.heightInupt = new TextFieldWidget(this.textRenderer, this.centerWidth - 400, this.centerHeight + 160, 80, 20, Text.literal("Size"));
        this.heightInupt.setText(Float.toString(this.limboSquareBlockEntity.getHeight()));
        this.addSelectableChild(this.heightInupt);
        this.sizeInput = new TextFieldWidget(this.textRenderer, this.centerWidth - 300, this.centerHeight + 160, 80, 20, Text.literal("Height"));
        this.sizeInput.setText(Float.toString(this.limboSquareBlockEntity.getSize()));
        this.addSelectableChild(this.sizeInput);
    }

    private void updateLimboBlock() {
        ClientPlayNetworking.send(new UpdateLimboSquareBlockPacket.UpdateLimboSquareBlockPayload(
                this.limboSquareBlockEntity.getPos(),
                new Vector3f(this.limboSquareBlockEntity.getColor()),
                parseFloat(this.sizeInput.getText()),
                parseFloat(this.heightInupt.getText())
        ));
    }

    private float parseFloat(String integer) {
        try {
            return Float.parseFloat(integer);
        } catch (NumberFormatException var3) {
            return 0;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawTextWithShadow(this.textRenderer, this.title, this.width/2 - 410, this.height/2 + 8, 16777215);

        this.heightInupt.render(context, mouseX, mouseY, delta);
        this.sizeInput.render(context, mouseX, mouseY, delta);

        context.drawTextWithShadow(this.textRenderer, HEIGHT_TEXT, this.centerWidth - 400, this.centerHeight - this.textRenderer.fontHeight + 160, 10526880);

        context.drawTextWithShadow(this.textRenderer, SIZE_TEXT, this.centerWidth - 300, this.centerHeight - this.textRenderer.fontHeight + 160, 10526880);
    }

    private void drawLetters(DrawContext context) {
        context.drawTextWithShadow(
                this.textRenderer,
                RED,
                this.centerWidth - 150 - this.textRenderer.getWidth(RED),
                this.centerHeight - 90 - this.textRenderer.fontHeight / 2,
                RenderUtil.getRgb(255, 0, 0)
        );

        context.drawTextWithShadow(
                this.textRenderer,
                GREEN,
                this.centerWidth - 150 - this.textRenderer.getWidth(GREEN),
                this.centerHeight - 50 - this.textRenderer.fontHeight / 2,
                RenderUtil.getRgb(0, 255, 0)
        );

        context.drawTextWithShadow(
                this.textRenderer,
                BLUE,
                this.centerWidth - 150 - this.textRenderer.getWidth(BLUE),
                this.centerHeight - 10 - this.textRenderer.fontHeight / 2,
                RenderUtil.getRgb(0, 0, 255)
        );
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        int xSize = 100;
        int ySize = 100;
        int offsetX = -310;
        int offsetY = 120;

        context.fillGradient(
                this.width/2 - xSize  + offsetX,
                this.height/2 - ySize + offsetY,
                this.width/2 + xSize   + offsetX,
                this.height/2 + ySize  + offsetY,
                -1072689136,
                -804253680
        );

        context.fill(
                this.width/2 + xSize  + offsetX- 2,
                this.height/2 - ySize + offsetY + 2,
                this.width/2 + xSize  + offsetX+ 2,
                this.height/2 + ySize + offsetY - 2,
                -1072689136
        );
        context.fill(
                this.width/2 - xSize  + offsetX- 2,
                this.height/2 - ySize + offsetY - 2,
                this.width/2 + xSize  + offsetX+ 2,
                this.height/2 - ySize + offsetY + 2,
                -1072689136
        );
        context.fill(
                this.width/2 - xSize  + offsetX- 2,
                this.height/2 + ySize + offsetY - 2,
                this.width/2 + xSize  + offsetX+ 2,
                this.height/2 + ySize + offsetY + 2,
                -1072689136
        );
        context.fill(
                this.width/2 - xSize  + offsetX- 2,
                this.height/2 - ySize + offsetY + 2,
                this.width/2 - xSize  + offsetX+ 2,
                this.height/2 + ySize + offsetY - 2,
                -1072689136
        );
    }

    private void done() {
        if (this.client != null) {
            this.updateLimboBlock();
            this.client.setScreen(null);
        }
    }

    private void cancel() {
        if (this.client != null) {
            this.limboSquareBlockEntity.setColor(prevColor);
            this.limboSquareBlockEntity.setSize(prevSize);
            this.limboSquareBlockEntity.setHeight(prevHeight);
            this.client.setScreen(null);
        }
    }

    @Override
    public void close() {
        super.close();
        this.cancel();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
