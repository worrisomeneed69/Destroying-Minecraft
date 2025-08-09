package com.sp.render.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

public class HSVColorTextureManager {
    private static boolean init;
    private static NativeImageBackedTexture hsvTexture;
    private static NativeImage hsvImage;
    private static Identifier hsvTextureIdentifier;

    private static NativeImageBackedTexture hueTexture;
    private static NativeImage hueImage;
    private static Identifier hueTextureIdentifier;

    public static void init() {
        if (!init) {
            hsvTexture = new NativeImageBackedTexture(255, 255, false);
            hsvImage = hsvTexture.getImage();
            hsvTextureIdentifier = MinecraftClient.getInstance().getTextureManager().registerDynamicTexture("hsv_color", hsvTexture);

            hueTexture = new NativeImageBackedTexture(20, 255, false);
            hueImage = hueTexture.getImage();
            hueTextureIdentifier = MinecraftClient.getInstance().getTextureManager().registerDynamicTexture("hue_color", hueTexture);
            init = true;
        }
    }


    public static NativeImageBackedTexture getHsvTexture() {
        return hsvTexture;
    }

    public static NativeImage getHsvImage() {
        return hsvImage;
    }

    public static Identifier getHsvTextureIdentifier() {
        return hsvTextureIdentifier;
    }


    public static NativeImageBackedTexture getHueTexture() {
        return hueTexture;
    }

    public static NativeImage getHueImage() {
        return hueImage;
    }

    public static Identifier getHueTextureIdentifier() {
        return hueTextureIdentifier;
    }

    public static void upload() {
        hsvTexture.upload();
        hueTexture.upload();
    }


}
