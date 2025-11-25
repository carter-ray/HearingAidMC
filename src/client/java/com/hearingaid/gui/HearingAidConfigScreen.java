package com.hearingaid.gui;

import com.hearingaid.config.HearingAidConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class HearingAidConfigScreen {
        public static ConfigScreenFactory<Screen> getFactory() {
                return parent -> {
                        HearingAidConfig config = hearingaidConfig.INSTANCE;
                        ConfigBuilder builder = ConfigBuilder.create()
                                .setParentScreen(parent)
                                .setTitle(Text.literal("Hearing Aid Config"));
                                
                        return builder.build();
                };
        }
}
