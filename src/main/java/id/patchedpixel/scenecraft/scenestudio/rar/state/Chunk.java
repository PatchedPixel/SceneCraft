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

package id.patchedpixel.scenecraft.scenestudio.rar.state;

import id.patchedpixel.scenecraft.scenestudio.netio.NetInput;
import id.patchedpixel.scenecraft.scenestudio.netio.NetOutput;
import id.patchedpixel.scenecraft.scenestudio.protocol.PacketTypeRegistry;
import id.patchedpixel.scenecraft.scenestudio.protocol.packets.PacketChunkData;
import id.patchedpixel.scenecraft.scenestudio.protocol.registry.DimensionType;
import id.patchedpixel.scenecraft.scenestudio.rar.PacketSink;
import id.patchedpixel.scenecraft.scenestudio.rar.RandomAccessState;
import id.patchedpixel.scenecraft.scenestudio.rar.cache.ReadableCache;
import id.patchedpixel.scenecraft.scenestudio.rar.cache.WriteableCache;
import id.patchedpixel.scenecraft.scenestudio.rar.containers.BlockStateTree;

import java.io.IOException;

public class Chunk extends TransientThing implements RandomAccessState {
    private final BlockStateTree blocks;

    public Chunk(PacketTypeRegistry registry, NetInput in) throws IOException {
        super(registry, in);
        this.blocks = new BlockStateTree(registry, in.readVarInt());
    }

    @Override
    public void load(PacketSink sink, ReadableCache cache) throws IOException {
        super.load(sink, cache);
        blocks.load(sink, cache);
    }

    @Override
    public void unload(PacketSink sink, ReadableCache cache) throws IOException {
        super.unload(sink, cache);
        blocks.unload(sink, cache);
    }

    @Override
    public void play(PacketSink sink, int currentTimeStamp, int targetTime) throws IOException {
        blocks.play(sink, currentTimeStamp, targetTime);
    }

    @Override
    public void rewind(PacketSink sink, int currentTimeStamp, int targetTime) throws IOException {
        if (currentTimeStamp >= despawnTime) {
            play(sink, spawnTime - 1, targetTime);
            return;
        }
        blocks.rewind(sink, currentTimeStamp, targetTime);
    }

    public static class Builder extends TransientThing.Builder {
        public final BlockStateTree.Builder blocks;

        public Builder(PacketTypeRegistry registry, DimensionType dimensionType, PacketChunkData.Column column) throws IOException {
            addSpawnPacket(PacketChunkData.load(column).write(registry));
            addDespawnPacket(PacketChunkData.unload(column.x, column.z).write(registry));

            blocks = new BlockStateTree.Builder(registry, dimensionType, column);
        }

        @Override
        public void build(NetOutput out, WriteableCache cache) throws IOException {
            super.build(out, cache);

            out.writeVarInt(blocks.build(cache));
        }
    }
}
