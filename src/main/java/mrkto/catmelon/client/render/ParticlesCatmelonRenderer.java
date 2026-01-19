package mrkto.catmelon.client.render;

import mchorse.blockbuster.client.particles.BedrockScheme;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import net.minecraft.world.World;

import java.util.*;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3d;

public class ParticlesCatmelonRenderer {
    private final EmitterEntry[] emitterArray;
    private final ArrayDeque<EmitterEntry> inactivePool;
    private final Set<EmitterEntry> activeList = new HashSet<>();
    private final Set<EmitterEntry> addPool = new HashSet<>();
    private boolean addPoolIterating = false;

    private final Vector2f tempVec2f = new Vector2f();
    public ParticlesCatmelonRenderer(int poolSize, World world) {
        emitterArray = new EmitterEntry[poolSize];
        inactivePool = new ArrayDeque<>(poolSize);
        for(int i = 0; i < poolSize; i++) {
            EmitterEntry emitterEntry = new EmitterEntry(i);
            emitterEntry.emitter.world = world;
            inactivePool.add(emitterEntry);
            emitterArray[i] = emitterEntry;
        }
    }
    public void render(float partialTicks) {
        synchronized (activeList) {
            for (EmitterEntry emitterEntry : activeList) {
                BedrockEmitter emitter = emitterEntry.emitter;
                if (emitterEntry.source != null) {
                    if (!emitterEntry.source.exist()) {
                        continue;
                    }
                    emitterEntry.source.getPosition(emitter.lastGlobal);
                    emitterEntry.source.getRotation(tempVec2f);
                    emitterEntry.setRotation(tempVec2f.y, tempVec2f.x);
                }
                emitter.render(partialTicks);
            }
        }
    }
    public void update() {
        synchronized (activeList) {
            addPoolIterating = true;
            activeList.addAll(addPool);
            addPool.clear();
            addPoolIterating = false;
            Iterator<EmitterEntry> iterator = activeList.iterator();
            while (iterator.hasNext()) {
                EmitterEntry emitterEntry = iterator.next();
                BedrockEmitter emitter = emitterEntry.emitter;
                emitter.update();
                if(emitter.isFinished()) {
                    iterator.remove();
                    inactivePool.add(emitterEntry);
                }
            }
        }


    }
    public int createEmitter(BedrockScheme scheme, float x, float y, float z, float yaw, float pitch) {
        EmitterEntry emitterEntry = inactivePool.poll();
        if(emitterEntry == null) {
            return -1;
        }
        emitterEntry.setPosition(x, y, z);
        emitterEntry.setRotation(yaw, pitch);
        emitterEntry.start(scheme);
        if(addPoolIterating) {
            activeList.add(emitterEntry);
        } else {
            addPool.add(emitterEntry);
        }

        return emitterEntry.id;
    }
    public int createEmitter(BedrockScheme scheme, EmitterSource source) {
        EmitterEntry emitterEntry = inactivePool.poll();
        if(emitterEntry == null) {
            return -1;
        }
        emitterEntry.source = source;
        emitterEntry.start(scheme);
        if(addPoolIterating) {
            activeList.add(emitterEntry);
        } else {
            addPool.add(emitterEntry);
        }
        return emitterEntry.id;
    }
    public EmitterEntry getEmitter(int id) {
        if(id < 0 || id >= emitterArray.length) {
            return null;
        }
        return emitterArray[id];
    }
    public boolean stopEmitter(int id) {
        if(id < 0 || id >= emitterArray.length) {
            return false;
        }
        emitterArray[id].emitter.running = false;
        return true;
    }
    public static class EmitterEntry {
        BedrockEmitter emitter;
        EmitterSource source;
        int id;
        private EmitterEntry(int id) {
            this.id = id;
            this.emitter = new BedrockEmitter();
        }
        public void setPosition(float x, float y, float z) {
            emitter.lastGlobal.x = x;
            emitter.lastGlobal.y = y;
            emitter.lastGlobal.z = z;
        }
        public void setRotation(float yaw, float pitch) {
            emitter.rotation.setIdentity();
            emitter.rotation.rotX(pitch);
            emitter.rotation.rotY(yaw);
        }
        protected void start(BedrockScheme scheme) {
            emitter.setScheme(scheme);
        }
    }
    public interface EmitterSource {
        void getPosition(Vector3d out);
        void getRotation(Vector2f out);
        boolean exist();
    }
}
