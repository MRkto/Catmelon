package mrkto.catmelon.client.impl.parse;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mrkto.catmelon.api.model.Skeleton;
import mrkto.catmelon.api.model.SkeletonBone;
import mrkto.catmelon.api.model.SkeletonSocket;
import mrkto.catmelon.client.impl.model.parted.IModelPart;
import mrkto.catmelon.client.impl.model.parted.PartedModel;
import mrkto.catmelon.client.impl.model.parted.TrianglesPart;
import mrkto.catmelon.client.impl.model.parted.LocatorPart;
import net.minecraft.util.EnumFacing;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.util.*;

public class ModelParserJSON {
//    public static Model parse(JsonObject object) {
//
//    }
    public static ParsedResult parseModel(JsonObject object) {
        JsonObject description = object.getAsJsonObject("description");
        int width = description.getAsJsonPrimitive("texture_width").getAsInt();
        int height = description.getAsJsonPrimitive("texture_height").getAsInt();

        return parseBones(width, height, object.getAsJsonArray("bones"));
    }
    public static ParsedResult parseBones(int width, int height, JsonArray array) {
        Map<String, SkeletonBone> flatBones = new HashMap<>();
        Map<String, String> parents = new HashMap<>();
        Map<String, List<String>> childrens = new HashMap<>();
        Map<String, Integer> indexesBones = new HashMap<>();
        Map<String, Integer> indexesSockets = new HashMap<>();
        List<SkeletonSocket> sockets = new ArrayList<>();
        PartedModel model = new PartedModel();
        int index = 0;
        for (JsonElement jsonElement : array) {
            JsonObject jsonBone = jsonElement.getAsJsonObject();

            String name = jsonBone.getAsJsonPrimitive("name").getAsString();
            indexesBones.put(name, index);
            SkeletonBone bone = new SkeletonBone(name, index);

            if(jsonBone.has("pivot"))
                parseVector(jsonBone.getAsJsonArray("pivot"), bone.pivot);
            if(jsonBone.has("rotation"))
                parseVector(jsonBone.getAsJsonArray("rotation"), bone.rotation);
            bone.rotation.x *= -1f;
            bone.rotation.y *= -1f;
            bone.rotation.scale((float) (Math.PI / 180f));
            bone.pivot.scale(0.0625f);
            bone.pivot.x *= -1f;

            List<IModelPart> parts = new ArrayList<>();
            if(jsonBone.has("cubes"))
                for (JsonElement element : jsonBone.getAsJsonArray("cubes"))
                    parts.add(parseCube(width, height, element.getAsJsonObject()));
            if(jsonBone.has("locators"))
                for (Map.Entry<String, JsonElement> entry : jsonBone.getAsJsonObject("locators").entrySet()) {
                    LocatorPart locatorPart = parseLocator(entry.getKey(), entry.getValue());
                    parts.add(locatorPart);
                    indexesSockets.put(entry.getKey(), sockets.size());
                    sockets.add(new SkeletonSocket(index, locatorPart.position, locatorPart.rotation));
                }

            if(jsonBone.has("parent")) {
                parents.put(name, jsonBone.getAsJsonPrimitive("parent").getAsString());
                childrens.computeIfAbsent(jsonBone.getAsJsonPrimitive("parent").getAsString(), k -> new ArrayList<>()).add(name);
            }
            model.parts.put(name, parts);
            index++;
            flatBones.put(bone.name, bone);
        }
        List<SkeletonBone> boneList = new ArrayList<>();

        for (SkeletonBone bone : flatBones.values()) {
            String parentID = parents.get(bone.name);
            List<String> childrenIDs = childrens.get(bone.name);
            if(childrenIDs != null) {
                bone.children = new ArrayList<>();
                for (String childID : childrenIDs) {
                    bone.children.add(flatBones.get(childID));
                }
            }
            if(parentID != null) {
                bone.parent = flatBones.get(parentID);
            }
            if(bone.parent == null) {
                boneList.add(bone);
            }
        }
        return new ParsedResult(model, new Skeleton(boneList, sockets, indexesBones, indexesSockets));
    }


