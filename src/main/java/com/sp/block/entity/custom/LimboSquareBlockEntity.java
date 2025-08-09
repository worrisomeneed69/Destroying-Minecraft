package com.sp.block.entity.custom;

import com.sp.block.entity.ModBlockEntities;
import com.sp.render.gui.screen.LimboSquareBlockScreen;
import com.sp.render.gui.screen.PhysicsDoorBlockScreen;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.ComponentMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class LimboSquareBlockEntity extends BlockEntity {
    private final Vector3f color;
    private float size;
    private float height;

    public LimboSquareBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LIMBO_SQUARE_BE, pos, state);
        this.color = new Vector3f();
        this.height = 0.0f;
        this.size = 1.0f;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putFloat("color_r", this.color.x);
        nbt.putFloat("color_g", this.color.y);
        nbt.putFloat("color_b", this.color.z);

        nbt.putFloat("size", this.size);
        nbt.putFloat("height", this.height);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.color.x = nbt.getFloat("color_r");
        this.color.y = nbt.getFloat("color_g");
        this.color.z = nbt.getFloat("color_b");

        this.size = nbt.getFloat("size");
        this.height = nbt.getFloat("height");
    }

    public boolean openScreen(PlayerEntity player) {
        if (!player.isCreativeLevelTwoOp()) {
            return false;
        } else {
            if (player.getEntityWorld().isClient) {
                MinecraftClient.getInstance().setScreen(new LimboSquareBlockScreen(this));
            }

            return true;
        }
    }

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.createComponentlessNbt(registryLookup);
    }


    public Vector3f getColor() {
        return this.color;
    }

    public void setColor(Vector3f color) {
        this.color.set(color);
    }

    public float getSize() {
        return this.size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public float getHeight() {
        return this.height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}
