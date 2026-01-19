package mrkto.catmelon.client.animation.parse;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mrkto.catmelon.client.animation.data.model.Model;
import mrkto.catmelon.client.animation.data.model.ModelBone;
import mrkto.catmelon.client.animation.data.model.parts.QuadsPart;
import mrkto.catmelon.client.animation.data.model.parts.LocatorPart;
import net.minecraft.util.EnumFacing;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelParserJSON {
//    public static Model parse(JsonObject object) {
//
//    }
    public static Model parseModel(JsonObject object) {
        JsonObject description = object.getAsJsonObject("description");
        int width = description.getAsJsonPrimitive("texture_width").getAsInt();
        int height = description.getAsJsonPrimitive("texture_height").getAsInt();

        return parseBones(width, height, object.getAsJsonArray("bones"));
    }
    public static Model parseBones(int width, int height, JsonArray array) {
        Map<String, ModelBone> flatBones = new HashMap<>();
        Map<String, String> parents = new HashMap<>();
        Map<String, List<String>> childrens = new HashMap<>();
        for (JsonElement jsonElement : array) {
            JsonObject jsonBone = jsonElement.getAsJsonObject();

            String name = jsonBone.getAsJsonPrimitive("name").getAsString();
            ModelBone bone = new ModelBone(name);

            if(jsonBone.has("pivot"))
                parseVector(jsonBone.getAsJsonArray("pivot"), bone.pivot);
            if(jsonBone.has("rotation"))
                parseVector(jsonBone.getAsJsonArray("rotation"), bone.rotation);
            bone.rotation.x *= -1f;
            bone.rotation.y *= -1f;
            bone.rotation.scale((float) (Math.PI / 180f));
            bone.pivot.scale(0.0625f);
            bone.pivot.x *= -1f;


            if(jsonBone.has("cubes"))
                for (JsonElement element : jsonBone.getAsJsonArray("cubes"))
                    bone.parts.add(parseCube(width, height, element.getAsJsonObject()));
            if(jsonBone.has("locators"))
                for (Map.Entry<String, JsonElement> entry : jsonBone.getAsJsonObject("locators").entrySet())
                    bone.parts.add(parseLocator(entry.getKey(), entry.getValue().getAsJsonArray()));
            if(jsonBone.has("parent")) {
                parents.put(name, jsonBone.getAsJsonPrimitive("parent").getAsString());
                childrens.computeIfAbsent(jsonBone.getAsJsonPrimitive("parent").getAsString(), k -> new ArrayList<>()).add(name);
            }

            flatBones.put(bone.id, bone);
        }
        Model model = new Model();
        for (ModelBone bone : flatBones.values()) {
            String parentID = parents.get(bone.id);
            List<String> childrenIDs = childrens.get(bone.id);
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
                model.bones.add(bone);
            }
        }
        return model;
    }
    private static LocatorPart parseLocator(String name, JsonArray pivot) {
        LocatorPart locatorPart = new LocatorPart(name);
        parseVector(pivot, locatorPart.pivot);
        locatorPart.pivot.scale(0.0625f);
        locatorPart.pivot.x *= -1;
        return locatorPart;
    }
    private static QuadsPart parseCube(int width, int height, JsonObject object) {
        QuadsPart cube = new QuadsPart();
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
            if(uvSide == null) {
                continue;
            }
            QuadsPart.Quad quad = new QuadsPart.Quad();
            if(facing == EnumFacing.WEST) {
                quad.x = facing.getDirectionVec().getX();
                quad.y = facing.getDirectionVec().getY();
                quad.z = facing.getDirectionVec().getZ();

                quad.vertices.add(new QuadsPart.Vertex(origin.x, origin.y, origin.z, uvSide.minX, uvSide.maxY));
                quad.vertices.add(new QuadsPart.Vertex(origin.x, origin.y, origin.z + size.z, uvSide.maxX, uvSide.maxY));
                quad.vertices.add(new QuadsPart.Vertex(origin.x, origin.y + size.y, origin.z + size.z, uvSide.maxX, uvSide.minY));
                quad.vertices.add(new QuadsPart.Vertex(origin.x, origin.y + size.y, origin.z, uvSide.minX, uvSide.minY));

            }
            if(facing == EnumFacing.NORTH) {
                quad.x = facing.getDirectionVec().getX();
                quad.y = facing.getDirectionVec().getY();
                quad.z = facing.getDirectionVec().getZ();

                quad.vertices.add(new QuadsPart.Vertex(origin.x, origin.y, origin.z, uvSide.maxX, uvSide.maxY));
                quad.vertices.add(new QuadsPart.Vertex(origin.x, origin.y + size.y, origin.z, uvSide.maxX, uvSide.minY));
                quad.vertices.add(new QuadsPart.Vertex(origin.x + size.x, origin.y + size.y, origin.z, uvSide.minX, uvSide.minY));
                quad.vertices.add(new QuadsPart.Vertex(origin.x + size.x, origin.y, origin.z, uvSide.minX, uvSide.maxY));

            }
            if(facing == EnumFacing.EAST) {
                quad.x = facing.getDirectionVec().getX();
                quad.y = facing.getDirectionVec().getY();
                quad.z = facing.getDirectionVec().getZ();


                quad.vertices.add(new QuadsPart.Vertex(origin.x + size.x, origin.y, origin.z, uvSide.maxX, uvSide.maxY));
                quad.vertices.add(new QuadsPart.Vertex(origin.x + size.x, origin.y + size.y, origin.z, uvSide.maxX, uvSide.minY));
                quad.vertices.add(new QuadsPart.Vertex(origin.x + size.x, origin.y + size.y, origin.z + size.z, uvSide.minX, uvSide.minY));
                quad.vertices.add(new QuadsPart.Vertex(origin.x + size.x, origin.y, origin.z + size.z, uvSide.minX, uvSide.maxY));

            }
            if(facing == EnumFacing.SOUTH) {
                quad.x = facing.getDirectionVec().getX();
                quad.y = facing.getDirectionVec().getY();
                quad.z = facing.getDirectionVec().getZ();
                quad.vertices.add(new QuadsPart.Vertex(origin.x + size.x, origin.y, origin.z + size.z, uvSide.maxX, uvSide.maxY));
                quad.vertices.add(new QuadsPart.Vertex(origin.x + size.x, origin.y + size.y, origin.z + size.z, uvSide.maxX, uvSide.minY));
                quad.vertices.add(new QuadsPart.Vertex(origin.x, origin.y + size.y, origin.z + size.z, uvSide.minX, uvSide.minY));
                quad.vertices.add(new QuadsPart.Vertex(origin.x, origin.y, origin.z + size.z, uvSide.minX, uvSide.maxY));

            }
            if(facing == EnumFacing.UP) {
                quad.x = facing.getDirectionVec().getX();
                quad.y = facing.getDirectionVec().getY();
                quad.z = facing.getDirectionVec().getZ();

                quad.vertices.add(new QuadsPart.Vertex(origin.x, origin.y + size.y, origin.z, uvSide.maxX, uvSide.maxY));
                quad.vertices.add(new QuadsPart.Vertex(origin.x, origin.y + size.y, origin.z + size.z, uvSide.maxX, uvSide.minY));
                quad.vertices.add(new QuadsPart.Vertex(origin.x + size.x, origin.y + size.y, origin.z + size.z, uvSide.minX, uvSide.minY));
                quad.vertices.add(new QuadsPart.Vertex(origin.x + size.x, origin.y + size.y, origin.z, uvSide.minX, uvSide.maxY));
            }
            if(facing == EnumFacing.DOWN) {
                quad.x = facing.getDirectionVec().getX();
                quad.y = facing.getDirectionVec().getY();
                quad.z = facing.getDirectionVec().getZ();

                quad.vertices.add(new QuadsPart.Vertex(origin.x + size.x, origin.y, origin.z + size.z, uvSide.minX, uvSide.maxY));
                quad.vertices.add(new QuadsPart.Vertex(origin.x, origin.y, origin.z + size.z, uvSide.maxX, uvSide.maxY));
                quad.vertices.add(new QuadsPart.Vertex(origin.x, origin.y, origin.z, uvSide.maxX, uvSide.minY));
                quad.vertices.add(new QuadsPart.Vertex(origin.x + size.x, origin.y, origin.z, uvSide.minX, uvSide.minY));


            }
            cube.quads.add(quad);
        }
        Vector4f temp = new Vector4f();
        for (QuadsPart.Quad quad : cube.quads) {
            temp.set(quad.x, quad.y, quad.z, 1f);
            matrix1.transform(temp);
            Vector3f vector3f = new Vector3f(temp.x, temp.y, temp.z);
            vector3f.normalize();
            quad.x = vector3f.x;
            quad.y = vector3f.y;
            quad.z = vector3f.z;

            for (QuadsPart.Vertex vertex : quad.vertices) {
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
}
