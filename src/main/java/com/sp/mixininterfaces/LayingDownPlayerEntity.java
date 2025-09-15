package com.sp.mixininterfaces;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Optional;

public interface LayingDownPlayerEntity {

    boolean isLayingDown();
    void setLayingDown(boolean layingDown);

    Optional<BlockPos> getLayingDownPos();
    void setLayingDownPos(BlockPos layingDownPos);

    Direction getLayingDownDirection();

    void getUp();
}
