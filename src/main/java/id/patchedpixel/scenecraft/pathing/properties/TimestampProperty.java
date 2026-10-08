package id.patchedpixel.scenecraft.pathing.properties;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import id.patchedpixel.scenecraft.replay.ReplayHandler;
import id.patchedpixel.scenecraft.replay.ReplaySender;
import id.patchedpixel.scenecraft.studio.pathing.property.AbstractProperty;
import id.patchedpixel.scenecraft.studio.pathing.property.PropertyPart;
import id.patchedpixel.scenecraft.studio.pathing.property.PropertyParts;
import id.patchedpixel.scenecraft.gui.utils.NonNull;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

/**
 * Property for the time in the replay.
 */
public class TimestampProperty extends AbstractProperty<Integer> {
    public static final TimestampProperty PROPERTY = new TimestampProperty();
    public final PropertyPart<Integer> TIME = new PropertyParts.ForInteger(this, true);
    private TimestampProperty() {
        super("timestamp", "scenecraft.gui.editkeyframe.timestamp", null, 0);
    }

    @Override
    public Collection<PropertyPart<Integer>> getParts() {
        return Collections.singletonList(TIME);
    }

    @Override
    public void applyToGame(Integer value, @NonNull Object replayHandler) {
        ReplaySender replaySender = ((ReplayHandler) replayHandler).getReplaySender();
        if (replaySender.isAsyncMode()) {
            replaySender.jumpToTime(value);
        } else {
            replaySender.sendPacketsTill(value);
        }
    }

    @Override
    public void toJson(JsonWriter writer, Integer value) throws IOException {
        writer.value(value);
    }

    @Override
    public Integer fromJson(JsonReader reader) throws IOException {
        return reader.nextInt();
    }
}
