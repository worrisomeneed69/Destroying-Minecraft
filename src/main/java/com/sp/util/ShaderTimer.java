package com.sp.util;

public class ShaderTimer {
    private float timer;
    private float prevTimer;

    public void setTimer(float timer){
        this.timer = timer;
    }

    public void maxTimer(){
        this.timer = 1.0f;
    }

    public void setPrevTimer() {
        this.prevTimer = this.timer;
    }

    public void reset(){
        this.prevTimer = 0.0f;
        this.timer = 0.0f;
    }

    public float getTimer(float tickDelta) {
        return this.prevTimer + (this.timer - this.prevTimer) * tickDelta;
    }

}
