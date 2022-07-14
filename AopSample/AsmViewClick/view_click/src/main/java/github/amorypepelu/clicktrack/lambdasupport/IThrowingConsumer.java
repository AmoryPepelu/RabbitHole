package github.amorypepelu.clicktrack.lambdasupport;

import java.util.function.Consumer;

/**
 * 避免在lambda里面抛出异常
 */
public interface IThrowingConsumer<T> extends Consumer<T> {

    @Override
    default void accept(T t) {
        try {
            apply(t);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void apply(T t) throws Exception;
}
