package com.hearingaid;
import com.hearingaid.config.HearingAidConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public class HearingAid implements ModInitializer {

    @Override
    public void onInitialize() {
        // Load or create default config
		ServerLifecycleEvents.SERVER_STARTED.register((MinecraftServer server) -> {
			HearingAidConfig.load();
            HearingAidConfig.ensureallSoundsRegistered();
			System.out.println("[HearingAid] Initialized. Sound config loaded.");

	    });

    }
}
