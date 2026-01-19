package mrkto.catmelon.client.animation.data.model.parts;

import mrkto.catmelon.client.animation.utils.MatrixStack;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.util.ArrayList;
import java.util.List;

public class QuadsPart implements IRenderablePart {
    public List<Quad> quads = new ArrayList<>();

    private static final ThreadLocal<Vector3f> tempVector3f =
            ThreadLocal.withInitial(Vector3f::new);
    private static final ThreadLocal<Vector4f> tempVector4f =
            ThreadLocal.withInitial(Vector4f::new);

    @Override
    public void renderPart(BufferBuilder builder, MatrixStack stack, float partialTicks) {
        for (Quad quad : quads) {
            Vector3f normal = tempVector3f.get();
            normal.set(quad.x, quad.y, quad.z);
            stack.getNormalMatrix().transform(normal);

            for (Vertex vertex : quad.vertices) {
                Vector4f position = tempVector4f.get();
                position.set(vertex.x, vertex.y, vertex.z, 1f);
                stack.getModelMatrix().transform(position);

                builder.pos(position.x, position.y, position.z)
                        .tex(vertex.u, vertex.v)
                        .lightmap((int) OpenGlHelper.lastBrightnessX, (int) OpenGlHelper.lastBrightnessY)
                        .normal(normal.x, normal.y, normal.z)
                        .endVertex();
            }
        }
    }


    public static class Quad {
        public float x, y, z;
        public List<Vertex> vertices = new ArrayList<>();

    }
    public static class Vertex {
        public float x, y, z, u, v;
        public Vertex(float x, float y, float z, float u, float v) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.u = u;
            this.v = v;
        }
    }
}
