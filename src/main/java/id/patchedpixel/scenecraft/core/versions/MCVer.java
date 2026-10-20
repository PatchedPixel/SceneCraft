package id.patchedpixel.scenecraft.core.versions;

import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.systems.RenderSystem;
import id.patchedpixel.scenecraft.scenestudio.protocol.State;
import id.patchedpixel.scenecraft.scenestudio.protocol.CurrentProtocol;
import id.patchedpixel.scenecraft.scenestudio.protocol.PacketTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.CrashReportCategory;
import net.minecraft.resources.Identifier;
import net.minecraft.client.gui.Font;
import java.util.concurrent.Callable;
import java.util.Objects;
import net.minecraft.client.gui.screens.Screen;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.util.Util;
import net.minecraft.world.phys.Vec3;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.AbstractWidget;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.network.chat.Component;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.particle.Particle;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.sdl.SDLScancode;

import java.io.File;
import java.net.URI;
import java.util.Optional;

/**
 * Abstraction over things that have changed between different MC versions.
 */
public class MCVer {
    public static int getProtocolVersion() {
        return CurrentProtocol.get();
    }

    public static State fromMc(ConnectionProtocol mcState) {
        switch (mcState) {
            case HANDSHAKING: return State.HANDSHAKE;
            case STATUS: return State.STATUS;
            case LOGIN: return State.LOGIN;
            case CONFIGURATION: return State.CONFIGURATION;
            case PLAY: return State.PLAY;
        }
        throw new IllegalArgumentException("Unexpected value: " + mcState);
    }

    public static PacketTypeRegistry getPacketTypeRegistry(ConnectionProtocol state) {
        return getPacketTypeRegistry(fromMc(state));
    }

    public static PacketTypeRegistry getPacketTypeRegistry(State state) {
        return PacketTypeRegistry.get(state);
    }

    public static PacketTypeRegistry getPacketTypeRegistry(boolean loginPhase) {
        return PacketTypeRegistry.get(loginPhase ? State.LOGIN : State.PLAY);
    }

    public static void resizeMainWindow(Minecraft mc, int width, int height) {
        Window window = mc.getWindow();
        window.onFramebufferResize(width, height);
        window.framebufferWidth = width;
        window.framebufferHeight = height;
    }

    /**
     * Whether the active renderpearl backend is OpenGL (as opposed to Vulkan).
     * Used to gate features which only exist for OpenGL (e.g. Iris based ODS rendering).
     */
    public static boolean isOpenGlBackend() {
        try {
            return "OpenGL".equals(RenderSystem.getDevice().getDeviceInfo().backendName());
        } catch (Throwable t) {
            return false;
        }
    }

    public static Minecraft getMinecraft() {
        return Minecraft.getInstance();
    }

    public static void addButton(
            Screen screen,
            Button button
    ) {
        screen.addRenderableWidget(button);
    }

    public static class ScissorBounds {
        public final int x;
        public final int y;
        public final int width;
        public final int height;

