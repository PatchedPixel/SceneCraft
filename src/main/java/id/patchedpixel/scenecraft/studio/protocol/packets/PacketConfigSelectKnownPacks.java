/*
 * Copyright (c) 2023
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
import id.patchedpixel.scenecraft.studio.protocol.data.VersionedIdentifier;

import java.io.IOException;
import java.util.*;

public class PacketConfigSelectKnownPacks {
    public static List<VersionedIdentifier> read(Packet packet) throws IOException {
        try (Packet.Reader in = packet.reader()) {
            return in.readList(() -> VersionedIdentifier.read(in));
        }
    }

    public static Packet write(PacketTypeRegistry registry, List<VersionedIdentifier> packs) throws IOException {
        if (packs == null) packs = Collections.emptyList();

        Packet packet = new Packet(registry, PacketType.ConfigSelectKnownPacks);
        try (Packet.Writer writer = packet.overwrite()) {
            writer.writeList(packs, entry -> entry.write(writer));
        }
        return packet;
    }
}
