package id.patchedpixel.scenecraft.core;

//import id.patchedpixel.scenecraft.generator.ScbinMappingReloadListener;
import id.patchedpixel.scenecraft.core.gui.GuiReplaySettings;
import id.patchedpixel.scenecraft.record.SceneCraftRecorder;
//import net.minecraft.resources.ResourceLocation;
import net.minecraft.SharedConstants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
//import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.CLIENT)
@Mod(value = SceneCraft.MOD_ID)
public class SceneCraftBackend {
    private final String version;
    private SceneCraft mod;

    public SceneCraftBackend(IEventBus modBus, ModContainer container) {
        this.version = container.getModInfo().getVersion().toString();

        modBus.addListener((RegisterKeyMappingsEvent event) -> {
            this.mod = new SceneCraft(this);
            this.mod.registerModuleKeyBindings();
            this.mod.getKeyBindingRegistry().onRegisterKeyMappings(event);
        });
//        modBus.addListener(RegisterClientReloadListenersEvent.class, event ->
//                event.addListener(
//                        ResourceLocation.fromNamespaceAndPath(SceneCraft.MOD_ID, "scbin_mapping_generator"),
//                        new ScbinMappingReloadListener()));
        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, parent) -> {
                    if (this.mod == null) {
                        return parent;
                    }
                    return new GuiReplaySettings(parent, this.mod.getSettingsRegistry()).toMinecraft();
                })
        );
        modBus.addListener((FMLClientSetupEvent event) -> {
            if (this.mod != null) this.mod.initModules();
        });
    }

    public String getVersion() {
        return version;
    }

    public String getMinecraftVersion() {
        return SharedConstants.getCurrentVersion().getName();
    }
}
