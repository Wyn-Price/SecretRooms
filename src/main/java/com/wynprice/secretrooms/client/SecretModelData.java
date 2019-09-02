package com.wynprice.secretrooms.client;

import com.wynprice.secretrooms.client.model.providers.SecretQuadProvider;
import net.minecraft.block.BlockState;
import net.minecraftforge.client.model.data.ModelProperty;

public class SecretModelData {
    public static final ModelProperty<BlockState> SRM_MIRRORSTATE = new ModelProperty<>();
    public static final ModelProperty<SecretQuadProvider> SRM_RENDER = new ModelProperty<>();
}
