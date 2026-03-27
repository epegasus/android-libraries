package com.sohaib.cameraview.dev_filter;

import androidx.annotation.NonNull;

import com.sohaib.cameraview.CameraView;
import com.sohaib.cameraview.dev_filters.AutoFixFilter;
import com.sohaib.cameraview.dev_filters.BlackAndWhiteFilter;
import com.sohaib.cameraview.dev_filters.BrightnessFilter;
import com.sohaib.cameraview.dev_filters.ContrastFilter;
import com.sohaib.cameraview.dev_filters.CrossProcessFilter;
import com.sohaib.cameraview.dev_filters.DocumentaryFilter;
import com.sohaib.cameraview.dev_filters.DuotoneFilter;
import com.sohaib.cameraview.dev_filters.FillLightFilter;
import com.sohaib.cameraview.dev_filters.GammaFilter;
import com.sohaib.cameraview.dev_filters.GrainFilter;
import com.sohaib.cameraview.dev_filters.GrayscaleFilter;
import com.sohaib.cameraview.dev_filters.HueFilter;
import com.sohaib.cameraview.dev_filters.InvertColorsFilter;
import com.sohaib.cameraview.dev_filters.LomoishFilter;
import com.sohaib.cameraview.dev_filters.PosterizeFilter;
import com.sohaib.cameraview.dev_filters.SaturationFilter;
import com.sohaib.cameraview.dev_filters.SepiaFilter;
import com.sohaib.cameraview.dev_filters.SharpnessFilter;
import com.sohaib.cameraview.dev_filters.TemperatureFilter;
import com.sohaib.cameraview.dev_filters.TintFilter;
import com.sohaib.cameraview.dev_filters.VignetteFilter;

/**
 * Contains commonly used {@link Filter}s.
 * You can use {@link #newInstance()} to create a new instance and
 * pass it to {@link CameraView#setFilter(Filter)}.
 */
public enum Filters {

    /** @see NoFilter */
    NONE(NoFilter.class),

    /** @see AutoFixFilter */
    AUTO_FIX(AutoFixFilter.class),

    /** @see BlackAndWhiteFilter */
    BLACK_AND_WHITE(BlackAndWhiteFilter.class),

    /** @see BrightnessFilter */
    BRIGHTNESS(BrightnessFilter.class),

    /** @see ContrastFilter */
    CONTRAST(ContrastFilter.class),

    /** @see CrossProcessFilter */
    CROSS_PROCESS(CrossProcessFilter.class),

    /** @see DocumentaryFilter */
    DOCUMENTARY(DocumentaryFilter.class),

    /** @see DuotoneFilter */
    DUOTONE(DuotoneFilter.class),

    /** @see FillLightFilter */
    FILL_LIGHT(FillLightFilter.class),

    /** @see GammaFilter */
    GAMMA(GammaFilter.class),

    /** @see GrainFilter */
    GRAIN(GrainFilter.class),

    /** @see GrayscaleFilter */
    GRAYSCALE(GrayscaleFilter.class),

    /** @see HueFilter */
    HUE(HueFilter.class),

    /** @see InvertColorsFilter */
    INVERT_COLORS(InvertColorsFilter.class),

    /** @see LomoishFilter */
    LOMOISH(LomoishFilter.class),

    /** @see PosterizeFilter */
    POSTERIZE(PosterizeFilter.class),

    /** @see SaturationFilter */
    SATURATION(SaturationFilter.class),

    /** @see SepiaFilter */
    SEPIA(SepiaFilter.class),

    /** @see SharpnessFilter */
    SHARPNESS(SharpnessFilter.class),

    /** @see TemperatureFilter */
    TEMPERATURE(TemperatureFilter.class),

    /** @see TintFilter */
    TINT(TintFilter.class),

    /** @see VignetteFilter */
    VIGNETTE(VignetteFilter.class);

    private final Class<? extends Filter> filterClass;

    Filters(@NonNull Class<? extends Filter> filterClass) {
        this.filterClass = filterClass;
    }

    /**
     * Returns a new instance of the given filter.
     * @return a new instance
     */
    @NonNull
    public Filter newInstance() {
        try {
            return filterClass.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            return new NoFilter();
        }
    }
}
