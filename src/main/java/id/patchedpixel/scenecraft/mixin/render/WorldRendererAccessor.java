package id.patchedpixel.scenecraft.mixin.render;

import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;

import net.minecraft.client.renderer.chunk.SectionRenderDispatcher.RenderSection;

@Mixin(LevelRenderer.class)
public interface WorldRendererAccessor {
}
