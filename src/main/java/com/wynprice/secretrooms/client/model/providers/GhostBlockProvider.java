package com.wynprice.secretrooms.client.model.providers;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GhostBlockProvider extends SecretQuadProvider {

    public static final GhostBlockProvider GHOST_BLOCK = new GhostBlockProvider();
    @Override
    public List<BakedQuad> render(@Nullable BlockState mirrorState, @Nonnull IBakedModel model, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        List<BakedQuad> quadList = super.render(mirrorState, model, side, rand, extraData);

        //As ghost blocks don't have a collision, we need to render the quads on both sides
        if(side == null) {
            this.addSide(mirrorState, null, model, rand, extraData, quadList);
            for (Direction direction : Direction.values()) {
                this.addSide(mirrorState, direction, model, rand, extraData, quadList);
            }
        }

        return quadList;
    }

    private void addSide(BlockState mirrorState, Direction direction, IBakedModel model, Random random, IModelData data, List<BakedQuad> quadList) {
        for (BakedQuad quad : model.getQuads(mirrorState, direction, random, data)) {
            int size = quad.getFormat().getIntegerSize();
            int uOff = quad.getFormat().getUvOffsetById(0) / 4;
            int[] aint = quad.getVertexData();
            int[] outData = Arrays.copyOf(aint, aint.length);

            //Swap the vertex positions around so the quad faces inwards.
            //Vertex swaps:
            //0 <-> 3
            //1 <-> 2

            //The U part of the UV data is also swapped around in the same fashion (0 <-> 3 & 1 <-> 2)
            //This makes it so the quad on the inside is mapped up with the quad on the outside

            outData[0] = aint[3*size];
            outData[1] = aint[3*size+1];
            outData[2] = aint[3*size+2];
            outData[3*size] = aint[0];
            outData[3*size+1] = aint[1];
            outData[3*size+2] = aint[2];

            outData[size] = aint[2*size];
            outData[size+1] = aint[2*size+1];
            outData[size+2] = aint[2*size+2];
            outData[2*size] = aint[size];
            outData[2*size+1] = aint[size+1];
            outData[2*size+2] = aint[size+2];



            outData[uOff] = aint[3*size + uOff];
            outData[3*size + uOff] = aint[uOff];
            outData[size + uOff] = aint[2*size + uOff];
            outData[2*size + uOff] = aint[size + uOff];

            quadList.add(new BakedQuad(outData, quad.getTintIndex(), quad.getFace(), quad.getSprite(), quad.shouldApplyDiffuseLighting(), quad.getFormat()));
        }
    }
}

