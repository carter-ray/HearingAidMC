package com.raindelay;

import java.util.Map;

import com.raindelay.gui.RainDelayConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.terraformersmc.modmenu.api.UpdateChecker;
import com.terraformersmc.modmenu.util.mod.fabric.FabricLoaderUpdateChecker;

public class RainDelayModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return RainDelayConfigScreen.getFactory();
    }

    @Override
	public Map<String, UpdateChecker> getProvidedUpdateCheckers() {
        return Map.of("fabricloader", new FabricLoaderUpdateChecker());
	}
}
