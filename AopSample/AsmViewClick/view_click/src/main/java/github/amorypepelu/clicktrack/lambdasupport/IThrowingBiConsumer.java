package github.amorypepelu.clicktrack.lambdasupport;

import java.util.function.BiConsumer;

/**
 * Created by sly on 2019-04-24.
 */
public interface IThrowingBiConsumer<T, U> extends BiConsumer<T, U> {
    @Override
    default void accept(T t, U u) {
        try {
            apply(t, u);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void apply(T t, U u) throws Exception;
}
