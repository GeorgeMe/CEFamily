package com.yunyan.mall.views.custom_views;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;
/**
 * Created by George on 2015/8/14.
 */
public class LobsterTextView extends TextView {
    public LobsterTextView(Context context) {

        super(context);

        if (!isInEditMode())
            init(context);
    }

    public LobsterTextView(Context context, AttributeSet attrs) {

        super(context, attrs);

        if (!isInEditMode())
            init(context);
    }

    public LobsterTextView(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);

        if (!isInEditMode())
            init(context);

    }

    private void init(Context context) {

        Typeface t = Typeface.createFromAsset(context.getAssets(), "Lobster-Regular.ttf");
        this.setTypeface(t);
    }
}
