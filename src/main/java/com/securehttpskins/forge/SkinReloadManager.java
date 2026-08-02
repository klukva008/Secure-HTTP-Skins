package com.securehttpskins.forge;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.io.File;

@Mod.EventBusSubscriber(modid = SecureHttpSkins.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SkinReloadManager {

    private static KeyMapping reloadKey;

    @Mod.EventBusSubscriber(modid = SecureHttpSkins.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            reloadKey = new KeyMapping(
                    "key.securehttpskins.reload",
                    GLFW.GLFW_KEY_F9,
                    "key.categories.securehttpskins"
            );
            event.register(reloadKey);
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (reloadKey == null) return;
        if (!reloadKey.consumeClick()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        clearSkinCache();

        int count = 0;
        for (Player player : mc.level.players()) {
            if (player == mc.player) continue;
            PlayerInfo info = mc.getConnection().getPlayerInfo(player.getUUID());
            if (info != null) count++;
        }

        Component msg = Component.literal("\u00a7a[SecureHttpSkins] \u00a77Skin cache cleared (" + count + " player(s) nearby). Rejoin to re-download.");
        mc.player.displayClientMessage(msg, true);
        SecureHttpSkins.LOGGER.info("Skin cache cleared via F9: {} player(s) nearby", count);
    }

    public static void clearSkinCache() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return;

        int deleted = 0;

        File skinCache = new File(mc.gameDirectory, "skin_cache");
        if (skinCache.exists() && skinCache.isDirectory()) {
            File[] files = skinCache.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.getName().endsWith(".png")) {
                        f.delete();
                        deleted++;
                    }
                }
            }
        }

        File objectsDir = new File(mc.gameDirectory, "assets/skins");
        if (objectsDir.exists() && objectsDir.isDirectory()) {
            deleted += deleteRecursive(objectsDir);
        }

        SecureHttpSkins.LOGGER.info("Cleared {} cached skin file(s)", deleted);
    }

    private static int deleteRecursive(File fileOrDir) {
        int count = 0;
        if (fileOrDir.isDirectory()) {
            File[] children = fileOrDir.listFiles();
            if (children != null) {
                for (File child : children) {
                    count += deleteRecursive(child);
                }
            }
        }
        if (fileOrDir.delete()) count++;
        return count;
    }
}
