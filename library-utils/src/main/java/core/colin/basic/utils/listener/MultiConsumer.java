package core.colin.basic.utils.listener;

/**
 */
public interface MultiConsumer<T, F> {

    void accept(T t, F f);
}
