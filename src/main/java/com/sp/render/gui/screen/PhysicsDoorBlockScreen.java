package com.sp.render.gui.screen;

import com.sp.block.entity.custom.PhysicsDoorBlockEntity;
import com.sp.networking.C2S.UpdatePhysicsDoorPacket;
import com.sp.networking.CustomPayloads;
import com.sp.render.SelectionHandler;
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

public class PhysicsDoorBlockScreen extends Screen {
    private final PhysicsDoorBlockEntity physicsDoorBlockEntity;
    private static final Text CORNER1_TEXT = Text.literal("Corner 1:");
    private static final Text CORNER2_TEXT = Text.literal("Corner 2:");
    private static final Text DIRECTION_TEXT = Text.literal("Direction:");
    private static final Text NUM_OF_BLOCKS_TEXT = Text.literal("Number Of Blocks:");
    private static final Text SHOW_SELECTION_TEXT = Text.literal("Selection:");
    private static final Text PLAY_SOUND_TEXT = Text.literal("Play Sound:");
    private static final Text X_TEXT = Text.literal("X: ");
    private static final Text Y_TEXT = Text.literal("Y: ");
    private static final Text Z_TEXT = Text.literal("Z: ");
    private int centerWidth;
    private int centerHeight;

    private TextFieldWidget corner1XInput;
    private TextFieldWidget corner1YInput;
    private TextFieldWidget corner1ZInput;

    private TextFieldWidget corner2XInput;
    private TextFieldWidget corner2YInput;
    private TextFieldWidget corner2ZInput;

    private TextFieldWidget numOfBlocksInput;

    private Direction prevDirection;
    private boolean prevShowSelection;
    private boolean prevPlaySound;
    private int prevSpeed;


    public PhysicsDoorBlockScreen(PhysicsDoorBlockEntity physicsDoorBlockEntity) {
        super(Text.literal("Physics Door Block"));
        this.physicsDoorBlockEntity = physicsDoorBlockEntity;
    }

