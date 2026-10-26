package mrkto.catmelon.client.impl.model.parted;

import mrkto.catmelon.client.utils.MatrixStack;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.util.ArrayList;
import java.util.List;

public class TrianglesPart implements IRenderablePart {
    public List<Face> faces = new ArrayList<>();

    private static final ThreadLocal<Vector3f> tempVector3f =
            ThreadLocal.withInitial(Vector3f::new);
    private static final ThreadLocal<Vector4f> tempVector4f =
            ThreadLocal.withInitial(Vector4f::new);

    @Override
    public void renderPart(BufferBuilder builder, MatrixStack stack, float partialTicks) {
        for (int i = 0, facesSize = faces.size(); i < facesSize; i++) {
            Face face = faces.get(i);
            Vector3f normal = tempVector3f.get();
            normal.set(face.x, face.y, face.z);
            stack.getNormalMatrix().transform(normal);

            List<Vertex> vertices = face.vertices;
            for (int j = 0, verticesSize = vertices.size(); j < verticesSize; j++) {
                Vertex vertex = vertices.get(j);
                Vector4f position = tempVector4f.get();
                position.set(vertex.x, vertex.y, vertex.z, 1f);
                stack.getModelMatrix().transform(position);

                builder.pos(position.x, position.y, position.z)
                        .tex(vertex.u, vertex.v)
                        .lightmap((int) OpenGlHelper.lastBrightnessY, (int) OpenGlHelper.lastBrightnessX)
                        .normal(normal.x, normal.y, normal.z)
                        .endVertex();
            }
        }
    }


    public static class Face {
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
