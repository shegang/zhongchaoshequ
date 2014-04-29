package com.example.qiumishequouzhan.webviewpage;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.webkit.WebView;

/**
 * Created with IntelliJ IDEA.
 * User: jinxing
 * Date: 14-3-20
 * Time: 上午11:53
 * To change this template use File | Settings | File Templates.
 */
public class MainFragment extends FragmentActivity {
    public static final String EXTRA_VIEW_URL = "com.MarsKingser.EXTRA_VIEW_URL";
    public static final String EXTRA_FRAGMENT = "com.MarsKingser.EXTRA_FRAGMENT";
    public static final String EXTRA_FRAGMENTTITLE = "com.MarsKingser.EXTRA_FRAGMENTTITLE";
    public static final int FRAGMENT_ONEPAGEWEBVIEW = 0;
    public static final int FRAGMENT_EDITPAGEWEBVIEW = 1;

    public static MainFragment obj;
    public static Fragment fragment;

    public static MainFragment GetInstance() {
        return obj;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setTitle(getIntent().getStringExtra(EXTRA_TITLE));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            new ActionBarHelper().setDisplayHomeAsUpEnabled(true);
        }
        // Check what fragment is shown, replace if needed.
        fragment = getSupportFragmentManager().findFragmentById(android.R.id.content);
        if (fragment == null) {
            // Make new fragment to show.
            int fragmentId = getIntent().getIntExtra(EXTRA_FRAGMENT, FRAGMENT_ONEPAGEWEBVIEW);
            String sTitle = getIntent().getStringExtra(EXTRA_FRAGMENTTITLE);
            // String counts = getIntent().getStringExtra("SchEvalCount");
            switch (fragmentId) {
                case FRAGMENT_ONEPAGEWEBVIEW: {
                    String sUrl = getIntent().getStringExtra(EXTRA_VIEW_URL);
                    fragment = OneWebPageView.newInstance();
                    ((OneWebPageView)fragment).SetWebViewUrl(sUrl);
//                    OneWebPageView.SetWebViewUrl(sUrl);
                    if (sTitle != null) {
                        ((OneWebPageView)fragment).SetWebViewTitle(sTitle);
//                        OneWebPageView.SetWebViewTitle(sTitle);
                    }

                }
                break;
                case FRAGMENT_EDITPAGEWEBVIEW: {
                    String sUrl = getIntent().getStringExtra(EXTRA_VIEW_URL);

                    fragment = EditOnePage.newInstance();
                    EditOnePage.SetWebViewUrl(sUrl);
                }
                break;

            }
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, fragment).commit();
        }
        obj = this;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Helper for fix issue VerifyError on Android 1.6. On Android 1.6 virtual machine
     * tries to resolve (verify) getActionBar function, and since there is no such function,
     * Dalvik throws VerifyError.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private class ActionBarHelper {

        /**
         * Set whether home should be displayed as an "up" affordance.
         * Set this to true if selecting "home" returns up by a single level in your UI
         * rather than back to the top level or front page.
         *
         * @param showHomeAsUp true to show the user that selecting home will return one
         *                     level up rather than to the top level of the app.
         */
        private void setDisplayHomeAsUpEnabled(boolean showHomeAsUp) {
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(showHomeAsUp);
            }
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case RESULT_CANCELED:
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    int State = bundle.getInt("ChangeState");
                    switch (State) {
                        case 2:
//                            ((OneWebPageView) fragment).CallBack(bundle.getString("chageurl"));
                            break;
                    }
                }
                break;

            default:
                break;
        }
    }
}
