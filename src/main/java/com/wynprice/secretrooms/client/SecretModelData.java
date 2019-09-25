package com.wynprice.secretrooms.client;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.ModelProperty;

import java.util.List;
import java.util.Set;

public class SecretModelData {
    public static final ModelProperty<BlockState> SRM_BLOCKSTATE = new ModelProperty<>();

    public static final ModelProperty<BlockState> MODEL_MAP_STATE = new ModelProperty<>();

    public static final ModelProperty<Set<Direction>> GHOST_BLOCK_NEIGHBOURS = new ModelProperty<>();
}
