package com.sp.networking.S2C;

import com.sp.networking.InitializePackets;
import com.sp.render.supernova.SupernovaRenderer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class InvokeSupernovaPacket {

    public static void receive(InitializePackets.SupernovaPayload payload, ClientPlayNetworking.Context context){
        context.client().execute(()->{
            boolean on = payload.start() == 1;
            SupernovaRenderer.toggleSupernova(on);
        });
    }

}
