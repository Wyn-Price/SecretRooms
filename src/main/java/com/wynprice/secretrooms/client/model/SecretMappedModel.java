package com.wynprice.secretrooms.client.model;

import com.google.common.math.DoubleMath;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.wynprice.secretrooms.client.SecretModelData;
import com.wynprice.secretrooms.server.utils.ModelDataUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class SecretMappedModel extends SecretBlockModel {

    private final Map<BlockState, AABB> stateAreaCache = new HashMap<>();

    public SecretMappedModel(BakedModel model) {
        super(model);
    }

    private static final Supplier<BlockRenderDispatcher> DISPATCHER = () -> Minecraft.getInstance().getBlockRenderer();

    @Override
    public List<BakedQuad> render(@Nonnull BlockState mirrorState, @Nonnull BlockState baseState, @Nonnull BakedModel model, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        Optional<BlockState> blockState = ModelDataUtils.getData(extraData, SecretModelData.MODEL_MAP_STATE);
        if(!blockState.isPresent()) {
            return super.render(mirrorState, baseState, model, side, rand, extraData);
        }

        if (side != null) {
            return Collections.emptyList();
        }

        List<BakedQuad> outQuads = new ArrayList<>();

        BlockState mappedState = blockState.get();
        BakedModel mappedModel = DISPATCHER.get().getBlockModel(mappedState);
        AABB bb = this.createQuadBorder(mappedModel, mappedState, rand, extraData);


        List<BakedQuad> allQuads = new ArrayList<>(model.getQuads(mirrorState, null, rand, extraData));
        for (Direction value : Direction.values()) {
            allQuads.addAll(model.getQuads(mirrorState, value, rand, extraData));
        }

        for (BakedQuad quad : allQuads) {
            outQuads.add(this.resizeQuad(quad, bb));
        }

        return outQuads;
    }

    private BakedQuad resizeQuad(BakedQuad texture, AABB modelRange) {
        List<Vec3> vertices = this.getVertexPositions(texture);
        List<Vec3> clamped = this.clampVertexPositions(modelRange, vertices);

        int size = DefaultVertexFormat.BLOCK.getIntegerSize();
        int[] aint = new int[texture.getVertices().length];
        System.arraycopy(texture.getVertices(), 0, aint, 0, aint.length);

        for (int v = 0; v < clamped.size(); v++) {
            Vec3 vec = clamped.get(v);

            aint[v*size] = Float.floatToIntBits((float) vec.x);
            aint[v*size+1] = Float.floatToIntBits((float) vec.y);
            aint[v*size+2] = Float.floatToIntBits((float) vec.z);

        }

        this.resetUVPositions(aint, size, 4, vertices, clamped);

        return new BakedQuad(aint, texture.getTintIndex(), texture.getDirection(), texture.getSprite(), texture.isShade());
    }

    private void resetUVPositions(int[] aint, int size, int uOff, List<Vec3> vertices, List<Vec3> clampedVertices) {
        boolean uvRot = aint[uOff] == aint[size+uOff];

        int[] t = new int[]{ 1, 0, 3, 2 };
        int[] a = new int[]{ 3, 2, 1, 0 };
        int[] mappedU = uvRot ? a : t;
        int[] mappedV = uvRot ? t : a;

        for (int v = 0; v < 4; v++) {
            if(vertices.get(v).equals(clampedVertices.get(v))) {
                continue;
            }
            Vec3 clamped = clampedVertices.get(v);
            Vec3 from = vertices.get(v);

            Vec3 toU = vertices.get(mappedU[v]);
            Vec3 toV = vertices.get(mappedV[v]);

            Direction.Axis uAxis = getDifferential(from, toU);
            Direction.Axis vAxis = getDifferential(from, toV);

            double uInterpolated = dist(clamped, from, uAxis) / dist(toU, from, uAxis);
            double vInterpolated = dist(clamped, from, vAxis) / dist(toV, from, vAxis);

            aint[v*size + uOff] = Float.floatToIntBits((float) this.interpolate(Float.intBitsToFloat(aint[v*size + uOff]), Float.intBitsToFloat(aint[mappedU[v]*size + uOff]), uInterpolated));
            aint[v*size + uOff+1] = Float.floatToIntBits((float) this.interpolate(Float.intBitsToFloat(aint[v*size + uOff+1]), Float.intBitsToFloat(aint[mappedV[v]*size + uOff+1]), vInterpolated));
        }
    }

    private Direction.Axis getDifferential(Vec3 v1, Vec3 v2) {
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

    private double dist(Vec3 v1, Vec3 v2, Direction.Axis axis) {
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

    private List<Vec3> clampVertexPositions(AABB modelQuadRange, List<Vec3> vertices) {
        List<Vec3> out = new ArrayList<>();
        for (Vec3 vertex : vertices) {
            out.add(new Vec3(
                Mth.clamp(vertex.x, modelQuadRange.minX, modelQuadRange.maxX),
                Mth.clamp(vertex.y, modelQuadRange.minY, modelQuadRange.maxY),
                Mth.clamp(vertex.z, modelQuadRange.minZ, modelQuadRange.maxZ)
            ));
        }
        return out;
    }

    private List<Vec3> getVertexPositions(BakedQuad quad) {
        int size = DefaultVertexFormat.BLOCK.getIntegerSize();
        int[] aint = quad.getVertices();

        List<Vec3> out = new ArrayList<>();
        for (int v = 0; v < 4; v++) {
            out.add(new Vec3(Float.intBitsToFloat(aint[v*size]), Float.intBitsToFloat(aint[v*size+1]), Float.intBitsToFloat(aint[v*size+2])));
        }
        return out;
    }

    private AABB createQuadBorder(BakedModel mappedModel, BlockState state, Random rand, IModelData extraData) {
        if(this.stateAreaCache.containsKey(state)) {
            return this.stateAreaCache.get(state);
        }
        List<BakedQuad> modelQuads = new ArrayList<>(mappedModel.getQuads(state, null, rand, extraData));
        for (Direction direction : Direction.values()) {
            modelQuads.addAll(mappedModel.getQuads(state, direction, rand, extraData));
        }

        Vec3 min = new Vec3(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
        Vec3 max = new Vec3(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);

        for (BakedQuad quad : modelQuads) {
            for (Vec3 vertexPos : this.getVertexPositions(quad)) {
                setVec(min, vertexPos, Math::min);
                setVec(max, vertexPos, Math::max);
            }
        }

        AABB bb = new AABB(min.x, min.y, min.z, max.x, max.y, max.z);
        this.stateAreaCache.put(state, bb);
        return bb;
    }

    private void setVec(Vec3 toSet, Vec3 vertex, BiFunction<Double, Double, Double> cons) {
        toSet.x = cons.apply(toSet.x, vertex.x);
        toSet.y = cons.apply(toSet.y, vertex.y);
        toSet.z = cons.apply(toSet.z, vertex.z);
    }

}
