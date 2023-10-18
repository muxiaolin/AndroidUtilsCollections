package core.colin.basic.utils.listener;

/**
 *
 */
public interface SimpleDataCallback<E> {

    void onSuccess(E data);

    void onFailure(String errorMsg);
}
