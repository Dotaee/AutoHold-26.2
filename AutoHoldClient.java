package fr.autohold;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

/** AutoHold : H = avance et casse sans fin, aucune sécurité. Rappuie sur H pour arrêter. */
public final class AutoHoldClient implements ClientModInitializer {

    /** true = sprint pendant l'avance. */
    private static final boolean SPRINT = true;

    private static KeyMapping toggleKey;
    private static boolean on = false;

    @Override
    public void onInitializeClient() {
        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath("autohold", "main"));
        toggleKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.autohold.toggle", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_H, category));
        ClientTickEvents.END_CLIENT_TICK.register(AutoHoldClient::tick);
    }

    private static void tick(Minecraft mc) {
        if (mc.player == null) {
            if (on) {
                on = false;
                release(mc);
            }
            return;
        }

        while (toggleKey.consumeClick()) {
            on = !on;
            if (!on) release(mc);
            mc.player.sendSystemMessage(Component.literal("[AutoHold] " + (on ? "activé" : "désactivé")));
        }

        if (on) {
            mc.options.keyUp.setDown(true);
            mc.options.keyAttack.setDown(true);
            mc.options.keySprint.setDown(SPRINT);
        }
    }

    private static void release(Minecraft mc) {
        mc.options.keyUp.setDown(false);
        mc.options.keyAttack.setDown(false);
        mc.options.keySprint.setDown(false);
    }
}
