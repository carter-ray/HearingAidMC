package com.hearingaid;
import com.hearingaid.config.HearingAidConfig;
import net.fabricmc.api.ModInitializer;

public class HearingAid implements ModInitializer {

    @Override
    public void onInitialize() {
        // Load or create default config
        HearingAidConfig.load();
        System.out.println("[HearingAid] Initialized. Sound config loaded.");
    }
}
