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
package id.patchedpixel.scenecraft.studio.studio;

import id.patchedpixel.scenecraft.studio.PacketData;
import id.patchedpixel.scenecraft.studio.io.ReplayInputStream;
import id.patchedpixel.scenecraft.studio.stream.AbstractPacketStream;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.io.IOException;

public class StudioPacketStream extends AbstractPacketStream {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final ReplayInputStream in;

    public StudioPacketStream(ReplayInputStream in) {
        this.in = in;
    }

    @Override
    protected PacketData nextInput() {
        try {
            return in.readPacket();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void start() {}

    @Override
    protected void cleanup() {
        try {
            in.close();
        } catch (IOException e) {
            LOGGER.error("Failed to close the replay input stream", e);
        }
    }
}
