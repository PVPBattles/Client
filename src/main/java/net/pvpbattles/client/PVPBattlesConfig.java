package net.pvpbattles.client;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

public final class PVPBattlesConfig {
    public static final int MAX_BLUR_STRENGTH = 10;

    public static boolean modEnabled = true;
    public static boolean blurEnabled = true;
    /** ぼかしの強さ 0〜10 (Minecraft標準と同じ段階) */
    public static int blurStrength = 5;
    public static boolean logoEnabled = true;
    public static boolean crystalOptimizerEnabled = true;
    public static boolean anchorOptimizerEnabled = true;
    public static boolean toggleSprintEnabled = true;

    /** Optimizer を止めるサーバーのアドレス (小文字、ポートなし) */
    public static final Set<String> disabledServers = new LinkedHashSet<>();

    private PVPBattlesConfig() {}

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("pvpbattles.properties");
    }

    private static String normalize(String address) {
        String a = address.trim().toLowerCase(Locale.ROOT);
        int idx = a.lastIndexOf(':');
        if (idx > 0 && a.substring(idx + 1).matches("\\d+")) {
            a = a.substring(0, idx);
        }
        return a;
    }

    /** 今つないでいるサーバーのアドレス。マルチプレイでなければ null */
    public static String currentServerAddress() {
        ServerInfo info = MinecraftClient.getInstance().getCurrentServerEntry();
        if (info == null || info.address == null) {
            return null;
        }
        return normalize(info.address);
    }

    private static boolean matches(String current, String entry) {
        return current.equals(entry) || current.endsWith("." + entry);
    }

    /** 今のサーバーで Optimizer を止める設定になっているか */
    public static boolean isServerBlocked() {
        String current = currentServerAddress();
        if (current == null) {
            return false;
        }
        for (String entry : disabledServers) {
            if (matches(current, entry)) {
                return true;
            }
        }
        return false;
    }

    /** 今のサーバーの止める/止めないを切り替える。サーバーにつないでいないときは何もしない */
    public static void toggleCurrentServerBlocked() {
        String current = currentServerAddress();
        if (current == null) {
            return;
        }
        if (isServerBlocked()) {
            disabledServers.removeIf(entry -> matches(current, entry));
        } else {
            disabledServers.add(current);
        }
        save();
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
            crystalOptimizerEnabled = Boolean.parseBoolean(props.getProperty("crystalOptimizerEnabled", "true"));
            anchorOptimizerEnabled = Boolean.parseBoolean(props.getProperty("anchorOptimizerEnabled", "true"));
            toggleSprintEnabled = Boolean.parseBoolean(props.getProperty("toggleSprintEnabled", "true"));

            try {
                int strength = Integer.parseInt(props.getProperty("blurStrength", "5").trim());
                // 以前の 0〜100 の値が残っていたら 0〜10 に直す
                if (strength > MAX_BLUR_STRENGTH) {
                    strength = Math.round(strength / 10.0F);
                }
                blurStrength = Math.max(0, Math.min(MAX_BLUR_STRENGTH, strength));
            } catch (NumberFormatException e) {
                blurStrength = 5;
            }

            disabledServers.clear();
            for (String part : props.getProperty("disabledServers", "").split(",")) {
                String entry = part.trim();
                if (!entry.isEmpty()) {
                    disabledServers.add(normalize(entry));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        Properties props = new Properties();
        props.setProperty("modEnabled", Boolean.toString(modEnabled));
        props.setProperty("blurEnabled", Boolean.toString(blurEnabled));
        props.setProperty("blurStrength", Integer.toString(blurStrength));
        props.setProperty("logoEnabled", Boolean.toString(logoEnabled));
        props.setProperty("crystalOptimizerEnabled", Boolean.toString(crystalOptimizerEnabled));
        props.setProperty("anchorOptimizerEnabled", Boolean.toString(anchorOptimizerEnabled));
        props.setProperty("toggleSprintEnabled", Boolean.toString(toggleSprintEnabled));
        props.setProperty("disabledServers", String.join(",", disabledServers));
        try (OutputStream out = Files.newOutputStream(file())) {
            props.store(out, "PVPBattles");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
