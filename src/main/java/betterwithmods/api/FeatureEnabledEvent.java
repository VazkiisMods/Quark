package betterwithmods.api;

import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Created by tyler on 5/14/17.
 */
public class FeatureEnabledEvent extends Event {
    private String module;
    private String feature;
    private boolean enabled;

    public FeatureEnabledEvent(String module, String feature, boolean enabled) {
        this.module = module;
        this.feature = feature;
        this.enabled = enabled;
    }

    public String getModule() {
        return module;
    }

    public String getFeature() {
        return feature;
    }

    public boolean isEnabled() {
        return enabled;
    }
}