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
package id.patchedpixel.scenecraft.scenestudio.pathing.change;

import com.google.common.base.Preconditions;
import id.patchedpixel.scenecraft.scenestudio.pathing.interpolation.Interpolator;
import id.patchedpixel.scenecraft.scenestudio.pathing.path.Timeline;
import id.patchedpixel.scenecraft.scenestudio.pathing.property.Property;
import lombok.NonNull;

/**
 * Adds a new property.
 */
public final class AddInterpolatorProperty implements Change {
    @NonNull
    public static AddInterpolatorProperty create(Interpolator interpolator, Property property) {
        return new AddInterpolatorProperty(interpolator, property);
    }

    AddInterpolatorProperty(Interpolator interpolator, Property property) {
        this.interpolator = interpolator;
        this.property = property;
    }

    private final Interpolator interpolator;

    private final Property property;

    private boolean applied;

    @Override
    public void apply(Timeline timeline) {
        Preconditions.checkState(!applied, "Already applied!");

        interpolator.registerProperty(property);

        applied = true;
    }

    @Override
    public void undo(Timeline timeline) {
        Preconditions.checkState(applied, "Not yet applied!");

        interpolator.unregisterProperty(property);

        applied = false;
    }
}
