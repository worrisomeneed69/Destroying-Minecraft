package com.sp.mixin;

import foundry.veil.api.client.render.post.PostProcessingManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = PostProcessingManager.class, remap = false)
public interface PostProcessingManagerAccessor {

    @Accessor("activePipelines")
    List<PostProcessingManager.ProfileEntry> getActuallyActivePipelines();

}
