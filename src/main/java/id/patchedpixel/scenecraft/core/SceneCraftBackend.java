package id.patchedpixel.scenecraft.core;

import id.patchedpixel.scenecraft.record.SceneCraftRecorder;
import net.minecraft.SharedConstants;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@Mod(value = SceneCraft.MOD_ID, dist = Dist.CLIENT)
public class SceneCraftBackend {
    private final String version;
    private SceneCraft mod;

    public SceneCraftBackend(IEventBus modBus, ModContainer container) {
        this.version = container.getModInfo().getVersion().toString();

        // NOTE: the mod is constructed before the Minecraft instance exists (Main -> ClientModLoader.begin),
        // so SceneCraft (which captures Minecraft.getInstance()) must NOT be created here.
        // RegisterKeyMappingsEvent fires from the Options constructor, i.e. after Minecraft.instance is set.
        modBus.addListener(RegisterKeyMappingsEvent.class, event -> {
            this.mod = new SceneCraft(this);
            this.mod.registerModuleKeyBindings();
            this.mod.getKeyBindingRegistry().onRegisterKeyMappings(event);
        });
        modBus.addListener(RegisterPayloadHandlersEvent.class, SceneCraftRecorder::registerPayloads);
        modBus.addListener(FMLClientSetupEvent.class, event -> {
            if (this.mod != null) this.mod.initModules();
        });
    }

    public String getVersion() {
        return version;
    }

    public String getMinecraftVersion() {
        return SharedConstants.getCurrentVersion().name();
    }
}
