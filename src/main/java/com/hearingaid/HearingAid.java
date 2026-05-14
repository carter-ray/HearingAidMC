package com.hearingaid;
import com.hearingaid.config.HearingAidConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class HearingAid implements ModInitializer {

    @Override
    public void onInitialize() {
        // Load or create default config
        HearingAidConfig.load();
        System.out.println("[HearingAid] Initialized. Sound config loaded.");

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("hearingaid")
                .then(Commands.literal("reload")
                    .executes(context -> {
                        HearingAidConfig.save();
                        context.getSource().sendSuccess(() -> Component.literal("[HearingAid] Config reloaded."), true);
                        return 1;
                    })
                )
            );
        });


    }
}
