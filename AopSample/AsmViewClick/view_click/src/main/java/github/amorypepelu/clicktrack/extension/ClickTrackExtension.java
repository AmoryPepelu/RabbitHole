package github.amorypepelu.clicktrack.extension;

/**
 * 使用 private + getter setter 会有问题，boolean 类型变量无法识别
 * Created by sly on 2019-04-22.
 */
public class ClickTrackExtension {

    public String name;
    public Boolean isDebug;
    public Boolean enable;

    @Override
    public String toString() {
        return "ClickTrackExtension{" +
                "name='" + name + '\'' +
                ", isDebug=" + isDebug +
                ", enable=" + enable +
                '}';
    }
}
