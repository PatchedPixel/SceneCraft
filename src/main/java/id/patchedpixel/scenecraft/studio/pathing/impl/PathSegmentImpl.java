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
package id.patchedpixel.scenecraft.studio.pathing.impl;

import id.patchedpixel.scenecraft.studio.pathing.interpolation.Interpolator;
import id.patchedpixel.scenecraft.studio.pathing.path.Keyframe;
import id.patchedpixel.scenecraft.studio.pathing.path.Path;
import id.patchedpixel.scenecraft.studio.pathing.path.PathSegment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PathSegmentImpl implements PathSegment {
    private final Path path;
    private final Keyframe startKeyframe;
    private final Keyframe endKeyframe;
    private Interpolator interpolator;

    public PathSegmentImpl(Path path, Keyframe startKeyframe, Keyframe endKeyframe, Interpolator interpolator) {
        this.path = path;
        this.startKeyframe = startKeyframe;
        this.endKeyframe = endKeyframe;
        setInterpolator(interpolator);
    }

    @Override
    public void setInterpolator(Interpolator interpolator) {
        if (this.interpolator != null) {
            this.interpolator.removeSegment(this);
        }
        this.interpolator = interpolator;
        if (this.interpolator != null) {
            this.interpolator.addSegment(this);
        }
    }
}
