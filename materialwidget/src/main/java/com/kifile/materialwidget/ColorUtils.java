/*
 * Copyright 2015 kifile
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kifile.materialwidget;

import android.graphics.Color;

/**
 * Created by kifile on 15-1-4.
 */
public class ColorUtils {

    /**
     * Get the value of color with specified alpha.
     *
     * @param color
     * @param alpha between 0 to 255.
     * @return Return the color with specified alpha.
     */
    public static int getColorAtAlpha(int color, int alpha) {
        if (alpha < 0 || alpha > 255) {
            throw new IllegalArgumentException("The alpha should be 0 - 255.");
        }
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }
}
