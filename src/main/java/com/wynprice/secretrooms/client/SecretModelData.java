package com.wynprice.secretrooms.client;

import com.wynprice.secretrooms.client.model.providers.SecretQuadProvider;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.ModelProperty;

import java.util.List;
import java.util.function.Supplier;

public class SecretModelData {
    //Thi is used until https://github.com/MinecraftForge/MinecraftForge/pull/6115 is merged.
    public static final ModelProperty<Supplier<Boolean>> SRM_DO_RENDER = new ModelProperty<>();

    public static final ModelProperty<BlockState> SRM_BASESTATE = new ModelProperty<>();
    public static final ModelProperty<SecretQuadProvider> SRM_RENDER = new ModelProperty<>();

    public static final ModelProperty<List<Direction>> SRM_ONE_WAY_GLASS_SIDES = new ModelProperty<>();

    public static final ModelProperty<BlockState> MODEL_MAP_STATE = new ModelProperty<>();

}