    @Override
    protected void init() {
        this.centerWidth = this.width / 2;
        this.centerHeight = this.height / 2;

        //Save the values in case canceled (It's the only one set when the button is pushed)
        this.prevDirection = this.physicsDoorBlockEntity.getMovementDirection();
        this.prevShowSelection = this.physicsDoorBlockEntity.shouldShowSelection();
        this.prevPlaySound = this.physicsDoorBlockEntity.shouldPlaySound();
        this.prevSpeed = this.physicsDoorBlockEntity.getSpeed();

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Save"), button -> this.done()).dimensions(this.centerWidth - 150, this.centerHeight + 60, 80, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Cancel"), button -> this.cancel()).dimensions(this.centerWidth - 50, this.centerHeight + 60, 80, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Select"),
                button -> this.select()
        ).dimensions(this.centerWidth + 50, this.centerHeight + 60, 80, 20).build());

        this.addDrawableChild(
                CyclingButtonWidget.<Direction>builder(direction -> Text.literal(direction.getName().toUpperCase()))
                        .values(Direction.values())
                        .initially(this.physicsDoorBlockEntity.getMovementDirection())
                        .omitKeyText()
                        .build(this.centerWidth - 150, this.centerHeight, 50, 20, Text.literal(""), (button, value) -> this.physicsDoorBlockEntity.setMovementDirection(value))
        );

        this.addDrawableChild(
                CyclingButtonWidget.onOffBuilder(this.physicsDoorBlockEntity.shouldShowSelection())
                        .omitKeyText()
                        .build(this.centerWidth + 90, this.centerHeight, 40, 20, SHOW_SELECTION_TEXT, (button, showSelection) -> this.physicsDoorBlockEntity.setShowSelection(showSelection))
        );

        this.addDrawableChild(
                CyclingButtonWidget.onOffBuilder(this.physicsDoorBlockEntity.shouldPlaySound())
                        .omitKeyText()
                        .build(this.centerWidth + 90, this.centerHeight + 30, 40, 20, PLAY_SOUND_TEXT, (button, playSound) -> this.physicsDoorBlockEntity.setPlaySound(playSound))
        );

        this.addDrawableChild(new SliderWidget(this.centerWidth - 150, this.centerHeight + 30, 100, 20, ScreenTexts.EMPTY, 1.0) {
            {
                this.value = PhysicsDoorBlockScreen.this.physicsDoorBlockEntity.getSpeed() / 20.0;
                this.updateMessage();
            }

            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal("Speed: " + PhysicsDoorBlockScreen.this.physicsDoorBlockEntity.getSpeed()));
            }

            @Override
            protected void applyValue() {
                int speed = MathHelper.floor(MathHelper.clampedLerp(1.0, 20.0, this.value));
                PhysicsDoorBlockScreen.this.physicsDoorBlockEntity.setSpeed(speed);
            }
        });


        BlockPos corner1 = physicsDoorBlockEntity.getCorner1();
        this.corner1XInput = new TextFieldWidget(this.textRenderer, this.centerWidth - 150, this.centerHeight - 80, 80, 20, Text.literal("Corner 1 X"));
        this.corner1XInput.setText(Integer.toString(corner1.getX()));
        this.addSelectableChild(this.corner1XInput);
        this.corner1YInput = new TextFieldWidget(this.textRenderer, this.centerWidth - 40, this.centerHeight - 80, 80, 20, Text.literal("Corner 1 Y"));
        this.corner1YInput.setText(Integer.toString(corner1.getY()));
        this.addSelectableChild(this.corner1YInput);
        this.corner1ZInput = new TextFieldWidget(this.textRenderer, this.centerWidth + 70, this.centerHeight - 80, 80, 20, Text.literal("Corner 1 Z"));
        this.corner1ZInput.setText(Integer.toString(corner1.getZ()));
        this.addSelectableChild(this.corner1ZInput);

        BlockPos corner2 = physicsDoorBlockEntity.getCorner2();
        this.corner2XInput = new TextFieldWidget(this.textRenderer, this.centerWidth - 150, this.height /2 - 40, 80, 20, Text.literal("Corner 2 X"));
        this.corner2XInput.setText(Integer.toString(corner2.getX()));
        this.addSelectableChild(this.corner2XInput);
        this.corner2YInput = new TextFieldWidget(this.textRenderer, this.centerWidth - 40, this.centerHeight - 40, 80, 20, Text.literal("Corner 2 Y"));
        this.corner2YInput.setText(Integer.toString(corner2.getY()));
        this.addSelectableChild(this.corner2YInput);
        this.corner2ZInput = new TextFieldWidget(this.textRenderer, this.centerWidth + 70, this.centerHeight - 40, 80, 20, Text.literal("Corner 2 Z"));
        this.corner2ZInput.setText(Integer.toString(corner2.getZ()));
        this.addSelectableChild(this.corner2ZInput);

        this.numOfBlocksInput = new TextFieldWidget(this.textRenderer, this.centerWidth - 80, this.centerHeight, 80, 20, Text.literal("Number of Blocks"));
        this.numOfBlocksInput.setText(Integer.toString(this.physicsDoorBlockEntity.getNumOfBlocks()));
        this.addSelectableChild(this.numOfBlocksInput);
    }

    @Override
    public void tick() {

    }

    private void updatePhysicsBlock() {
        BlockPos corner1 = new BlockPos(parseInt(this.corner1XInput.getText()), parseInt(this.corner1YInput.getText()), parseInt(this.corner1ZInput.getText()));
        BlockPos corner2 = new BlockPos(parseInt(this.corner2XInput.getText()), parseInt(this.corner2YInput.getText()), parseInt(this.corner2ZInput.getText()));

        ClientPlayNetworking.send(new CustomPayloads.UpdatePhysicsDoorBlock(
                this.physicsDoorBlockEntity.getPos(),
                corner1,
                corner2,
                this.physicsDoorBlockEntity.getMovementDirection(),
                parseInt(this.numOfBlocksInput.getText()),
                this.physicsDoorBlockEntity.getSpeed(),
                this.physicsDoorBlockEntity.shouldShowSelection(),
                this.physicsDoorBlockEntity.shouldPlaySound()
        ));
    }

    private int parseInt(String integer) {
        try {
            return Integer.parseInt(integer);
        } catch (NumberFormatException var3) {
            return 0;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.centerWidth, 10, 16777215);

        context.drawTextWithShadow(this.textRenderer, CORNER1_TEXT, this.centerWidth - 150, this.centerHeight - 80 - this.textRenderer.fontHeight, 10526880);
        this.drawLetters(context);

        this.corner1XInput.render(context, mouseX, mouseY, delta);
        this.corner1YInput.render(context, mouseX, mouseY, delta);
        this.corner1ZInput.render(context, mouseX, mouseY, delta);


        context.drawTextWithShadow(this.textRenderer, CORNER2_TEXT, this.centerWidth - 150, this.centerHeight - 40 - this.textRenderer.fontHeight, 10526880);
        this.corner2XInput.render(context, mouseX, mouseY, delta);
        this.corner2YInput.render(context, mouseX, mouseY, delta);
        this.corner2ZInput.render(context, mouseX, mouseY, delta);

        context.drawTextWithShadow(this.textRenderer, DIRECTION_TEXT, this.centerWidth - 150, this.centerHeight - this.textRenderer.fontHeight, 10526880);
        context.drawTextWithShadow(this.textRenderer, NUM_OF_BLOCKS_TEXT, this.centerWidth - 80, this.centerHeight - this.textRenderer.fontHeight, 10526880);
        this.numOfBlocksInput.render(context, mouseX, mouseY, delta);

        context.drawTextWithShadow(this.textRenderer, SHOW_SELECTION_TEXT, this.centerWidth + 90, this.centerHeight - this.textRenderer.fontHeight, 10526880);

        context.drawTextWithShadow(this.textRenderer, PLAY_SOUND_TEXT, this.centerWidth + 90, this.centerHeight + 30 - this.textRenderer.fontHeight, 10526880);
    }

    private void drawLetters(DrawContext context) {
        for (int i = 0; i <= 1; i++) {
            context.drawTextWithShadow(
                    this.textRenderer,
                    X_TEXT,
                    this.centerWidth - 150 - this.textRenderer.getWidth(X_TEXT),
                    this.centerHeight - 70 + (i*40) - this.textRenderer.fontHeight / 2,
                    RenderUtil.getRgb(255, 0, 0)
            );

            context.drawTextWithShadow(
                    this.textRenderer,
                    Y_TEXT,
                    this.centerWidth - 40 - this.textRenderer.getWidth(Y_TEXT),
                    this.centerHeight - 70 + (i*40) - this.textRenderer.fontHeight / 2,
                    RenderUtil.getRgb(0, 255, 0)
            );

            context.drawTextWithShadow(
                    this.textRenderer,
                    Z_TEXT,
                    this.centerWidth + 70 - this.textRenderer.getWidth(Z_TEXT),
                    this.centerHeight - 70 + (i*40) - this.textRenderer.fontHeight / 2,
                    RenderUtil.getRgb(0, 0, 255)
            );
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderInGameBackground(context);
    }

    private void done() {
        if (this.client != null) {
            this.updatePhysicsBlock();
            this.client.setScreen(null);
        }
    }

    private void cancel() {
        if (this.client != null) {
            this.physicsDoorBlockEntity.setMovementDirection(prevDirection);
            this.physicsDoorBlockEntity.setShowSelection(prevShowSelection);
            this.physicsDoorBlockEntity.setPlaySound(prevPlaySound);
            this.physicsDoorBlockEntity.setSpeed(prevSpeed);
            this.client.setScreen(null);
        }
    }

    private void select() {
        if(client == null) return;

        SelectionHandler.startSelection((corner1, corner2) -> {
            this.corner1XInput.setText(Integer.toString(corner1.getX()));
            this.corner1YInput.setText(Integer.toString(corner1.getY()));
            this.corner1ZInput.setText(Integer.toString(corner1.getZ()));

            this.corner2XInput.setText(Integer.toString(corner2.getX()));
            this.corner2YInput.setText(Integer.toString(corner2.getY()));
            this.corner2ZInput.setText(Integer.toString(corner2.getZ()));
            this.updatePhysicsBlock();
            this.physicsDoorBlockEntity.setSettingSelection(false);
        }, () -> this.physicsDoorBlockEntity.setSettingSelection(false));

        this.physicsDoorBlockEntity.setSettingSelection(true);
        this.client.setScreen(null);
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
