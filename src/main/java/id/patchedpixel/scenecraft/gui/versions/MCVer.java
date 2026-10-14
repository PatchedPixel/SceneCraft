package id.patchedpixel.scenecraft.gui.versions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.CrashReportCategory;
import com.mojang.blaze3d.platform.Window;

import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * Abstraction over things that have changed between different MC versions.
 */
public class MCVer {
    public static Minecraft getMinecraft() {
        return Minecraft.getInstance();
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
}
