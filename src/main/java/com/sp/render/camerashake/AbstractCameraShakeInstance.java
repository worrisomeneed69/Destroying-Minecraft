package com.sp.render.camerashake;

import java.util.Optional;

public abstract class AbstractCameraShakeInstance {
    protected float trauma;
    protected int progress;
    protected final int duration;

    protected AbstractCameraShakeInstance(int duration) {
        this.duration = duration;
        this.progress = 0;
    }

    public void tick(){
        progress++;
    }

    public float getTrauma() {
        return this.trauma;
    }

    public boolean isFinished() {
        return this.progress >= this.duration;
    }

    public enum Type {
        NORMAL(1),
        POINT(2),
        SUSTAINED(3);
        private final int id;

        Type(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public Optional<Type> getFromId(int id) {
            if (id <= 0 || id > Type.values().length) return Optional.empty();

            Type[] types = Type.values();
            return Optional.of(types[id]);
        }
    }
}
