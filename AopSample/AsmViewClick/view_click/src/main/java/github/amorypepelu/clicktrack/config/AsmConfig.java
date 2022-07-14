package github.amorypepelu.clicktrack.config;

import github.amorypepelu.clicktrack.extension.ClickTrackExtension;

/**
 * Created by sly on 2019-04-19.
 */
public class AsmConfig {
    public static ClickTrackExtension ext = new ClickTrackExtension();

    public static final String Annotation_Class = "Lio/github/pepelu/track_util/TrackViewMethod;";
    public static final String Hooker_Class = "io/github/pepelu/track_util/TrackViewOnClickHelper";
}
