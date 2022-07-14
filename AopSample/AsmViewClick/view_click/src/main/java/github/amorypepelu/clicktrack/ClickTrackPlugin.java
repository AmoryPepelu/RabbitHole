package github.amorypepelu.clicktrack;

import com.android.build.gradle.AppExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import github.amorypepelu.clicktrack.config.AsmConfig;
import github.amorypepelu.clicktrack.extension.ClickTrackExtension;
import github.amorypepelu.clicktrack.transform.ClickTrackTransform;
import github.amorypepelu.clicktrack.util.LogUtil;

/**
 * Created by sly on 2019-04-19.
 */
public class ClickTrackPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getExtensions().create("clickTrackExtension", ClickTrackExtension.class);

        AppExtension appExtension = project.getExtensions().findByType(AppExtension.class);
        if (appExtension != null) {
            appExtension.registerTransform(new ClickTrackTransform());
        }

        project.afterEvaluate(proj -> {
            Object obj = project.getExtensions().findByName("clickTrackExtension");
            LogUtil.i("clickTrackExtension=" + obj);
            if (obj instanceof ClickTrackExtension) {
                ClickTrackExtension clickTrackExt = (ClickTrackExtension) obj;
                AsmConfig.ext = clickTrackExt;
                LogUtil.setIsLogOpen(clickTrackExt.isDebug);
            }
        });
    }
}
