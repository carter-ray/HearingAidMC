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

    private static final Object SAVE_LOCK = new Object();
    private static volatile boolean saveQueued = false;
    private static volatile boolean saving = false;

    /** Load or create the config file */
    public static void load() {
        try {
            if (!Files.exists(CONFIG_PATH)) {
                Files.createDirectories(CONFIG_PATH.getParent());
                Files.writeString(CONFIG_PATH, "{\n\t\"fixed\": {},\n\t\"volume\": {}\n}", StandardOpenOption.CREATE);
            }

            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                root = root != null ? root : new JsonObject();

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

            } catch (Exception e) {
                System.err.println("[HearingAid] Config is malformed!\n" + e.getMessage());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void save() {
        JsonObject sounds = new JsonObject();

        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                sounds = GSON.fromJson(reader, JsonObject.class);
            } catch (Exception e) {
                
            }
        }

        JsonObject existingFixed = sounds.has("fixed")
                ? sounds.getAsJsonObject("fixed")
                : new JsonObject();

        JsonObject existingVolume = sounds.has("volume")
                ? sounds.getAsJsonObject("volume")
                : new JsonObject();

        // add new sounds from memory to json object for file writeout
        for (Map.Entry<String, Float> e : fixedSounds.entrySet()) {
            if (!existingFixed.has(e.getKey())) {
                existingFixed.addProperty(e.getKey(), e.getValue());
            }
        }
        for (Map.Entry<String, Float> e : volumeSounds.entrySet()) {
            if (!existingVolume.has(e.getKey())) {
                existingVolume.addProperty(e.getKey(), e.getValue());
            }
        }

        sounds.add("fixed", existingFixed);
        sounds.add("volume", existingVolume);
        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {
            GSON.toJson(sounds, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        load();
    }

    private static void queueSave() {
        synchronized (SAVE_LOCK) {
            saveQueued = true;

            // if not saving, start a thread
            // if saving already, the thread will pick up the change
            if (!saving) {
                saving = true;

                new Thread(() -> {
                    try {
                        while (true) {
                            boolean doSave;
                            synchronized (SAVE_LOCK) {
                                doSave = saveQueued;
                                saveQueued = false;
                            }

                            if (!doSave) break; // nothing left to save

                            save();
                            Thread.sleep(50);
                        }
                    } catch (InterruptedException ignored) {
                    } finally {
                        synchronized (SAVE_LOCK) {
                            saving = false;
                        }
                    }
                }).start();
            }
        }
    }

    public static float getFixedRange(SoundEvent sound) {
        String id = Registries.SOUND_EVENT.getId(sound).toString();
        if (!fixedSounds.containsKey(id)) {
            fixedSounds.put(id, 16.0f);
            queueSave();
        }
        return fixedSounds.get(id);
    }

    public static float getVolumeBaseRange(SoundEvent sound) {
        String id = Registries.SOUND_EVENT.getId(sound).toString();
        if (!volumeSounds.containsKey(id)) {
            volumeSounds.put(id, 16.0f);
            queueSave();
        }
        return volumeSounds.get(id);
    }

}
