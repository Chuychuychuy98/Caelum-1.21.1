package nuparu.caelum.client.event;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import nuparu.caelum.Caelum;
import nuparu.caelum.client.data.StarDataManager;

@EventBusSubscriber(modid = Caelum.MODID, value = Dist.CLIENT)
public class ClientReloadListeners {
    @SubscribeEvent
    public static void addReloadListener(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(StarDataManager.INSTANCE);
    }
}
