package id.patchedpixel.scenecraft.mixin.render;

import id.patchedpixel.scenecraft.render.hooks.EntityRendererHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.client.renderer.LevelRenderer;

/**
 * Forces the sky to always render when chroma keying is active. Ordinarily it only renders when the render distance is
 * at 4 or greater.
 */
@Mixin(LevelRenderer.class)
public abstract class Mixin_ChromaKeyForceSky {
    // Sky appears to be rendered unconditionally now
}