    private static LocatorPart parseLocator(String name, JsonElement locator) {
        LocatorPart locatorPart = new LocatorPart(name);
        if(locator.isJsonArray()) {
            parseVector(locator.getAsJsonArray(), locatorPart.position);
        }
        if(locator.isJsonObject()) {
            parseVector(locator.getAsJsonObject().getAsJsonArray("offset"), locatorPart.position);
            parseVector(locator.getAsJsonObject().getAsJsonArray("rotation"), locatorPart.rotation);
        }

        locatorPart.position.scale(0.0625f);
        locatorPart.rotation.scale((float) (Math.PI / 180D));
        locatorPart.position.x *= -1;
        return locatorPart;
    }
    private static TrianglesPart parseCube(int width, int height, JsonObject object) {
        TrianglesPart cube = new TrianglesPart();
        float inflate = 0f;
        Vector3f origin = new Vector3f();
        Vector3f size = new Vector3f();
        Vector3f pivot = new Vector3f();
        Vector3f rotation = new Vector3f();
        UV[] uvSides = new UV[EnumFacing.values().length];
        if (object.has("inflate")) {
            inflate = object.get("inflate").getAsFloat() * 0.0625f;
        }
        parseVector(object.getAsJsonArray("size"), size);
        parseVector(object.getAsJsonArray("origin"), origin);
        origin.x += size.x;
        origin.x *= -1;
        origin.scale(0.0625f);
        size.scale(0.0625f);
        origin.x -= inflate;
        origin.y -= inflate;
        origin.z -= inflate;
        size.x += inflate * 2;
        size.y += inflate * 2;
        size.z += inflate * 2;


        if (object.has("pivot")) {
            parseVector(object.getAsJsonArray("pivot"), pivot);
            pivot.scale(0.0625f);
            pivot.x *= -1;
        } else {
            pivot.set(origin);
        }

        if (object.has("rotation")) {
            parseVector(object.getAsJsonArray("rotation"), rotation);
            rotation.scale((float) (Math.PI / -180f));
            rotation.z *= -1;
        }

        if (object.has("uv")) {
            boolean mirror = object.has("mirror") && object.get("mirror").getAsBoolean();

            parseUV(uvSides, width, height, size, object.get("uv"), mirror);
        }
        Matrix4f matrix1 = new Matrix4f();
        Matrix4f matrix2 = new Matrix4f();
        matrix1.setIdentity();
        matrix2.setIdentity();
        matrix2.setTranslation(pivot);
        matrix1.mul(matrix2);
        matrix2.setIdentity();
        matrix2.rotZ(rotation.z);
        matrix1.mul(matrix2);
        matrix2.rotY(rotation.y);
        matrix1.mul(matrix2);
        matrix2.rotX(rotation.x);
        matrix1.mul(matrix2);
        matrix2.setIdentity();
        matrix2.setTranslation(new Vector3f(-pivot.x, -pivot.y, -pivot.z));
        matrix1.mul(matrix2);

        for (EnumFacing facing : EnumFacing.values()) {
            UV uvSide = uvSides[facing.getIndex()];
            if (uvSide == null) {
                continue;
            }

            TrianglesPart.Face face = new TrianglesPart.Face();

            float nx = facing.getDirectionVec().getX();
            float ny = facing.getDirectionVec().getY();
            float nz = facing.getDirectionVec().getZ();
            face.x = nx; face.y = ny; face.z = nz;


            if (facing == EnumFacing.WEST) {
                TrianglesPart.Vertex v0 = new TrianglesPart.Vertex(origin.x, origin.y, origin.z, uvSide.minX, uvSide.maxY);
                TrianglesPart.Vertex v1 = new TrianglesPart.Vertex(origin.x, origin.y, origin.z + size.z, uvSide.maxX, uvSide.maxY);
                TrianglesPart.Vertex v2 = new TrianglesPart.Vertex(origin.x, origin.y + size.y, origin.z + size.z, uvSide.maxX, uvSide.minY);
                TrianglesPart.Vertex v3 = new TrianglesPart.Vertex(origin.x, origin.y + size.y, origin.z, uvSide.minX, uvSide.minY);

                face.vertices.add(v0);
                face.vertices.add(v1);
                face.vertices.add(v2);

                face.vertices.add(v0);
                face.vertices.add(v2);
                face.vertices.add(v3);

            } else if (facing == EnumFacing.EAST) {
                TrianglesPart.Vertex v0 = new TrianglesPart.Vertex(origin.x + size.x, origin.y, origin.z, uvSide.maxX, uvSide.maxY);
                TrianglesPart.Vertex v1 = new TrianglesPart.Vertex(origin.x + size.x, origin.y + size.y, origin.z, uvSide.maxX, uvSide.minY);
                TrianglesPart.Vertex v2 = new TrianglesPart.Vertex(origin.x + size.x, origin.y + size.y, origin.z + size.z, uvSide.minX, uvSide.minY);
                TrianglesPart.Vertex v3 = new TrianglesPart.Vertex(origin.x + size.x, origin.y, origin.z + size.z, uvSide.minX, uvSide.maxY);

                face.vertices.add(v0);
                face.vertices.add(v1);
                face.vertices.add(v2);

                face.vertices.add(v0);
                face.vertices.add(v2);
                face.vertices.add(v3);

            } else if (facing == EnumFacing.NORTH) {
                TrianglesPart.Vertex v0 = new TrianglesPart.Vertex(origin.x, origin.y, origin.z, uvSide.maxX, uvSide.maxY);
                TrianglesPart.Vertex v1 = new TrianglesPart.Vertex(origin.x, origin.y + size.y, origin.z, uvSide.maxX, uvSide.minY);
                TrianglesPart.Vertex v2 = new TrianglesPart.Vertex(origin.x + size.x, origin.y + size.y, origin.z, uvSide.minX, uvSide.minY);
                TrianglesPart.Vertex v3 = new TrianglesPart.Vertex(origin.x + size.x, origin.y, origin.z, uvSide.minX, uvSide.maxY);

                face.vertices.add(v0);
                face.vertices.add(v1);
                face.vertices.add(v2);

                face.vertices.add(v0);
                face.vertices.add(v2);
                face.vertices.add(v3);

            } else if (facing == EnumFacing.SOUTH) {
                TrianglesPart.Vertex v0 = new TrianglesPart.Vertex(origin.x + size.x, origin.y, origin.z + size.z, uvSide.maxX, uvSide.maxY);
                TrianglesPart.Vertex v1 = new TrianglesPart.Vertex(origin.x + size.x, origin.y + size.y, origin.z + size.z, uvSide.maxX, uvSide.minY);
                TrianglesPart.Vertex v2 = new TrianglesPart.Vertex(origin.x, origin.y + size.y, origin.z + size.z, uvSide.minX, uvSide.minY);
                TrianglesPart.Vertex v3 = new TrianglesPart.Vertex(origin.x, origin.y, origin.z + size.z, uvSide.minX, uvSide.maxY);

                face.vertices.add(v0);
                face.vertices.add(v1);
                face.vertices.add(v2);

                face.vertices.add(v0);
                face.vertices.add(v2);
                face.vertices.add(v3);

            } else if (facing == EnumFacing.UP) {
                TrianglesPart.Vertex v0 = new TrianglesPart.Vertex(origin.x, origin.y + size.y, origin.z, uvSide.maxX, uvSide.maxY);
                TrianglesPart.Vertex v1 = new TrianglesPart.Vertex(origin.x, origin.y + size.y, origin.z + size.z, uvSide.maxX, uvSide.minY);
                TrianglesPart.Vertex v2 = new TrianglesPart.Vertex(origin.x + size.x, origin.y + size.y, origin.z + size.z, uvSide.minX, uvSide.minY);
                TrianglesPart.Vertex v3 = new TrianglesPart.Vertex(origin.x + size.x, origin.y + size.y, origin.z, uvSide.minX, uvSide.maxY);

                face.vertices.add(v0);
                face.vertices.add(v1);
                face.vertices.add(v2);

                face.vertices.add(v0);
                face.vertices.add(v2);
                face.vertices.add(v3);

            } else if (facing == EnumFacing.DOWN) {
                TrianglesPart.Vertex v0 = new TrianglesPart.Vertex(origin.x + size.x, origin.y, origin.z + size.z, uvSide.minX, uvSide.maxY);
                TrianglesPart.Vertex v1 = new TrianglesPart.Vertex(origin.x, origin.y, origin.z + size.z, uvSide.maxX, uvSide.maxY);
                TrianglesPart.Vertex v2 = new TrianglesPart.Vertex(origin.x, origin.y, origin.z, uvSide.maxX, uvSide.minY);
                TrianglesPart.Vertex v3 = new TrianglesPart.Vertex(origin.x + size.x, origin.y, origin.z, uvSide.minX, uvSide.minY);

                face.vertices.add(v0);
                face.vertices.add(v1);
                face.vertices.add(v2);

                face.vertices.add(v0);
                face.vertices.add(v2);
                face.vertices.add(v3);
            }

            cube.faces.add(face);
        }

        Set<TrianglesPart.Vertex> processedList = new HashSet<>();
        Vector4f temp = new Vector4f();
        for (TrianglesPart.Face face : cube.faces) {
            temp.set(face.x, face.y, face.z, 1f);
            matrix1.transform(temp);
            Vector3f vector3f = new Vector3f(temp.x, temp.y, temp.z);
            vector3f.normalize();
            face.x = vector3f.x;
            face.y = vector3f.y;
            face.z = vector3f.z;

            for (TrianglesPart.Vertex vertex : face.vertices) {
                if(processedList.contains(vertex)) {
                    continue;
                }
                processedList.add(vertex);
                temp.set(vertex.x, vertex.y, vertex.z, 1f);
                matrix1.transform(temp);
                vertex.x = temp.x;
                vertex.y = temp.y;
                vertex.z = temp.z;
            }
        }


        return cube;
    }
    private static void parseUV(UV[] uvSides, int width, int height, Vector3f size, JsonElement element, boolean mirror) {
        if (element.isJsonArray()) {
            int x = element.getAsJsonArray().get(0).getAsInt();
            int y = element.getAsJsonArray().get(1).getAsInt();
            int w = (int) Math.floor(size.x);
            int h = (int) Math.floor(size.y);
            int d = (int) Math.floor(size.z);
            float tMinX = x + d;
            float tMinY = y + d;
            float tMaxX = tMinX + w;
            float tMaxY = tMinY + h;
            if (mirror) {
                float tmp = tMaxX;
                tMaxX = tMinX;
                tMinX = tmp;
            }

            uvSides[EnumFacing.NORTH.getIndex()] = new UV(tMinX, tMinY, tMaxX, tMaxY, width, height);
            tMinX = x;
            tMinY = y + d;
            tMaxX = tMinX + d;
            tMaxY = tMinY + h;
            if (mirror) {
                tMinX = x + d + w;
                tMinY = y + d;
                tMaxX = tMinX + d;
                tMaxY = tMinY + h;
                float tmp = tMinX;
                tMinX = tMaxX;
                tMaxX = tmp;
            }

            uvSides[EnumFacing.EAST.getIndex()] = new UV(tMinX, tMinY, tMaxX, tMaxY, width, height);
            tMinX = x + d * 2.0F + w;
            tMinY = y + d;
            tMaxX = tMinX + w;
            tMaxY = tMinY + h;
            if (mirror) {
                float tmp = tMaxX;
                tMaxX = tMinX;
                tMinX = tmp;
            }

            uvSides[EnumFacing.SOUTH.getIndex()] = new UV(tMinX, tMinY, tMaxX, tMaxY, width, height);
            tMinX = x + d + w;
            tMinY = y + d;
            tMaxX = tMinX + d;
            tMaxY = tMinY + h;
            if (mirror) {
                tMinX = x;
                tMinY = y + d;
                tMaxX = tMinX + d;
                tMaxY = tMinY + h;
                float tmp = tMinX;
                tMinX = tMaxX;
                tMaxX = tmp;
            }

            uvSides[EnumFacing.WEST.getIndex()] = new UV(tMinX, tMinY, tMaxX, tMaxY, width, height);
            tMinX = x + d;
            tMinY = y;
            tMaxX = tMinX + w;
            tMaxY = tMinY + d;
            if (mirror) {
                float tmp = tMaxX;
                tMaxX = tMinX;
                tMinX = tmp;
            }

            uvSides[EnumFacing.UP.getIndex()] = new UV(tMinX, tMinY, tMaxX, tMaxY, width, height);
            tMinX = x + d + w;
            tMinY = y + d;
            tMaxX = tMinX + w;
            tMaxY = y;
            if (mirror) {
                float tmp = tMaxX;
                tMaxX = tMinX;
                tMinX = tmp;
            }

            uvSides[EnumFacing.DOWN.getIndex()] = new UV(tMinX, tMinY, tMaxX, tMaxY, width, height);
        }
        else if (element.isJsonObject()) {
            JsonObject sides = element.getAsJsonObject();

            if (sides.has("north")) uvSides[EnumFacing.NORTH.getIndex()] = parseUVSide(width, height, sides.get("north").getAsJsonObject());
            if (sides.has("east")) uvSides[EnumFacing.EAST.getIndex()] = parseUVSide(width, height, sides.get("east").getAsJsonObject());
            if (sides.has("south")) uvSides[EnumFacing.SOUTH.getIndex()] = parseUVSide(width, height, sides.get("south").getAsJsonObject());
            if (sides.has("west")) uvSides[EnumFacing.WEST.getIndex()] = parseUVSide(width, height, sides.get("west").getAsJsonObject());
            if (sides.has("up")) uvSides[EnumFacing.UP.getIndex()] = parseUVSide(width, height, sides.get("up").getAsJsonObject());
            if (sides.has("down")) uvSides[EnumFacing.DOWN.getIndex()] = parseUVSide(width, height, sides.get("down").getAsJsonObject());
        }
    }

    private static UV parseUVSide(int width, int height, JsonObject uvSide) {
        JsonArray uv = uvSide.getAsJsonArray("uv");
        JsonArray uvSize = uvSide.getAsJsonArray("uv_size");
        return new UV(uv.get(0).getAsFloat(), uv.get(1).getAsFloat(), (uv.get(0).getAsFloat() + uvSize.get(0).getAsFloat()), (uv.get(1).getAsFloat() + uvSize.get(1).getAsFloat()), width, height);
    }

    private static void parseVector(JsonArray array, Vector3f vector) {
        vector.x = array.get(0).getAsFloat();
        vector.y = array.get(1).getAsFloat();
        vector.z = array.get(2).getAsFloat();
    }
    public static class UV {
        public UV(float minX, float minY, float maxX, float maxY, int width, int height) {
            this.minX = minX / width;
            this.minY = minY / height;
            this.maxX = maxX / width;
            this.maxY = maxY / height;
        }
        public float minX, minY, maxX, maxY;
    }
    public static final class ParsedResult {
        public final PartedModel model;
        public final Skeleton skeleton;
        public ParsedResult(PartedModel model, Skeleton skeleton) {
            this.model = model;
            this.skeleton = skeleton;
        }
    }
}
