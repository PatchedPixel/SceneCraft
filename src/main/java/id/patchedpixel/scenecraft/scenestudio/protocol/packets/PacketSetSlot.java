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

import java.io.IOException;

public class PacketSetSlot {
    public static int getWindowId(Packet packet) throws IOException {
        try (Packet.Reader in = packet.reader()) {
            return in.readUnsignedByte();
        }
    }

    public static int getSlot(Packet packet) throws IOException {
        try (Packet.Reader in = packet.reader()) {
            in.readUnsignedByte(); // window id
            in.readVarInt(); // revision

            return in.readShort();
        }
    }
}
