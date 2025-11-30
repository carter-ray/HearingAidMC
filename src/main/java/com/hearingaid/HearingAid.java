package com.hearingaid;
import com.hearingaid.config.HearingAidConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public class HearingAid implements ModInitializer {

    @Override
    public void onInitialize() {
        // Load or create default config
        HearingAidConfig.load();
        System.out.println("[HearingAid] Initialized. Sound config loaded.");

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("hearingaid")
                .then(CommandManager.literal("reload")
                    .executes(context -> {
                        HearingAidConfig.save();
                        context.getSource().sendFeedback(() -> Text.literal("[HearingAid] Config reloaded."), true);
                        return 1;
                    })
                )
            );
        });
    }
}
