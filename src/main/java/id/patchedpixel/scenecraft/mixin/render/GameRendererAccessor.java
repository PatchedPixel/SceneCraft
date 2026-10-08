package id.patchedpixel.scenecraft.mixin.render;

import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.fog.FogRenderer;
import com.mojang.blaze3d.resource.CrossFrameResourcePool;

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {

    @Accessor("resourcePool")
    CrossFrameResourcePool getPool();
    @Accessor
    GuiRenderer getGuiRenderer();
    @Accessor
    net.minecraft.client.renderer.state.GameRenderState getGameRenderState();
    @Accessor
    FogRenderer getFogRenderer();
}
