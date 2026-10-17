/*
 * Copyright (c) 2021
 *
 * This file is part of ReplayStudio.
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
package id.patchedpixel.scenecraft.scenestudio.protocol.packets;

import id.patchedpixel.scenecraft.scenestudio.protocol.Packet;
import id.patchedpixel.scenecraft.scenestudio.protocol.PacketType;
import id.patchedpixel.scenecraft.scenestudio.protocol.PacketTypeRegistry;

import java.io.IOException;

public class PacketEntityHeadLook {
    public static float getYaw(Packet packet) throws IOException {
        try (Packet.Reader in = packet.reader()) {
            in.readVarInt(); // entity id

            return in.readByte() / 256f * 360;
        }
    }

    public static Packet write(PacketTypeRegistry registry, int entityId, float yaw) throws IOException {
        Packet packet = new Packet(registry, PacketType.EntityHeadLook);
        try (Packet.Writer out = packet.overwrite()) {
            out.writeVarInt(entityId);

            out.writeByte((int) (yaw / 360 * 256));
        }
        return packet;
    }
}
