package com.wynprice.secretrooms.client.compat;

import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import com.wynprice.secretrooms.server.items.TrueVisionGoggles;
import com.wynprice.secretrooms.server.items.TrueVisionGogglesClientHandler;
import mcjty.theoneprobe.api.IBlockDisplayOverride;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.apiimpl.ProbeConfig;
import mcjty.theoneprobe.apiimpl.providers.DefaultProbeInfoProvider;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Optional;

public class SecretRoomsTOPProvider implements IBlockDisplayOverride {
    @Override
    public boolean overrideStandardInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world,
                                        BlockState blockState, IProbeHitData data)
    {
        if(!TrueVisionGogglesClientHandler.isWearingGoggles(player) && blockState.getBlock() instanceof SecretBaseBlock) {
            Optional<BlockState> fakeStateOpt = SecretBaseBlock.getMirrorState(world, data.getPos());
            if(fakeStateOpt.isPresent()) {
                BlockState fakeState = fakeStateOpt.get();
                DefaultProbeInfoProvider.showStandardBlockInfo(
                        new ProbeConfig(), mode, probeInfo,
                        fakeState, fakeState.getBlock(), world,
                        data.getPos(), player,
                        new IProbeHitData() {
                            @Override
                            public BlockPos getPos() {
                                return data.getPos();
                            }

                            @Override
                            public Vector3d getHitVec() {
                                return data.getHitVec();
                            }

                            @Override
                            public Direction getSideHit() {
                                return data.getSideHit();
                            }

                            @Nonnull
                            @Override
                            public ItemStack getPickBlock() {
                                RayTraceResult result = world.rayTraceBlocks(new RayTraceContext(
                                        player.getPositionVec(),
                                        player.getPositionVec().add(player.getLookVec().x * 5, player.getLookVec().y * 5, player.getLookVec().z * 5),
                                        RayTraceContext.BlockMode.COLLIDER,
                                        RayTraceContext.FluidMode.NONE,
                                        player
                                ));
                                return fakeState.getPickBlock(result, world, data.getPos(), player);
                            }
                        }
                );
                return true;
            }
        }
        return false;
    }
}
