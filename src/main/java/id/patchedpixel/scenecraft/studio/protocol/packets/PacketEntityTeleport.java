/*
 * Copyright (c) 2021
 *
 * This file is part of ReplayStudio.
 *
 * ReplayStudio is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ReplayStudio is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ReplayStudio.  If not, see <https://www.gnu.org/licenses/>.
 */
package id.patchedpixel.scenecraft.studio.protocol.packets;

import id.patchedpixel.scenecraft.studio.protocol.Packet;
import id.patchedpixel.scenecraft.studio.protocol.PacketType;
import id.patchedpixel.scenecraft.studio.protocol.PacketTypeRegistry;
import id.patchedpixel.scenecraft.studio.util.Location;

import java.io.IOException;

public class PacketEntityTeleport {
    public static Location getLocation(Packet packet) throws IOException {
        try (Packet.Reader in = packet.reader()) {
            in.readVarInt(); // entity id

            double x = in.readDouble();
            double y = in.readDouble();
            double z = in.readDouble();

            in.readDouble(); // unused
            in.readDouble(); // unused
            in.readDouble(); // unused

            float yaw = in.readFloat();
            float pitch = in.readFloat();
            return new Location(x, y, z, yaw, pitch);
        }
    }

    public static Packet write(PacketTypeRegistry registry, int entityId, Location location, boolean onGround) throws IOException {
        Packet packet = new Packet(registry, PacketType.EntityTeleport);
        try (Packet.Writer out = packet.overwrite()) {
            out.writeVarInt(entityId);

            out.writeDouble(location.getX());
            out.writeDouble(location.getY());
            out.writeDouble(location.getZ());

            out.writeDouble(0); // unused
            out.writeDouble(0); // unused
            out.writeDouble(0); // unused

            out.writeFloat(location.getYaw());
            out.writeFloat(location.getPitch());

            // Set<Relative> relatives (int bit mask). Required since 1.21.2: 0 means all values are absolute.
            out.writeInt(0);
            out.writeBoolean(onGround);
        }

        return packet;
    }
}
