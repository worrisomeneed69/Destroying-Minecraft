package com.sp.render;

import net.minecraft.client.MinecraftClient;

public class BlackScreenManager {
    private static boolean isBlackScreen;


    public static boolean isIsBlackScreen() {
        return isBlackScreen;
    }

    public static void setBlackScreen(boolean isBlackScreen) {
        BlackScreenManager.isBlackScreen = isBlackScreen;
        MinecraftClient.getInstance().options.hudHidden = isBlackScreen;
    }
}
