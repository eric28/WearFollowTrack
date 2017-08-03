package eric.esteban28.wearfollowtrack;

import android.content.Context;
import android.support.wearable.view.SwipeDismissFrameLayout;
import android.util.AttributeSet;
import android.view.View;

public class MySwipeDismissFrameLayout extends SwipeDismissFrameLayout {
    public MySwipeDismissFrameLayout(Context context) {
        super(context);
    }

    public MySwipeDismissFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MySwipeDismissFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, float dx, float x, float y) {

        return true;
    }
}
