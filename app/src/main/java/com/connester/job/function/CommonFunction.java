package com.connester.job.function;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.connester.job.R;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.RequestBody;

public class CommonFunction {
    static ProgressDialog progressDialog;

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void PleaseWaitShow(Context context) {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                CommonFunction.dismissDialog();
            }
        }
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
    }

    public static void PleaseWaitShowMessage(String message) {
        try {
            if (progressDialog != null)
                progressDialog.setMessage(message);
        } catch (Exception e) {
            Log.e(LogTag.EXCEPTION, "Update Progress Alert", e);
        }
    }

    public static void dismissDialog() {
        try {
            if (progressDialog != null)
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
        } catch (Exception e) {
        }

    }

    public static File getImgDir() {
        File fileDir = new File(Environment.getExternalStorageDirectory() + "/Badmerc");
        Log.e(LogTag.EXCEPTION, fileDir.getAbsolutePath() + " <<");
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        return fileDir;
    }

    public static Bitmap textAsBitmap(String text) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(10);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setFakeBoldText(true);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.7f); // round
        int height = (int) (baseline + paint.descent() + 0.7f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }


    public static boolean isNetworkConnected(Context context) {
        boolean connected = false;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                connected = true;
            } else {
                connected = false;
                Toast.makeText(context.getApplicationContext(), "Please check your internet connection.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {

        }
        return connected;
    }

    public static void RateUs(Context context) {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }

    public static void RedirectPlayStore(Context context, String packagename) {
        Uri uri = Uri.parse("market://details?id=" + packagename);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + packagename)));
        }
    }

    public static String getNetworkId(Context context) {
        String type;
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //mobile
        NetworkInfo.State mobile = conMan.getNetworkInfo(0).getState();
        //wifi
        NetworkInfo.State wifi = conMan.getNetworkInfo(1).getState();
        if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING) {
            //mobile
            type = "mobile";
        } else if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
            //wifi
            type = "wifi";
        } else {
            type = "null";
        }
        return type;
    }

    public static String byteToScale(long size_bytes) {
        String cnt_size;
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        double size_kb = Double.parseDouble(decimalFormat.format(size_bytes / 1024));
        double size_mb = Double.parseDouble(decimalFormat.format(size_kb / 1024));
        double size_gb = Double.parseDouble(decimalFormat.format(size_mb / 1024));

        if (size_gb > 1) {
            cnt_size = size_gb + " GB";
        } else if (size_mb > 1) {
            cnt_size = size_mb + " MB";
        } else {
            cnt_size = size_kb + " KB";
        }
        return cnt_size;
    }

    public static void copyToClipBoard(Context context, String txt) {
        copyToClipBoard(context, txt, "label");
    }

    public static void copyToClipBoard(Context context, String txt, String label) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, txt);
        if (clipboard == null || clip == null)
            return;
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "Text copied!", Toast.LENGTH_SHORT).show();
    }

    public static String convertSeconds(int seconds) {
        int h = seconds / 3600;
        int m = (seconds % 3600) / 60;
        int s = seconds % 60;
        String sh = (h > 0 ? String.valueOf(h) + " " + "h" : "");
        String sm = (m < 10 && m > 0 && h > 0 ? "0" : "") + (m > 0 ? (h > 0 && s == 0 ? String.valueOf(m) : String.valueOf(m) + " " + "min") : "");
        String ss = (s == 0 && (h > 0 || m > 0) ? "" : (s < 10 && (h > 0 || m > 0) ? "0" : "") + String.valueOf(s) + " " + "sec");
        return sh + (h > 0 ? " " : "") + sm + (m > 0 ? " " : "") + ss;
    }

    @NonNull
    public static RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(
                okhttp3.MultipartBody.FORM, descriptionString);
    }


    public static void ShareAppLink(Context context) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/*");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "http://play.google.com/store/apps/details?id=" + context.getPackageName());
        context.startActivity(Intent.createChooser(sharingIntent, "Share using"));
    }

    public static void ShareMsgInWhatsapp(Context context, String msg, String toNumber) {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("http://api.whatsapp.com/send?phone=+91" + toNumber + "&text=" + msg));
//        Intent sendIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + toNumber + "?body=" + msg));
//        sendIntent.setPackage("com.whatsapp");
        context.startActivity(sendIntent);
        /*Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/*");
        sharingIntent.setPackage("com.whatsapp");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, msg);
        context.startActivity(Intent.createChooser(sharingIntent, "Share using"));*/
    }

    public static void _OpenLink(Context context, String link) {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse(link));
        context.startActivity(sendIntent);
    }

    public static void openBrowseDownload(String link, Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage("com.android.chrome");
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            // Chrome browser presumably not installed and open Kindle Browser
            Intent nonChromeintent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            context.startActivity(nonChromeintent);
        }
    }

    public static void call(Context context, String mobile) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + mobile));
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            _LoadAlert(context, "Call permission is off Please on");
            return;
        }
        context.startActivity(intent);
    }

    public static void mailInApp(Context context, String mail) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:" + mail));
        context.startActivity(emailIntent);
    }

    public static void _LoadFirstFragment(AppCompatActivity appCompatActivity, int resId, Fragment fragment) {
        FragmentTransaction fragmentTransaction = appCompatActivity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(resId, fragment);
        fragmentTransaction.commit();
    }

    public static void _ClearFragmentStack(AppCompatActivity appCompatActivity) {
        FragmentManager fm = appCompatActivity.getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    public static void _BackFragmentRemove(AppCompatActivity appCompatActivity) {
        if (appCompatActivity.getSupportFragmentManager().getBackStackEntryCount() > 0) {
            appCompatActivity.getSupportFragmentManager().popBackStackImmediate();
        }
    }

    public static void loadFragmentWithStack(FragmentActivity fragmentActivity, int layout, Fragment fragment, String fragmentName) {
        FragmentTransaction fragmentTransaction = fragmentActivity.getSupportFragmentManager().beginTransaction();
        //   fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.replace(layout, fragment);
        fragmentTransaction.addToBackStack(fragmentName);
        fragmentTransaction.commitAllowingStateLoss();
    }

    public static void loadFragmentWithStack(FragmentManager fragmentManager, int layout, Fragment fragment, String fragmentName) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //   fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.replace(layout, fragment);
        fragmentTransaction.addToBackStack(fragmentName);
        fragmentTransaction.commitAllowingStateLoss();
    }

    public static AlertDialog _LoadAlert(Context context, String msg) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
        return alertDialog;
    }

    public static String randomAlphaNumeric(int count) {
        String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    public static float getDp(float vl) {
        return (vl * Resources.getSystem().getDisplayMetrics().density + 0.5f);
    }

    public static void setViewHeightBasedOnChildren(View view) {
        int totalHeight = 0;

        view.measure(0, 0);
        totalHeight = view.getMeasuredHeight();

        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = totalHeight;
        view.setLayoutParams(params);

    }

    public static void setGridViewHeightBasedOnChildren(GridView gridView) {
        ListAdapter gridViewAdapter = gridView.getAdapter();
        if (gridViewAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int items = gridViewAdapter.getCount();
        int rows = 1;

        View listItem = gridViewAdapter.getView(0, null, gridView);
        listItem.measure(0, 0);
        totalHeight = listItem.getMeasuredHeight();
        Log.d("rows234", String.valueOf(totalHeight));
        float x = 1;
        if (items > 2) {
            x = items / 2;
            if (items % 2 != 0) {
                rows = (int) (x + 1);
                Log.d("rows", String.valueOf(rows));
            } else {
                rows = (int) (x);
                Log.d("rows", String.valueOf(rows));
            }
            totalHeight *= rows;
            Log.d("rows2", String.valueOf(totalHeight));
        }

        ViewGroup.LayoutParams params = gridView.getLayoutParams();

        params.height = totalHeight + gridView.getPaddingTop() + gridView.getPaddingBottom() + 20;
        gridView.setLayoutParams(params);
    }


    public static String getDeviceName(Context context) {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        String id = Build.ID;
        long time = Build.TIME;
        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        return manufacturer + "-" + model + "-" + id + "-" + time + "-" + android_id;
    }


    public static String getDeviceId(Context context) {
        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        return android_id;
    }

    public static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        char second = s.charAt(1);
        if (Character.isUpperCase(first) && Character.isLowerCase(second)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.toLowerCase().substring(1);
        }
    }

    public static ScrollView OnScrollSetBottomListener(final ScrollView scrollView, final ScrollBottomListener scrollBottomListener) {
        return OnScrollSetBottomListener(scrollView, scrollBottomListener, 0);
    }

    public static ScrollView OnScrollSetBottomListener(final ScrollView scrollView, final ScrollBottomListener scrollBottomListener, int scrollViewExtraHeight) {
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (scrollView.getChildAt(0).getBottom()
                        <= (scrollView.getHeight() + scrollView.getScrollY() + scrollViewExtraHeight)) {
                    //scroll view is at bottom
                    scrollBottomListener.onScrollBottom();
                }
            }
        });
        return scrollView;
    }

    public static boolean verticallyTopBottomShowInView(View videoView, int scrollY, Context context) {
        int maintainPix = 30;
        int displayHeight = getScreenResolution(context)[1];
//        int location[] = new int[2];
//        videoView.getLocationOnScreen(location);
//        int viewX = location[0];
//        int viewY = location[1];
        int viewHeight = videoView.getHeight();
        float positionOnPage = videoView.getY();
        boolean topIn = false;
        boolean bottomIn = false;
//        Log.e(LogTag.TMP_LOG, "ScrollY:" + scrollY + " & location Y:" + viewY + " & View ON Y:" + positionOnPage + " & height:" + viewHeight + " & D-height:" + displayHeight);
        if ((scrollY - maintainPix) > (positionOnPage - displayHeight) && scrollY < (positionOnPage - maintainPix)) {
//            Log.e(LogTag.TMP_LOG, "VIEW TOP INNN....");
            topIn = true;
        }
        if ((scrollY - maintainPix) > (positionOnPage - displayHeight + viewHeight) && scrollY < (positionOnPage - maintainPix + viewHeight)) {
//            Log.e(LogTag.TMP_LOG, "VIEW BOTTOM INNN....");
            bottomIn = true;
        }
        return topIn && bottomIn;
    }

    public static int[] getScreenResolution(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        return new int[]{width, height};
    }

    public static String convertAmountUnitForm(String valueStr) {
        ArrayList<String> rt = new ArrayList<>();
        String sVals[] = valueStr.split("-");
        for (String val : sVals) {
            double value = Double.parseDouble(val);
            int power;
            String suffix = " KMBT";
            String formattedNumber = "";

            NumberFormat formatter = new DecimalFormat("#,###.00");
            power = (int) StrictMath.log10(value);
            value = (value / (Math.pow(10, (power / 3) * 3)));
            formattedNumber = formatter.format(value);
            formattedNumber = formattedNumber + suffix.charAt(power / 3);
//            String avl = formattedNumber.length() > 4 ? formattedNumber.replaceAll("\\.[0-9]+", "") : formattedNumber;
            rt.add(formattedNumber);
        }
        return TextUtils.join("-", rt);
    }

    public static String NumberToWord(String number) {
        String word = "";
        String[] HTLC = {"", "", "K", "Lakh", "Cr"}; // H-hundread , T-Thousand, ..
        int split[] = {0, 2, 3, 5, 7, 9};
        String[] temp = new String[split.length];
        boolean addzero = true;
        int len1 = number.length();
        if (len1 > split[split.length - 1]) {
            return "limit Error";
        }
        for (int l = 1; l < split.length; l++)
            if (number.length() == split[l])
                addzero = false;
        if (addzero == true)
            number = "0" + number;
        int len = number.length();
        int j = 0;
        // spliting & putting numbers in temp array.
        while (split[j] < len) {
            int beg = len - split[j + 1];
            int end = beg + split[j + 1] - split[j];
            temp[j] = number.substring(beg, end);
            j = j + 1;
        }

        for (int k = 0; k < j; k++) {
            if (k >= 1) {
                word = temp[k] + " " + HTLC[k];
            } else
                word = temp[k];
        }
        if (word.startsWith("0")) {
            return word.split("0", 2)[1];
        }
        return (word);
    }

    public static String base64Encode(String str) {
        return Base64.encodeToString(str.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
    }

    public static String base64Decode(String encryptStr) {
        return new String(Base64.decode(encryptStr, Base64.DEFAULT), StandardCharsets.UTF_8);
    }

    public static boolean isEmail(String email) {
        String pttr = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";

        Pattern pattern = Pattern.compile(pttr, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if (matcher.find()) return true;
        else return false;
    }

    public static boolean isMobileNo(String mobileNo) {
        String regex = "\\d{10}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(mobileNo);
        if (matcher.matches()) return true;
        else return false;
    }


    public static String getMimietype(Context context, Uri uri) {
        ContentResolver resolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return MimeTypeMap.getFileExtensionFromUrl(resolver.getType(uri));
    }

    public static String fileType(String url) {
        ArrayList<String> img = new ArrayList<>();
        img.addAll(Arrays.asList(new String[]{"png", "jpg", "bmp", "jpeg", "webp"}));

        ArrayList<String> video = new ArrayList<>();
        video.addAll(Arrays.asList(new String[]{"mp4", "3gp", "avi", "mkv", "mpg"}));

        String ext = url.substring(url.lastIndexOf(".") + 1);
        if (video.indexOf(ext) != -1) {
            return "VIDEO";
        } else if (img.indexOf(ext) != -1) {
            return "IMAGE";
        } else {
            return "DOC";
        }
    }

    public static String getFileExtension(String url) {
        return url.substring(url.lastIndexOf(".") + 1);
    }

    public static String getVersionCode(Context context) {
        try {
            return String.valueOf(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
//        return String.valueOf(BuildConfig.VERSION_CODE);
        return null;
    }

    public static String getVersionName(Context context) {
        try {
            return String.valueOf(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
//        return BuildConfig.VERSION_NAME;
        return null;
    }

    public static String findAgeRange(String birthDate) {
        if (birthDate.equals("0000-00-00")) {
            return "-";
        }
        int i = 0;
        int start = 0;
        int stop = 0;
        int year = 0;
        Date today = new Date();
        Date dobDate = DateUtils.getObjectDate("yyyy-MM-dd", birthDate);
        year = today.getYear() - dobDate.getYear();

        if (year % 5 == 0) {
            year++;
        }
        for (i = year; i <= year + 5; i++) {
            if (i % 5 == 0) {
                stop = i;
                break;
            }
        }
        for (i = year; i >= year - 5; i--) {
            if (i % 5 == 0) {
                start = i;
                break;
            }
        }
        return start + " - " + stop + " years";
    }

    public static void _OpenLinkInMyWebview(Context context, String titleStr, String link) {
        Dialog dialog = new Dialog(context, R.style.Base_Theme_Connester);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.link_open_webview_dialog);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        ImageView back_iv = dialog.findViewById(R.id.back_iv);
        back_iv.setOnClickListener(v -> {
            dialog.dismiss();
        });
        TextView title = dialog.findViewById(R.id.title);
        title.setText(titleStr);
        WebView simple_wv = dialog.findViewById(R.id.simple_wv);
        simple_wv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                PleaseWaitShow(context);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                dismissDialog();
            }
        });
        simple_wv.loadUrl(link);

        dialog.show();
    }
}