        public ScissorBounds(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ScissorBounds that = (ScissorBounds) o;
            return x == that.x && y == that.y && width == that.width && height == that.height;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, width, height);
        }
    }

    public static Window newScaledResolution(Minecraft mc) {
        return mc.getWindow();
    }

    public static void addDetail(CrashReportCategory category, String name, Callable<String> callable) {
        category.setDetail(name, callable::call);
    }

    public static Font getFontRenderer() {
        return getMinecraft().font;
    }

    public static void setClipboardString(String text) {
        getMinecraft().keyboardHandler.setClipboard(text);
    }

    public static String getClipboardString() {
        return getMinecraft().keyboardHandler.getClipboard();
    }

    /**
     * Tells the game that the given element starts or stops receiving text input.
     * With SDL this is required for typed characters to be delivered at all.
     */
    public static void onTextInputFocusChange(GuiEventListener element, boolean focused) {
        getMinecraft().onTextInputFocusChange(element, focused);
    }

    public static Component literalText(String str) {
        return Component.literal(str);
    }

    public static Identifier identifier(String id) {
        return Identifier.parse(id);
    }

    public static Identifier identifier(String namespace, String path) {
        return Identifier.fromNamespaceAndPath(namespace, path);
    }

    public static Optional<AbstractWidget> findButton(Iterable<? extends GuiEventListener> buttonList, @SuppressWarnings("unused") String text, @SuppressWarnings("unused") int id) {
        final Component message = Component.translatable(text);
        for (GuiEventListener e : buttonList) {
            if (e instanceof ContainerEventHandler) {
                Optional<AbstractWidget> button = findButton(((ContainerEventHandler) e).children(), text, id);
                if (button.isPresent()) {
                    return button;
                }
            }
            if (!(e instanceof AbstractWidget)) {
                continue;
            }
            AbstractWidget b = (AbstractWidget) e;
            if (message.equals(b.getMessage())) {
                return Optional.of(b);
            }
            // Fuzzy match (copy does not include children)
            if (b.getMessage() != null && b.getMessage().plainCopy().equals(message)) {
                return Optional.of(b);
            }
        }
        return Optional.empty();
    }

    /** SDL3 event polling (replaces glfwPollEvents). */
    public static void pollEvents() {
        ((MinecraftMethodAccessor) getMinecraft()).sceneCraftPollEvents();
    }

    /** Flushes and re-polls the SDL input events. */
    public static void pumpEvents() {
        ((MinecraftMethodAccessor) getMinecraft()).sceneCraftPumpEvents();
    }

    public static void processKeyBinds() {
        ((MinecraftMethodAccessor) getMinecraft()).sceneCraftProcessKeyBinds();
    }

    public interface MinecraftMethodAccessor {
        void sceneCraftProcessKeyBinds();
        void sceneCraftExecuteTaskQueue();
        void sceneCraftPollEvents();
        void sceneCraftPumpEvents();
    }

    public static long milliTime() {
        return Util.getMillis();
    }

    public static Vec3 getPosition(Particle particle, float partialTicks) {
        double x = particle.xo + (particle.x - particle.xo) * partialTicks;
        double y = particle.yo + (particle.y - particle.yo) * partialTicks;
        double z = particle.zo + (particle.z - particle.zo) * partialTicks;
        return new Vec3(x, y, z);
    }

    public static void openFile(File file) {
        Blaze3D.openPath(file.toPath());
    }

    public static void openURL(URI url) {
        Blaze3D.openUri(url);
    }

    public static void pushMatrix() {
        RenderSystem.getModelViewStack().pushMatrix();
    }

    public static void popMatrix() {
        RenderSystem.getModelViewStack().popMatrix();
    }

    public static Quaternionf quaternion(float angle, Vector3f axis) {
        return new Quaternionf().fromAxisAngleDeg(axis.x, axis.y, axis.z, angle);
    }

    public static void emitLine(PoseStack.Pose matrixStack, VertexConsumer buffer, Vector2f p1, Vector2f p2, int color, float lineWidth) {
        emitLine(matrixStack, buffer, new Vector3f(p1.x, p1.y, 0), new Vector3f(p2.x, p2.y, 0), color, lineWidth);
    }

    public static void emitLine(PoseStack.Pose matrixStack, VertexConsumer buffer, Vector3f p1, Vector3f p2, int color, float lineWidth) {
        int r = color >> 24 & 0xff;
        int g = color >> 16 & 0xff;
        int b = color >> 8 & 0xff;
        int a = color & 0xff;
        Vector3f n = new Vector3f(p2).sub(p1);
        buffer.addVertex(matrixStack, p1.x, p1.y, p1.z)
                .setLineWidth(lineWidth)
                .setColor(r, g, b, a)
                .setNormal(n.x, n.y, n.z);
        buffer.getClass();
        buffer.addVertex(matrixStack, p2.x, p2.y, p2.z)
                .setLineWidth(lineWidth)
                .setColor(r, g, b, a)
                .setNormal(n.x, n.y, n.z);
        buffer.getClass();
    }

    public static abstract class Keyboard {
        public static final int KEY_ESCAPE = InputConstants.KEY_ESCAPE;
        public static final int KEY_LCONTROL = InputConstants.KEY_LCONTROL;
        public static final int KEY_RCONTROL = InputConstants.KEY_RCONTROL;
        public static final int KEY_LSUPER = InputConstants.KEY_LGUI;
        public static final int KEY_RSUPER = InputConstants.KEY_RGUI;
        public static final int LEFT_CTRL = Util.getPlatform() == Util.OS.OSX ? KEY_LSUPER : KEY_LCONTROL;
        public static final int RIGHT_CTRL = Util.getPlatform() == Util.OS.OSX ? KEY_RSUPER : KEY_RCONTROL;
        public static final int KEY_LSHIFT = InputConstants.KEY_LSHIFT;
        public static final int KEY_DELETE = InputConstants.KEY_DELETE;
        public static final int KEY_F1 = InputConstants.KEY_F1;
        public static final int KEY_A = InputConstants.KEY_A;
        public static final int KEY_B = InputConstants.KEY_B;
        public static final int KEY_C = InputConstants.KEY_C;
        public static final int KEY_H = InputConstants.KEY_H;
        public static final int KEY_I = InputConstants.KEY_I;
        public static final int KEY_J = InputConstants.KEY_J;
        public static final int KEY_K = InputConstants.KEY_K;
        public static final int KEY_L = InputConstants.KEY_L;
        public static final int KEY_M = InputConstants.KEY_M;
        public static final int KEY_N = InputConstants.KEY_N;
        public static final int KEY_O = InputConstants.KEY_O;
        public static final int KEY_P = InputConstants.KEY_P;
        public static final int KEY_Q = InputConstants.KEY_Q;
        public static final int KEY_V = InputConstants.KEY_V;
        public static final int KEY_X = InputConstants.KEY_X;
        public static final int KEY_Y = InputConstants.KEY_Y;
        public static final int KEY_Z = InputConstants.KEY_Z;

        public static boolean isKeyDown(int keyCode) {
            return InputConstants.isKeyDown(keyCode);
        }
    }
}
