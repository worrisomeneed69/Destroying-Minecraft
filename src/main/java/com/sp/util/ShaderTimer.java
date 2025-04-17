package com.sp.util;

public class ShaderTimer {
    private float timer;
    private float lastTimer;

    public void setTimer(float timer){
        this.timer = timer;
    }

    public void maxTimer(){
        this.timer = 1.0f;
    }

    public void setLastTimer() {
        this.lastTimer = this.timer;
    }

    public void reset(){
        this.lastTimer = 0.0f;
        this.timer = 0.0f;
    }

    public float getTimer(float tickDelta) {
        return this.lastTimer + (this.timer - this.lastTimer) * tickDelta;
    }

}
