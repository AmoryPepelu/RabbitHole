package github.amorypepelu.clicktrack.util;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.TransformOutputProvider;

import java.io.File;

/**
 * Created by sly on 2019-04-23.
 */
public class GradleUtils {

    public static File getTransformOutputLocation(TransformOutputProvider provider,
                                                  QualifiedContent content) {
        if (content instanceof JarInput) {
            return provider.getContentLocation(
                    content.getName(),
                    content.getContentTypes(),
                    content.getScopes(),
                    Format.JAR);
        }
        if (content instanceof DirectoryInput) {
            return provider.getContentLocation(
                    content.getName(),
                    content.getContentTypes(),
                    content.getScopes(),
                    Format.DIRECTORY);
        }
        return null;
    }
}
