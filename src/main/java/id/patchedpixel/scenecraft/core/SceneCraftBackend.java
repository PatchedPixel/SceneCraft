package id.patchedpixel.scenecraft.core;

import net.minecraft.SharedConstants;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@Mod(value = SceneCraft.MOD_ID, dist = Dist.CLIENT)
public class SceneCraftBackend {
    private final String version;
    private final SceneCraft mod;

    public SceneCraftBackend(IEventBus modBus, ModContainer container) {
        this.version = container.getModInfo().getVersion().toString();
        this.mod = new SceneCraft(this);

        modBus.addListener(RegisterKeyMappingsEvent.class, mod.getKeyBindingRegistry()::onRegisterKeyMappings);
        modBus.addListener(RegisterPayloadHandlersEvent.class, id.patchedpixel.scenecraft.record.SceneCraftRecording::registerPayloads);

        mod.initModules();
    }

    public String getVersion() {
        return version;
    }

    public String getMinecraftVersion() {
        return SharedConstants.getCurrentVersion().name();
    }

    public boolean isModLoaded(String id) {
        return ModList.get().isLoaded(id.toLowerCase());
    }
}
