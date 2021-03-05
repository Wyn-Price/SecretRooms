package com.wynprice.secretrooms.client.model;

import com.google.common.math.DoubleMath;
import com.wynprice.secretrooms.client.SecretModelData;
import com.wynprice.secretrooms.server.utils.ModelDataUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class SecretMappedModel extends SecretBlockModel {
    public SecretMappedModel(IBakedModel model) {
        super(model);
    }

    private static final Supplier<BlockRendererDispatcher> DISPATCHER = () -> Minecraft.getInstance().getBlockRendererDispatcher();

    @Override
    public List<BakedQuad> render(@Nonnull BlockState mirrorState, @Nonnull BlockState baseState, @Nonnull IBakedModel model, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        Optional<BlockState> blockState = ModelDataUtils.getData(extraData, SecretModelData.MODEL_MAP_STATE);
        if(!blockState.isPresent()) {
            return super.render(mirrorState, baseState, model, side, rand, extraData);
        }
        List<BakedQuad> modelQuads = DISPATCHER.get().getModelForState(blockState.get()).getQuads(blockState.get(), side, rand, extraData);
        List<BakedQuad> outQuads = new ArrayList<>();


        List<BakedQuad> allQuads = new ArrayList<>(model.getQuads(mirrorState, null, rand, extraData));
        for (Direction value : Direction.values()) {
            allQuads.addAll(model.getQuads(mirrorState, value, rand, extraData));
        }


        for (BakedQuad modelQuad : modelQuads) {
            AxisAlignedBB bb = this.createQuadBorder(modelQuad);
            for (BakedQuad texture : ((Iterable<BakedQuad>) () -> allQuads.stream().filter(q -> q.getFace() == modelQuad.getFace()).iterator())) {
                outQuads.add(this.resizeQuad(texture, bb));
            }
        }

        return outQuads;
    }

    private BakedQuad resizeQuad(BakedQuad texture, AxisAlignedBB modelRange) {
        List<Vector3d> vertices = this.getVertexPositions(texture);
        List<Vector3d> clamped = this.clampVertexPositions(modelRange, vertices);

        int size = DefaultVertexFormats.BLOCK.getIntegerSize();
        int[] aint = new int[texture.getVertexData().length];
        System.arraycopy(texture.getVertexData(), 0, aint, 0, aint.length);

        for (int v = 0; v < clamped.size(); v++) {
            Vector3d vec = clamped.get(v);

            aint[v*size] = Float.floatToIntBits((float) vec.x);
            aint[v*size+1] = Float.floatToIntBits((float) vec.y);
            aint[v*size+2] = Float.floatToIntBits((float) vec.z);

        }

        this.resetUVPositions(aint, size, DefaultVertexFormats.BLOCK.getOffset(0) / 4, vertices, clamped);

        return new BakedQuad(aint, texture.getTintIndex(), texture.getFace(), texture.getSprite(), texture.applyDiffuseLighting());
    }

    private void resetUVPositions(int[] aint, int size, int uOff, List<Vector3d> vertices, List<Vector3d> clampedVertices) {
        int[] mappedU = new int[]{ 3, 2, 1, 0 };
        int[] mappedV = new int[]{ 1, 0, 3, 2 };
        for (int v = 0; v < vertices.size(); v++) {
            if(vertices.get(v).equals(clampedVertices.get(v))) {
                continue;
            }
            Vector3d clamped = clampedVertices.get(v);
            Vector3d from = vertices.get(v);

            Vector3d toU = vertices.get(mappedU[v]);
            Vector3d toV = vertices.get(mappedV[v]);

            Direction.Axis uAxis = getDifferential(from, toU);
            Direction.Axis vAxis = getDifferential(from, toV);

            double uInterpolated = dist(clamped, from, uAxis) / dist(toU, from, uAxis);
            double vInterpolated = dist(clamped, from, vAxis) / dist(toV, from, vAxis);

            aint[v*size + uOff] = Float.floatToIntBits((float) this.interpolate(Float.intBitsToFloat(aint[v*size + uOff]), Float.intBitsToFloat(aint[mappedU[v]*size + uOff]), uInterpolated));
            aint[v*size + uOff+1] = Float.floatToIntBits((float) this.interpolate(Float.intBitsToFloat(aint[v*size + uOff+1]), Float.intBitsToFloat(aint[mappedV[v]*size + uOff+1]), vInterpolated));
        }
    }

    private Direction.Axis getDifferential(Vector3d v1, Vector3d v2) {
        if(!DoubleMath.fuzzyEquals(v1.x, v2.x, 1.0E-7D)) {
            return Direction.Axis.X;
        }
        if(!DoubleMath.fuzzyEquals(v1.y, v2.y, 1.0E-7D)) {
            return Direction.Axis.Y;
        }
        if(!DoubleMath.fuzzyEquals(v1.z, v2.z, 1.0E-7D)) {
            return Direction.Axis.Z;
        }
        return null;
    }

    private double interpolate(double from, double to, double alpha) {
        return from + (to - from) * alpha;
    }

    private double dist(Vector3d v1, Vector3d v2, Direction.Axis axis) {
        if(axis == null) {
            return 1D;
        }
        switch (axis) {
            case X: return Math.abs(v1.x - v2.x);
            case Y: return Math.abs(v1.y - v2.y);
            case Z: return Math.abs(v1.z - v2.z);
        }
        return 1D; //Cannot be 0, as otherwise we divide by 0
    }

    private List<Vector3d> clampVertexPositions(AxisAlignedBB modelQuadRange, List<Vector3d> vertices) {
        List<Vector3d> out = new ArrayList<>();
        for (Vector3d vertex : vertices) {
            out.add(new Vector3d(
                MathHelper.clamp(vertex.x, modelQuadRange.minX, modelQuadRange.maxX),
                MathHelper.clamp(vertex.y, modelQuadRange.minY, modelQuadRange.maxY),
                MathHelper.clamp(vertex.z, modelQuadRange.minZ, modelQuadRange.maxZ)
            ));
        }
        return out;
    }

    private List<Vector3d> getVertexPositions(BakedQuad quad) {
        int size = DefaultVertexFormats.BLOCK.getIntegerSize();
        int[] aint = quad.getVertexData();

        List<Vector3d> out = new ArrayList<>();
        for (int v = 0; v < 4; v++) {
            out.add(new Vector3d(Float.intBitsToFloat(aint[v*size]), Float.intBitsToFloat(aint[v*size+1]), Float.intBitsToFloat(aint[v*size+2])));
        }
        return out;
    }

    private AxisAlignedBB createQuadBorder(BakedQuad quad) {
        Vector3d min = new Vector3d(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
        Vector3d max = new Vector3d(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);

        for (Vector3d vertexPos : this.getVertexPositions(quad)) {
            setVec(min, vertexPos, Math::min);
            setVec(max, vertexPos, Math::max);
        }

        return new AxisAlignedBB(min.x, min.y, min.z, max.x, max.y, max.z);
    }

    private void setVec(Vector3d toSet, Vector3d vertex, BiFunction<Double, Double, Double> cons) {
        toSet.x = cons.apply(toSet.x, vertex.x);
        toSet.y = cons.apply(toSet.y, vertex.y);
        toSet.z = cons.apply(toSet.z, vertex.z);
    }

}
