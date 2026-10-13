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

package id.patchedpixel.scenecraft.studio.rar.containers;

import id.patchedpixel.scenecraft.studio.netio.NetInput;
import id.patchedpixel.scenecraft.studio.netio.NetOutput;
import id.patchedpixel.scenecraft.studio.protocol.Packet;
import id.patchedpixel.scenecraft.studio.protocol.PacketTypeRegistry;
import id.patchedpixel.scenecraft.studio.rar.PacketSink;

import java.io.IOException;

import static id.patchedpixel.scenecraft.studio.util.Utils.readCompressedPacket;
import static id.patchedpixel.scenecraft.studio.util.Utils.writeCompressedPacket;

public class PacketStateTree extends FullStateTree<Packet>  {
    private final PacketTypeRegistry registry;

    public PacketStateTree(PacketTypeRegistry registry, int index) {
        super(index);
        this.registry = registry;
    }

    @Override
    protected Packet read(NetInput in) throws IOException {
        return readCompressedPacket(registry, in);
    }

    @Override
    protected void discard(Packet value) {
        value.release();
    }

    @Override
    protected void apply(PacketSink sink, Packet value) throws IOException {
        sink.accept(value.retain());
    }

    public static class Builder extends FullStateTree.Builder<Packet> {
        @Override
        protected void write(NetOutput out, Packet value, int time) throws IOException {
            writeCompressedPacket(out, value);
            value.release();
        }

        @Override
        protected void discard(Packet value) {
            value.release();
        }
    }
}
