package id.patchedpixel.scenecraft.mixin.render;

import net.minecraft.client.renderer.SectionOcclusionGraph;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.concurrent.Future;

@Mixin(SectionOcclusionGraph.class)
public interface ChunkRenderingDataPreparerAccessor {
    @Accessor("fullUpdateTask")
    Future<?> fullUpdateFuture();
}
