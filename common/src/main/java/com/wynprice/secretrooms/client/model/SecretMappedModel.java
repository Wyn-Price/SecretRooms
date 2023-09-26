package com.wynprice.secretrooms.client.model;

import com.google.common.math.DoubleMath;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.wynprice.secretrooms.client.SecretModelRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3d;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class  SecretMappedModel extends SecretBlockModel {

    private final Map<BlockState, AABB> stateAreaCache = new HashMap<>();

    private static final Supplier<BlockRenderDispatcher> DISPATCHER = () -> Minecraft.getInstance().getBlockRenderer();

    @Override
    public List<BakedQuad> render(@Nonnull BlockState mirrorState, @Nonnull BlockState baseState, @Nonnull BakedModel model, @Nullable Direction side, @Nonnull RandomSource rand, @Nonnull SecretModelRenderContext context) {
        Optional<BlockState> blockState = context.mappedState();
        if(blockState.isEmpty()) {
            return super.render(mirrorState, baseState, model, side, rand, context);
        }

        if (side != null) {
            return Collections.emptyList();
        }

        List<BakedQuad> outQuads = new ArrayList<>();

        BlockState mappedState = blockState.get();
        BakedModel mappedModel = DISPATCHER.get().getBlockModel(mappedState);
        AABB bb = this.createQuadBorder(mappedModel, mappedState, rand, context);

        List<BakedQuad> allQuads = new ArrayList<>(context.getQuads(model, mirrorState, null, rand));
        for (Direction value : Direction.values()) {
            allQuads.addAll(context.getQuads(model, mirrorState, value, rand));
        }

        for (BakedQuad quad : allQuads) {
            outQuads.add(this.resizeQuad(quad, bb));
        }

        return outQuads;
    }

    private BakedQuad resizeQuad(BakedQuad texture, AABB modelRange) {
        List<Vector3d> vertices = this.getVertexPositions(texture);
        List<Vector3d> clamped = this.clampVertexPositions(modelRange, vertices);

        int size = DefaultVertexFormat.BLOCK.getIntegerSize();
        int[] aint = new int[texture.getVertices().length];
        System.arraycopy(texture.getVertices(), 0, aint, 0, aint.length);

        for (int v = 0; v < clamped.size(); v++) {
            Vector3d vec = clamped.get(v);

            aint[v*size] = Float.floatToIntBits((float) vec.x);
            aint[v*size+1] = Float.floatToIntBits((float) vec.y);
            aint[v*size+2] = Float.floatToIntBits((float) vec.z);

        }

        this.resetUVPositions(aint, size, 4, vertices, clamped);

        return new BakedQuad(aint, texture.getTintIndex(), texture.getDirection(), texture.getSprite(), texture.isShade());
    }

    private void resetUVPositions(int[] aint, int size, int uOff, List<Vector3d> vertices, List<Vector3d> clampedVertices) {
        boolean uvRot = aint[uOff] == aint[size+uOff];

        int[] t = new int[]{ 1, 0, 3, 2 };
        int[] a = new int[]{ 3, 2, 1, 0 };
        int[] mappedU = uvRot ? a : t;
        int[] mappedV = uvRot ? t : a;

        for (int v = 0; v < 4; v++) {
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
        return switch (axis) {
            case X -> Math.abs(v1.x - v2.x);
            case Y -> Math.abs(v1.y - v2.y);
            case Z -> Math.abs(v1.z - v2.z);
        };
    }

    private List<Vector3d> clampVertexPositions(AABB modelQuadRange, List<Vector3d> vertices) {
        List<Vector3d> out = new ArrayList<>();
        for (Vector3d vertex : vertices) {
            out.add(new Vector3d(
                Mth.clamp(vertex.x, modelQuadRange.minX, modelQuadRange.maxX),
                Mth.clamp(vertex.y, modelQuadRange.minY, modelQuadRange.maxY),
                Mth.clamp(vertex.z, modelQuadRange.minZ, modelQuadRange.maxZ)
            ));
        }
        return out;
    }

    private List<Vector3d> getVertexPositions(BakedQuad quad) {
        int size = DefaultVertexFormat.BLOCK.getIntegerSize();
        int[] aint = quad.getVertices();

        List<Vector3d> out = new ArrayList<>();
        for (int v = 0; v < 4; v++) {
            out.add(new Vector3d(Float.intBitsToFloat(aint[v*size]), Float.intBitsToFloat(aint[v*size+1]), Float.intBitsToFloat(aint[v*size+2])));
        }
        return out;
    }

    private AABB createQuadBorder(BakedModel mappedModel, BlockState state, RandomSource rand, SecretModelRenderContext context) {
        if(this.stateAreaCache.containsKey(state)) {
            return this.stateAreaCache.get(state);
        }
        List<BakedQuad> modelQuads = new ArrayList<>(context.getQuads(mappedModel, state, null, rand));
        for (Direction direction : Direction.values()) {
            modelQuads.addAll(context.getQuads(mappedModel, state, direction, rand));
        }

        Vector3d min = new Vector3d(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
        Vector3d max = new Vector3d(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);

        for (BakedQuad quad : modelQuads) {
            for (Vector3d vertexPos : this.getVertexPositions(quad)) {
                setVec(min, vertexPos, Math::min);
                setVec(max, vertexPos, Math::max);
            }
        }

        AABB bb = new AABB(min.x, min.y, min.z, max.x, max.y, max.z);
        this.stateAreaCache.put(state, bb);
        return bb;
    }

    private void setVec(Vector3d toSet, Vector3d vertex, BiFunction<Double, Double, Double> cons) {
        toSet.x = cons.apply(toSet.x, vertex.x);
        toSet.y = cons.apply(toSet.y, vertex.y);
        toSet.z = cons.apply(toSet.z, vertex.z);
    }

}
