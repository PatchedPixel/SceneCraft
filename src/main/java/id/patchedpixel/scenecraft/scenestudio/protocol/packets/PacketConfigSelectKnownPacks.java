/*
 * Copyright (c) 2023
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

package id.patchedpixel.scenecraft.scenestudio.protocol.packets;

import id.patchedpixel.scenecraft.scenestudio.protocol.Packet;
import id.patchedpixel.scenecraft.scenestudio.protocol.PacketType;
import id.patchedpixel.scenecraft.scenestudio.protocol.PacketTypeRegistry;
import id.patchedpixel.scenecraft.scenestudio.protocol.data.VersionedResourceLocation;

import java.io.IOException;
import java.util.*;

public class PacketConfigSelectKnownPacks {
    public static List<VersionedResourceLocation> read(Packet packet) throws IOException {
        try (Packet.Reader in = packet.reader()) {
            return in.readList(() -> VersionedResourceLocation.read(in));
        }
    }

    public static Packet write(PacketTypeRegistry registry, List<VersionedResourceLocation> packs) throws IOException {
        if (packs == null) packs = Collections.emptyList();

        Packet packet = new Packet(registry, PacketType.ConfigSelectKnownPacks);
        try (Packet.Writer writer = packet.overwrite()) {
            writer.writeList(packs, entry -> entry.write(writer));
        }
        return packet;
    }
}
