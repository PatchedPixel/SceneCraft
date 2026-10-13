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

package id.patchedpixel.scenecraft.scenestudio.rar;

import id.patchedpixel.scenecraft.scenestudio.rar.cache.ReadableCache;

import java.io.IOException;

public interface RandomAccessState {
    void load(PacketSink sink, ReadableCache cache) throws IOException;
    void unload(PacketSink sink, ReadableCache cache) throws IOException;

    void play(PacketSink sink, int currentTimeStamp, int targetTime) throws IOException;
    void rewind(PacketSink sink, int currentTimeStamp, int targetTime) throws IOException;

    default void playOrRewind(PacketSink sink, int currentTimeStamp, int targetTime) throws IOException {
        if (currentTimeStamp <= targetTime) {
            play(sink, currentTimeStamp, targetTime);
        } else {
            rewind(sink, currentTimeStamp, targetTime);
        }
    }
}
