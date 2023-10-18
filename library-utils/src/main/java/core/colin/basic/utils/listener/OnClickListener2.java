package core.colin.basic.utils.listener;

import android.view.View;

import core.colin.basic.utils.click.ClickHelper;

/**
 */
public abstract class OnClickListener2 implements View.OnClickListener {

    @Override
    public final void onClick(final View v) {
        ClickHelper.onlyFirstSameView(v, new ClickHelper.Callback() {
            @Override
            public void onClick(View view) {
                onClick2(view);
            }
        });
    }

    public abstract void onClick2(View v);
}
