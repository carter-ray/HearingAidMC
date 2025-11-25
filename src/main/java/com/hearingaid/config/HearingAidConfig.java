package com.hearingaid.config;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HearingAidConfig {

    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("hearingaid.json");

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static Map<String, Float> fixedSounds = new HashMap<>();
    private static Map<String, Float> volumeSounds = new HashMap<>();

    /** Load or create the config file */
    public static void load() {
        try {
            if (!Files.exists(CONFIG_PATH)) {
                Files.createDirectories(CONFIG_PATH.getParent());
                Files.createFile(CONFIG_PATH);
                saveDefaults();
            }

            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

                fixedSounds.clear();
                volumeSounds.clear();

                JsonObject fixed = root.getAsJsonObject("fixed");
                if (fixed != null) {
                    for (Map.Entry<String, JsonElement> e : fixed.entrySet()) {
                        fixedSounds.put(e.getKey(), e.getValue().getAsFloat());
                    }
                }

                JsonObject volume = root.getAsJsonObject("volume");
                if (volume != null) {
                    for (Map.Entry<String, JsonElement> e : volume.entrySet()) {
                        volumeSounds.put(e.getKey(), e.getValue().getAsFloat());
                    }
                }
            } catch (JsonParseException e) {
                System.err.println("[HearingAid] Config is malformed! Generating defaults.");
                saveDefaults();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveDefaults() {
        fixedSounds.clear();
        volumeSounds.clear();

        for (SoundEvent sound : Registries.SOUND_EVENT) {
            String id = Registries.SOUND_EVENT.getId(sound).toString();
            Optional<Float> fixed_range = sound.fixedRange();

            if (fixed_range.isPresent()) {
                fixedSounds.put(id,  fixed_range.get());
            } else {
                volumeSounds.put(id, 16.0f); 
            }
        }

        save();
    }

    public static void save() {
        JsonObject root = new JsonObject();

        JsonObject fixed = new JsonObject();
        fixedSounds.forEach((k, v) -> fixed.addProperty(k, v));
        root.add("fixed", fixed);

        JsonObject volume = new JsonObject();
        volumeSounds.forEach((k, v) -> volume.addProperty(k, v));
        root.add("volume", volume);

        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            GSON.toJson(root, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static float getFixedRange(SoundEvent sound) {
        String id = Registries.SOUND_EVENT.getId(sound).toString();
        return fixedSounds.get(id);
    }

    public static float getVolumeBaseRange(SoundEvent sound) {
        String id = Registries.SOUND_EVENT.getId(sound).toString();
        return volumeSounds.get(id);
    }

    public static void ensureallSoundsRegistered() {
        boolean addedNew = false;
        for (SoundEvent sound : Registries.SOUND_EVENT) {
            String id = Registries.SOUND_EVENT.getId(sound).toString();

            if (fixedSounds.containsKey(id) || volumeSounds.containsKey(id)) continue;

            Optional<Float> fixed_range = sound.fixedRange();
            if (fixed_range.isPresent()) {
                fixedSounds.put(id, fixed_range.get());
            } else {
                volumeSounds.put(id, 16.0f);
            }

            addedNew = true;
            System.out.println("[HearingAid] Added new sound to config: " + id);
        }

        // Save the JSON only if we added any new entries
        if (addedNew) {
            save();
            System.out.println("[HearingAid] Config updated with new sounds.");
        }
    }


}
