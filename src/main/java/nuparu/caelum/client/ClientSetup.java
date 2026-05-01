package nuparu.caelum.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import nuparu.caelum.Caelum;

@EventBusSubscriber(modid= Caelum.MODID, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiLayersEvent event) { // Note: This appears to be the right event, but I'm not 100% sure as it doesn't do anything as of yet anyway.
        //event.registerAboveAll(Caelum.MODID+"__debug", new DebugOverlay(Minecraft.getInstance()));
    }
}
