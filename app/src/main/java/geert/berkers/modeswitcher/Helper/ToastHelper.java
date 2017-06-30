package geert.berkers.modeswitcher.Helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import geert.berkers.modeswitcher.R;

/**
 * Created by Zorgkluis (geert).
 */
public class ToastHelper {

    public static Toast makeText(Context context, CharSequence text, int length) {
        return makeImageToast(context, R.mipmap.ic_launcher, text, length);
    }

    @SuppressWarnings("WeakerAccess")
    public static Toast makeImageToast(Context context, int imageResId, CharSequence text, int length) {
        @SuppressLint("ShowToast")
        Toast toast = Toast.makeText(context, text, length);

        View rootView = toast.getView();
        LinearLayout linearLayout = null;
        View messageTextView = null;

        // Check (expected) toast layout
        if (rootView instanceof LinearLayout) {
            linearLayout = (LinearLayout) rootView;

            if (linearLayout.getChildCount() == 1) {
                View child = linearLayout.getChildAt(0);

                if (child instanceof TextView) {
                    messageTextView = child;
                }
            }
        }

        // Cancel modification because toast layout is not what we expected
        if (linearLayout == null || messageTextView == null) {
            return toast;
        }

        ViewGroup.LayoutParams textParams = messageTextView.getLayoutParams();
        ((LinearLayout.LayoutParams) textParams).gravity = Gravity.CENTER_VERTICAL;

        // Convert dip dimension
        float density = context.getResources().getDisplayMetrics().density;
        int imageSize = (int) (density * 25 + 0.5f);
        int imageMargin = (int) (density * 15 + 0.5f);

        // Setup image view layout parameters
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(imageSize, imageSize);
        imageParams.setMargins(0, 0, imageMargin, 0);
        imageParams.gravity = Gravity.CENTER_VERTICAL;

        // Setup image view
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(imageResId);
        imageView.setLayoutParams(imageParams);

        // Modify root layout
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.addView(imageView, 0);

        return toast;
    }

}
