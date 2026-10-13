/*
 * Copyright (c) 2021
 *
 * This file is part of SceneStudio.
 *
 * SceneStudio is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SceneStudio is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with SceneStudio.  If not, see <https://www.gnu.org/licenses/>.
 */
package id.patchedpixel.scenecraft.studio.protocol.packets;

import id.patchedpixel.scenecraft.studio.protocol.Packet;
import id.patchedpixel.scenecraft.studio.protocol.PacketType;
import id.patchedpixel.scenecraft.studio.protocol.PacketTypeRegistry;
import id.patchedpixel.scenecraft.studio.util.DPosition;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.io.IOException;

public class PacketEntityMovement {
    public static Triple<DPosition, Pair<Float, Float>, Boolean> getMovement(Packet packet) throws IOException {
        PacketType type = packet.getType();
        boolean hasPos = type == PacketType.EntityPosition || type == PacketType.EntityPositionRotation;
        boolean hasRot = type == PacketType.EntityRotation || type == PacketType.EntityPositionRotation;
        try (Packet.Reader in = packet.reader()) {
            in.readVarInt(); // entity id

            DPosition pos = null;
            if (hasPos) {
                pos = new DPosition(
                        in.readShort() / 4096.0,
                        in.readShort() / 4096.0,
                        in.readShort() / 4096.0
                );
            }
            Pair<Float, Float> yawPitch = null;
            if (hasRot) {
                yawPitch = Pair.of(
                        in.readByte() / 256f * 360,
                        in.readByte() / 256f * 360
                );
            }
            boolean onGround = true;
            if ((hasPos || hasRot)) {
                onGround = in.readBoolean();
            }
            return Triple.of(pos, yawPitch, onGround);
        }
    }

    public static Packet write(PacketTypeRegistry registry, int entityId, DPosition deltaPos, Pair<Float, Float> yawPitch, boolean onGround) throws IOException {
        if (deltaPos == null && yawPitch == null) {
            // There is no "no movement" packet, a move by zero is the closest equivalent
            deltaPos = new DPosition(0, 0, 0);
        }
        boolean hasPos = deltaPos != null;
        boolean hasRot = yawPitch != null;
        PacketType type;
        if (hasPos) {
            if (hasRot) {
                type = PacketType.EntityPositionRotation;
            } else {
                type = PacketType.EntityPosition;
            }
        } else {
            type = PacketType.EntityRotation;
        }
        Packet packet = new Packet(registry, type);
        try (Packet.Writer out = packet.overwrite()) {
            out.writeVarInt(entityId);

            if (hasPos) {
                out.writeShort((int) (deltaPos.getX() * 4096));
                out.writeShort((int) (deltaPos.getY() * 4096));
                out.writeShort((int) (deltaPos.getZ() * 4096));
            }
            if (hasRot) {
                out.writeByte((int) (yawPitch.getKey() / 360 * 256));
                out.writeByte((int) (yawPitch.getValue() / 360 * 256));
            }
            if ((hasPos || hasRot)) {
                out.writeBoolean(onGround);
            }
        }
        return packet;
    }
}
