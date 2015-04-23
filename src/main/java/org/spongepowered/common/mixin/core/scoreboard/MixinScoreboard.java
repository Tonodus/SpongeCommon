/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.common.mixin.core.scoreboard;

import com.google.common.base.Optional;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.Score;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlot;
import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.common.scoreboard.SpongeDisplaySlot;

import java.util.Map;

@NonnullByDefault
@Mixin(Scoreboard.class)
@Implements(@Interface(iface = org.spongepowered.api.scoreboard.Scoreboard.class, prefix = "scoreboard$"))
public abstract class MixinScoreboard {

    @Shadow public Map scoreObjectives;
    @Shadow public ScoreObjective[] objectiveDisplaySlots;

    public Optional<Objective> scoreboard$getObjective(String name) {
        return Optional.fromNullable((Objective) this.scoreObjectives.get(name));
    }

    public Optional<Objective> scoreboard$getObjective(DisplaySlot slot) {
        return Optional.fromNullable((Objective) (this.objectiveDisplaySlots[((SpongeDisplaySlot) slot).getIndex()]));
    }

    public void scoreboard$addObjective(Objective objective, DisplaySlot displaySlot) {
        for (int i = 0; i < this.objectiveDisplaySlots.length; i++) {
            if (this.objectiveDisplaySlots[i] == objective) {
                this.objectiveDisplaySlots[i] = null;
                break;
            }
        }
        this.objectiveDisplaySlots[((SpongeDisplaySlot) displaySlot).getIndex() + 3] = (ScoreObjective) objective;

        (IMixinOb)
    }
}
