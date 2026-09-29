package net.pvpbattles.client;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class PVPBattlesConfig {
    public static boolean modEnabled = true;
    public static boolean blurEnabled = true;
    public static boolean logoEnabled = true;

    private PVPBattlesConfig() {}

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("pvpbattles.properties");
    }

    public static void load() {
        Path path = file();
        if (!Files.exists(path)) {
            return;
        }
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(path)) {
            props.load(in);
            modEnabled = Boolean.parseBoolean(props.getProperty("modEnabled", "true"));
            blurEnabled = Boolean.parseBoolean(props.getProperty("blurEnabled", "true"));
            logoEnabled = Boolean.parseBoolean(props.getProperty("logoEnabled", "true"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        Properties props = new Properties();
        props.setProperty("modEnabled", Boolean.toString(modEnabled));
        props.setProperty("blurEnabled", Boolean.toString(blurEnabled));
        props.setProperty("logoEnabled", Boolean.toString(logoEnabled));
        try (OutputStream out = Files.newOutputStream(file())) {
            props.store(out, "PVPBattles");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
