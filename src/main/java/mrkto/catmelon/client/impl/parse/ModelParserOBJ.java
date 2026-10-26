package mrkto.catmelon.client.impl.parse;

import mrkto.catmelon.client.impl.model.parted.IModelPart;
import mrkto.catmelon.client.impl.model.parted.PartedModel;
import mrkto.catmelon.client.impl.model.parted.TrianglesPart;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ModelParserOBJ {
    public static PartedModel parse(InputStream file) throws IOException {
        PartedModel model = new PartedModel();
        List<Vector3f> objVerts = new ArrayList<>();
        List<Vector2f> objUvs = new ArrayList<>();
        List<Vector3f> objNorms = new ArrayList<>();

        List<IModelPart> currentBone;
        TrianglesPart currentPart = new TrianglesPart();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("\\s+");
                String type = parts[0];

                if (isFirstLine && ("v".equals(type) || "vt".equals(type) || "vn".equals(type))) {
                    currentBone = new ArrayList<>();
                    model.parts.put("", currentBone);
                    currentPart = new TrianglesPart();
                    currentBone.add(currentPart);
                    isFirstLine = false;
                }

                switch (type) {
                    case "v":
                        parseVert(parts, objVerts);
                        break;
                    case "vt":
                        parseUV(parts, objUvs);
                        break;
                    case "vn":
                        parseNorm(parts, objNorms);
                        break;
                    case "g":
                    case "o":
                        currentBone = new ArrayList<>();
                        model.parts.put(parts[1], currentBone);
                        currentPart = new TrianglesPart();
                        currentBone.add(currentPart);
                        isFirstLine = false;
                        break;
                    case "f":
                        parseFace(parts, objVerts, objUvs, objNorms, currentPart);
                        break;
                }
            }
        }
        return model;
    }

    private static void parseVert(String[] parts, List<Vector3f> out) {
        Vector3f v = new Vector3f();
        v.x = Float.parseFloat(parts[1]);
        v.y = Float.parseFloat(parts[2]);
        v.z = Float.parseFloat(parts[3]);
        out.add(v);
    }

    private static void parseUV(String[] parts, List<Vector2f> out) {
        Vector2f uv = new Vector2f();
        uv.x = Float.parseFloat(parts[1]);
        uv.y = 1f - (parts.length > 2 ? Float.parseFloat(parts[2]) : 0f);
        out.add(uv);
    }

    private static void parseNorm(String[] parts, List<Vector3f> out) {
        Vector3f n = new Vector3f();
        n.x = Float.parseFloat(parts[1]);
        n.y = Float.parseFloat(parts[2]);
        n.z = Float.parseFloat(parts[3]);
        out.add(n);
    }

    private static void parseFace(String[] parts, List<Vector3f> objVerts,
                                  List<Vector2f> objUvs, List<Vector3f> objNorms,
                                  TrianglesPart part) {
        List<Vector3f> tempPos = new ArrayList<>();
        List<Vector2f> tempUV = new ArrayList<>();
        Vector3f firstNormal = null;

        for (int i = 1; i < parts.length; i++) {
            String[] ref = parts[i].split("/");
            int vIdx = Integer.parseInt(ref[0]) - 1;
            int vtIdx = ref.length > 1 && !ref[1].isEmpty() ? Integer.parseInt(ref[1]) - 1 : -1;
            int vnIdx = ref.length > 2 && !ref[2].isEmpty() ? Integer.parseInt(ref[2]) - 1 : -1;

            vIdx = vIdx < 0 ? objVerts.size() + vIdx : vIdx;
            vtIdx = vtIdx < 0 ? objUvs.size() + vtIdx : vtIdx;
            vnIdx = vnIdx < 0 ? objNorms.size() + vnIdx : vnIdx;

            if (vIdx < 0 || vIdx >= objVerts.size()) continue;
            if (vtIdx < 0 || vtIdx >= objUvs.size()) vtIdx = -1;
            if (vnIdx < 0 || vnIdx >= objNorms.size()) vnIdx = -1;

            Vector3f pos = objVerts.get(vIdx);
            Vector2f uv = vtIdx >= 0 ? objUvs.get(vtIdx) : new Vector2f(0, 0);
            Vector3f norm = vnIdx >= 0 ? objNorms.get(vnIdx) : new Vector3f(0, 0, 0);

            if (firstNormal == null) firstNormal = norm;

            tempPos.add(pos);
            tempUV.add(uv);
        }

        if (tempPos.isEmpty() || firstNormal == null) return;

        if(tempPos.size() == 3) {
            TrianglesPart.Face face = new TrianglesPart.Face();
            face.x = firstNormal.x;
            face.y = firstNormal.y;
            face.z = firstNormal.z;

            for (int i = 0; i < tempPos.size(); i++) {
                face.vertices.add(new TrianglesPart.Vertex(
                        tempPos.get(i).x, tempPos.get(i).y, tempPos.get(i).z,
                        tempUV.get(i).x, tempUV.get(i).y
                ));
            }
            part.faces.add(face);
        }
        if(tempPos.size() == 4) {
            TrianglesPart.Face face1 = new TrianglesPart.Face();
            TrianglesPart.Face face2 = new TrianglesPart.Face();
            face1.x = firstNormal.x;
            face1.y = firstNormal.y;
            face1.z = firstNormal.z;
            face2.x = firstNormal.x;
            face2.y = firstNormal.y;
            face2.z = firstNormal.z;

            TrianglesPart.Vertex[] vertices = new TrianglesPart.Vertex[4];

            for (int i = 0; i < 4; i++) {
                vertices[i] = new TrianglesPart.Vertex(tempPos.get(i).x, tempPos.get(i).y, tempPos.get(i).z, tempUV.get(i).x, tempUV.get(i).y);
            }

            face1.vertices.add(vertices[0]);
            face1.vertices.add(vertices[1]);
            face1.vertices.add(vertices[2]);

            face2.vertices.add(vertices[0]);
            face2.vertices.add(vertices[2]);
            face2.vertices.add(vertices[3]);

            part.faces.add(face1);
            part.faces.add(face2);
        }

    }
}
