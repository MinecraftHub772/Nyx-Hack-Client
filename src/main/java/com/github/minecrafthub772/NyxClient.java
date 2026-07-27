package com.nyx.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

@Mod(modid = "nyx", name = "Nyx Hack Client", version = "1.0")
public class NyxClient {

    private static List<Module> modules = new ArrayList<>();
    private static boolean guiOpen = false;
    private static int memoryCleanerTick = 0;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);

        // Hack Modules
        modules.add(new Module("Fly", Keyboard.KEY_F, "§bFly"));
        modules.add(new Module("Speed", Keyboard.KEY_V, "§aSpeed"));
        modules.add(new Module("Fullbright", Keyboard.KEY_B, "§eFullbright"));
        modules.add(new Module("NoFall", Keyboard.KEY_N, "§cNoFall"));
        modules.add(new Module("Sprint", Keyboard.KEY_S, "§6Sprint"));
        modules.add(new Module("Jesus", Keyboard.KEY_J, "§9Jesus"));
        modules.add(new Module("AntiKnockback", Keyboard.KEY_K, "§5AntiKB"));
        modules.add(new Module("ESP", Keyboard.KEY_E, "§dESP"));
        modules.add(new Module("XRay", Keyboard.KEY_X, "§7X-Ray"));
        modules.add(new Module("Scaffold", Keyboard.KEY_G, "§aScaffold"));

        // Lag Fix Modules
        modules.add(new Module("FastRender", Keyboard.KEY_R, "§fFastRender"));
        modules.add(new Module("EntityCulling", Keyboard.KEY_C, "§fEntityCulling"));
        modules.add(new Module("ParticleReducer", Keyboard.KEY_P, "§fParticleReducer"));
        modules.add(new Module("ChunkOptimizer", Keyboard.KEY_O, "§fChunkOptimizer"));
        modules.add(new Module("MemoryCleaner", Keyboard.KEY_M, "§fMemoryCleaner"));

        // RSHIFT to open GUI
        KeyBinding guiKey = new KeyBinding("Open Nyx GUI", Keyboard.KEY_RSHIFT, "Nyx");
        ClientRegistry.registerKeyBinding(guiKey);

        System.out.println("[Nyx] Loaded with 15 features!");
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            guiOpen = !guiOpen;
            Minecraft.getMinecraft().displayGuiScreen(guiOpen ? new ClickGUI() : null);
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (player == null) return;

        for (Module m : modules) {
            if (m.enabled) {
                switch (m.name) {
                    case "Fly":
                        player.capabilities.allowFlying = true;
                        player.capabilities.isFlying = true;
                        break;
                    case "Speed":
                        if (player.isSprinting()) {
                            player.motionX *= 1.2;
                            player.motionZ *= 1.2;
                        }
                        break;
                    case "Fullbright":
                        Minecraft.getMinecraft().gameSettings.gammaSetting = 1000f;
                        break;
                    case "NoFall":
                        if (player.fallDistance > 3) player.fallDistance = 0;
                        break;
                    case "Sprint":
                        player.setSprinting(true);
                        break;
                    case "Jesus":
                        if (player.isInWater()) {
                            player.motionY = 0.1;
                            player.onGround = true;
                        }
                        break;
                    case "FastRender":
                        Minecraft.getMinecraft().gameSettings.renderDistanceChunks = 4;
                        Minecraft.getMinecraft().gameSettings.limitFramerate = 60;
                        break;
                    case "EntityCulling":
                        Minecraft.getMinecraft().gameSettings.entityDistanceScaling = 0.5f;
                        break;
                    case "ParticleReducer":
                        Minecraft.getMinecraft().gameSettings.particleSetting = 2;
                        break;
                    case "ChunkOptimizer":
                        Minecraft.getMinecraft().gameSettings.chunkUpdates = 1;
                        break;
                    case "MemoryCleaner":
                        memoryCleanerTick++;
                        if (memoryCleanerTick % 100 == 0) {
                            System.gc();
                            memoryCleanerTick = 0;
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        if (!isModuleEnabled("Fullbright")) {
            Minecraft.getMinecraft().gameSettings.gammaSetting = 0f;
        }
    }

    private boolean isModuleEnabled(String name) {
        for (Module m : modules) {
            if (m.name.equals(name)) return m.enabled;
        }
        return false;
    }

    static class Module {
        String name;
        boolean enabled;
        KeyBinding keybind;
        String displayName;

        Module(String name, int key, String displayName) {
            this.name = name;
            this.enabled = false;
            this.displayName = displayName;
            this.keybind = new KeyBinding("Toggle " + name, key, "Nyx");
            ClientRegistry.registerKeyBinding(keybind);
        }

        void toggle() {
            enabled = !enabled;
        }
    }

    static class ClickGUI extends GuiScreen {
        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            this.drawDefaultBackground();
            int y = 30;
            mc.fontRendererObj.drawStringWithShadow("§b§lNyx Hack Client §7- §f15 Features", 20, 10, 0xFFFFFF);
            for (Module m : modules) {
                String status = m.enabled ? "§aON" : "§cOFF";
                String text = m.displayName + " §7[" + Keyboard.getKeyName(m.keybind.getKeyCode()) + "] §f" + status;
                mc.fontRendererObj.drawStringWithShadow(text, 20, y, m.enabled ? 0x55FF55 : 0xFF5555);
                y += 20;
            }
            mc.fontRendererObj.drawStringWithShadow("§7Press RSHIFT to close", 20, y + 10, 0x888888);
        }

        @Override
        protected void mouseClicked(int x, int y, int button) {
            int idx = (y - 30) / 20;
            if (idx >= 0 && idx < modules.size()) {
                modules.get(idx).toggle();
            }
        }

        @Override
        public boolean doesGuiPauseGame() {
            return false;
        }
    }
}
