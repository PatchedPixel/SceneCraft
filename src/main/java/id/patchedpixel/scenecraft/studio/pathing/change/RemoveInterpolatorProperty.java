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
package id.patchedpixel.scenecraft.studio.pathing.change;

import com.google.common.base.Preconditions;
import id.patchedpixel.scenecraft.studio.pathing.interpolation.Interpolator;
import id.patchedpixel.scenecraft.studio.pathing.path.Timeline;
import id.patchedpixel.scenecraft.studio.pathing.property.Property;
import lombok.NonNull;

/**
 * Removes a property.
 */
public final class RemoveInterpolatorProperty implements Change {
    @NonNull
    public static RemoveInterpolatorProperty create(Interpolator interpolator, Property property) {
        return new RemoveInterpolatorProperty(interpolator, property);
    }

    RemoveInterpolatorProperty(Interpolator interpolator, Property property) {
        this.interpolator = interpolator;
        this.property = property;
    }

    private final Interpolator interpolator;

    private final Property property;

    private boolean applied;

    @Override
    public void apply(Timeline timeline) {
        Preconditions.checkState(!applied, "Already applied!");

        interpolator.unregisterProperty(property);

        applied = true;
    }

    @Override
    public void undo(Timeline timeline) {
        Preconditions.checkState(applied, "Not yet applied!");

        interpolator.registerProperty(property);

        applied = false;
    }
}
