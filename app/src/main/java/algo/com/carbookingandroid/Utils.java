package algo.com.carbookingandroid;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;

/**
 * Created by heinhtetaung on 30/7/18.
 */

public class Utils {


    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static AlertDialog getLoadingDialog(Context context){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(null);
        alertDialog.setMessage(context.getString(R.string.loading_txt));
        return alertDialog;
    }

}
