/*
 *   Copyright 2012 Hai Bison
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.ericyl.themedemo.ui.activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.ericyl.themedemo.BuildConfig;
import com.ericyl.themedemo.ui.widget.LockPatternUtils;
import com.ericyl.themedemo.ui.widget.LockPatternView;
import com.ericyl.themedemo.ui.widget.LockPatternView.Cell;
import com.ericyl.themedemo.ui.widget.LockPatternView.DisplayMode;
import com.ericyl.themedemo.util.IEncrypter;
import com.ericyl.themedemo.util.InvalidEncrypterException;
import com.ericyl.themedemo.util.LoadingView;
import com.ericyl.themedemo.util.Settings.Display;
import com.ericyl.themedemo.util.Settings.Security;
import com.ericyl.themedemo.util.UI;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.text.format.DateUtils.SECOND_IN_MILLIS;
import static com.ericyl.themedemo.BuildConfig.DEBUG;
import static com.ericyl.themedemo.util.Settings.Display.METADATA_CAPTCHA_WIRED_DOTS;
import static com.ericyl.themedemo.util.Settings.Display.METADATA_MAX_RETRIES;
import static com.ericyl.themedemo.util.Settings.Display.METADATA_MIN_WIRED_DOTS;
import static com.ericyl.themedemo.util.Settings.Display.METADATA_STEALTH_MODE;
import static com.ericyl.themedemo.util.Settings.Security.METADATA_AUTO_SAVE_PATTERN;
import static com.ericyl.themedemo.util.Settings.Security.METADATA_ENCRYPTER_CLASS;

/**
 * Main activity for this library.
 * <p>
 * You can deliver result to {@link PendingIntent}'s and/ or
 * {@link ResultReceiver} too. See {@link #EXTRA_PENDING_INTENT_OK},
 * {@link #EXTRA_PENDING_INTENT_CANCELLED} and {@link #EXTRA_RESULT_RECEIVER}
 * for more details.
 * </p>
 * <p/>
 * <h1>NOTES</h1>
 * <ul>
 * <li>
 * You must use one of built-in actions when calling this activity. They start
 * with {@code ACTION_*}. Otherwise the library might behave strangely (we don't
 * cover those cases).</li>
 * <li>You must use one of the themes that this library supports. They start
 * with {@code R.style.Alp_42447968_Theme_*}. The reason is the themes contain
 * resources that the library needs.</li>
 * <li>With {@link #ACTION_COMPARE_PATTERN}, there are <b><i>4 possible result
 * codes</i></b>: {@link android.app.Activity#RESULT_OK}, {@link android.app.Activity#RESULT_CANCELED},
 * {@link #RESULT_FAILED} and {@link #RESULT_FORGOT_PATTERN}.</li>
 * <li>With {@link #ACTION_VERIFY_CAPTCHA}, there are <b><i>3 possible result
 * codes</i></b>: {@link android.app.Activity#RESULT_OK}, {@link android.app.Activity#RESULT_CANCELED},
 * and {@link #RESULT_FAILED}.</li>
 * </ul>
 *
 * @author Hai Bison
 * @since v1.0
 */
public class LockPatternActivity extends AppCompatActivity {

    private ActionBar mActionBar;
    private Toolbar mToolbar;

    private static final String CLASSNAME = LockPatternActivity.class.getName();

    /**
     * Use this action to create new pattern. You can provide an
     * {@link IEncrypter} with
     * {@link Security#setEncrypterClass(Context, Class)} to
     * improve security.
     * <p/>
     * If the user created a pattern, {@link android.app.Activity#RESULT_OK} returns with
     * the pattern ({@link #EXTRA_PATTERN}). Otherwise
     * {@link android.app.Activity#RESULT_CANCELED} returns.
     *
     * @see #EXTRA_PENDING_INTENT_OK
     * @see #EXTRA_PENDING_INTENT_CANCELLED
     * @since v2.4 beta
     */
    public static final String ACTION_CREATE_PATTERN = CLASSNAME
            + ".create_pattern";

    /**
     * Creates new intent with {@link #ACTION_CREATE_PATTERN}. You must call
     * this intent from a UI thread.
     *
     * @param context the context.
     * @return new intent.
     */
    public static Intent newIntentToCreatePattern(Context context) {
        Intent result = new Intent(ACTION_CREATE_PATTERN, null, context,
                LockPatternActivity.class);
        return result;
    }// newIntentToCreatePattern()

    /**
     * This method is a shortcut to call
     * {@link #newIntentToCreatePattern(Context)} from a UI thread.
     *
     * @param caller      must be an instance of {@link android.app.Activity}, or {@link android.app.Fragment}
     *                    or support library's {@code Fragment}. Other values will be
     *                    ignored.
     * @param context     the context.
     * @param requestCode request code for
     *                    {@link android.app.Activity#startActivityForResult(Intent, int)} or
     *                    counterpart methods of fragments.
     * @return {@code true} if the call has been made successfully,
     * {@code false} if any exception occurred.
     * @throws NullPointerException if caller or context is {@code null}.
     */
    public static boolean startToCreatePattern(Object caller, Context context,
                                               int requestCode) {
        return callStartActivityForResult(caller,
                newIntentToCreatePattern(context), requestCode);
    }// startToCreatePattern()

    /**
     * Calls {@code startActivityForResult(Intent, int)} from given caller,
     * ignores any exception.
     *
     * @param caller      the caller.
     * @param intent      the intent.
     * @param requestCode request code.
     * @return {@code true} if the call has been made successfully,
     * {@code false} if any exception occurred.
     * @throws NullPointerException if caller or intent is {@code null}.
     */
    public static boolean callStartActivityForResult(Object caller,
                                                     Intent intent, int requestCode) {
        try {
            Method method = caller.getClass().getMethod(
                    "startActivityForResult", Intent.class, int.class);
            method.setAccessible(true);
            method.invoke(caller, intent, requestCode);

            return true;
        } catch (Exception e) {
            /*
             * Just log it. We don't need to go to details here, as it's
             * responsibility of user to take care of caller.
             */
            if (DEBUG)
                Log.d(CLASSNAME, e.getMessage(), e);
        }

        return false;
    }// callStartActivityForResult()

    /**
     * Use this action to compare pattern. You provide the pattern to be
     * compared with {@link #EXTRA_PATTERN}.
     * <p/>
     * If you enabled feature auto-save pattern before (with
     * {@link Security#setAutoSavePattern(Context, boolean)} ),
     * then you don't need {@link #EXTRA_PATTERN} at this time. But if you use
     * this extra, its priority is higher than the one stored in shared
     * preferences.
     * <p/>
     * You can use {@link #EXTRA_PENDING_INTENT_FORGOT_PATTERN} to help your
     * users in case they forgot the patterns.
     * <p/>
     * If the user passes, {@link android.app.Activity#RESULT_OK} returns. If not,
     * {@link #RESULT_FAILED} returns.
     * <p/>
     * If the user cancels the task, {@link android.app.Activity#RESULT_CANCELED} returns.
     * <p/>
     * In any case, there will have extra {@link #EXTRA_RETRY_COUNT} available
     * in the intent result.
     *
     * @see #EXTRA_PATTERN
     * @see #EXTRA_PENDING_INTENT_OK
     * @see #EXTRA_PENDING_INTENT_CANCELLED
     * @see #RESULT_FAILED
     * @see #EXTRA_RETRY_COUNT
     * @since v2.4 beta
     */
    public static final String ACTION_COMPARE_PATTERN = CLASSNAME
            + ".compare_pattern";

    /**
     * Creates new intent with {@link #ACTION_COMPARE_PATTERN}. You must call
     * this intent from a UI thread.
     *
     * @param context the context.
     * @param pattern optional, see {@link #EXTRA_PATTERN}.
     * @return new intent.
     */
    public static Intent newIntentToComparePattern(Context context,
                                                   char[] pattern) {
        Intent result = new Intent(ACTION_COMPARE_PATTERN, null, context,
                LockPatternActivity.class);
        if (pattern != null)
            result.putExtra(EXTRA_PATTERN, pattern);

        return result;
    }// newIntentToComparePattern()

    /**
     * This method is a shortcut to call
     * {@link #newIntentToComparePattern(Context, char[])} from a UI thread.
     *
     * @param caller      must be an instance of {@link android.app.Activity}, or {@link android.app.Fragment}
     *                    or support library's {@code Fragment}. Other values will be
     *                    ignored.
     * @param context     the context.
     * @param requestCode request code for
     *                    {@link android.app.Activity#startActivityForResult(Intent, int)} or
     *                    counterpart methods of fragments.
     * @param pattern     optional, see {@link #EXTRA_PATTERN}.
     * @return {@code true} if the call has been made successfully,
     * {@code false} if any exception occurred.
     * @throws NullPointerException if caller or context is {@code null}.
     */
    public static boolean startToComparePattern(Object caller, Context context,
                                                int requestCode, char[] pattern) {
        return callStartActivityForResult(caller,
                newIntentToComparePattern(context, pattern), requestCode);
    }// startToComparePattern()

    /**
     * Use this action to let the activity generate a random pattern and ask the
     * user to re-draw it to verify.
     * <p/>
     * The default length of the auto-generated pattern is {@code 4}. You can
     * change it with
     * {@link Display#setCaptchaWiredDots(Context, int)}.
     *
     * @since v2.7 beta
     */
    public static final String ACTION_VERIFY_CAPTCHA = CLASSNAME
            + ".verify_captcha";

    /**
     * Creates new intent with {@link #ACTION_VERIFY_CAPTCHA}. You must call
     * this intent from a UI thread.
     *
     * @param context the context.
     * @return new intent.
     */
    public static Intent newIntentToVerifyCaptcha(Context context) {
        Intent result = new Intent(ACTION_VERIFY_CAPTCHA, null, context,
                LockPatternActivity.class);
        return result;
    }// newIntentToVerifyCaptcha()

    /**
     * This method is a shortcut to call
     * {@link #newIntentToVerifyCaptcha(Context)} from a UI thread.
     *
     * @param caller      must be an instance of {@link android.app.Activity}, or {@link android.app.Fragment}
     *                    or support library's {@code Fragment}. Other values will be
     *                    ignored.
     * @param context     the context.
     * @param requestCode request code for
     *                    {@link android.app.Activity#startActivityForResult(Intent, int)} or
     *                    counterpart methods of fragments.
     * @return {@code true} if the call has been made successfully,
     * {@code false} if any exception occurred.
     * @throws NullPointerException if caller or context is {@code null}.
     */
    public static boolean startToVerifyCaptcha(Object caller, Context context,
                                               int requestCode) {
        return callStartActivityForResult(caller,
                newIntentToVerifyCaptcha(context), requestCode);
    }// startToVerifyCaptcha()

    /**
     * If you use {@link #ACTION_COMPARE_PATTERN} and the user fails to "login"
     * after a number of tries, this activity will finish with this result code.
     *
     * @see #ACTION_COMPARE_PATTERN
     * @see #EXTRA_RETRY_COUNT
     */
    public static final int RESULT_FAILED = RESULT_FIRST_USER + 1;

    /**
     * If you use {@link #ACTION_COMPARE_PATTERN} and the user forgot his/ her
     * pattern and decided to ask for your help with recovering the pattern (
     * {@link #EXTRA_PENDING_INTENT_FORGOT_PATTERN}), this activity will finish
     * with this result code.
     *
     * @see #ACTION_COMPARE_PATTERN
     * @see #EXTRA_RETRY_COUNT
     * @see #EXTRA_PENDING_INTENT_FORGOT_PATTERN
     * @since v2.8 beta
     */
    public static final int RESULT_FORGOT_PATTERN = RESULT_FIRST_USER + 2;

    /**
     * For actions {@link #ACTION_COMPARE_PATTERN} and
     * {@link #ACTION_VERIFY_CAPTCHA}, this key holds the number of tries that
     * the user attempted to verify the input pattern.
     */
    public static final String EXTRA_RETRY_COUNT = CLASSNAME + ".retry_count";

    /**
     * Sets value of this key to a theme in {@code R.style.Alp_42447968_Theme_*}
     * . Default is the one you set in your {@code AndroidManifest.xml}. Note
     * that theme {@link com.ericyl.themedemo.R.style#AppTheme} is
     * available in API 4+, but it only works in API 14+.
     *
     * @since v1.5.3 beta
     */
    public static final String EXTRA_THEME = CLASSNAME + ".theme";

    /**
     * Key to hold the pattern. It must be a {@code char[]} array.
     * <p/>
     * <ul>
     * <li>If you use encrypter, it should be an encrypted array.</li>
     * <li>If you don't use encrypter, it should be the SHA-1 value of the
     * actual pattern. You can generate the value by
     * {@link LockPatternUtils#patternToSha1(List)}.</li>
     * </ul>
     *
     * @since v2 beta
     */
    public static final String EXTRA_PATTERN = CLASSNAME + ".pattern";

    /**
     * You can provide an {@link ResultReceiver} with this key. The activity
     * will notify your receiver the same result code and intent data as you
     * will receive them in {@link #onActivityResult(int, int, Intent)}.
     *
     * @since v2.4 beta
     */
    public static final String EXTRA_RESULT_RECEIVER = CLASSNAME
            + ".result_receiver";

    /**
     * Put a {@link PendingIntent} into this key. It will be sent before
     * {@link android.app.Activity#RESULT_OK} will be returning. If you were calling this
     * activity with {@link #ACTION_CREATE_PATTERN}, key {@link #EXTRA_PATTERN}
     * will be attached to the original intent which the pending intent holds.
     * <p/>
     * <h1>Notes</h1>
     * <ul>
     * <li>If you're going to use an activity, you don't need
     * {@link Intent#FLAG_ACTIVITY_NEW_TASK} for the intent, since the library
     * will call it inside {@link LockPatternActivity} .</li>
     * </ul>
     */
    public static final String EXTRA_PENDING_INTENT_OK = CLASSNAME
            + ".pending_intent_ok";

    /**
     * Put a {@link PendingIntent} into this key. It will be sent before
     * {@link android.app.Activity#RESULT_CANCELED} will be returning.
     * <p/>
     * <h1>Notes</h1>
     * <ul>
     * <li>If you're going to use an activity, you don't need
     * {@link Intent#FLAG_ACTIVITY_NEW_TASK} for the intent, since the library
     * will call it inside {@link LockPatternActivity} .</li>
     * </ul>
     */
    public static final String EXTRA_PENDING_INTENT_CANCELLED = CLASSNAME
            + ".pending_intent_cancelled";

    /**
     * You put a {@link PendingIntent} into this extra. The library will show a
     * button <i>"Forgot pattern?"</i> and call your intent later when the user
     * taps it.
     * <p/>
     * <h1>Notes</h1>
     * <ul>
     * <li>If you use an activity, you don't need
     * {@link Intent#FLAG_ACTIVITY_NEW_TASK} for the intent, since the library
     * will call it inside {@link LockPatternActivity} .</li>
     * <li>{@link LockPatternActivity} will finish with
     * {@link #RESULT_FORGOT_PATTERN} <i><b>after</b> making a call</i> to start
     * your pending intent.</li>
     * <li>It is your responsibility to make sure the Intent is good. The
     * library doesn't cover any errors when calling your intent.</li>
     * </ul>
     *
     * @author Thanks to Yan Cheng Cheok for his idea.
     * @see #ACTION_COMPARE_PATTERN
     * @since v2.8 beta
     */
    public static final String EXTRA_PENDING_INTENT_FORGOT_PATTERN = CLASSNAME
            + ".pending_intent_forgot_pattern";

    /**
     * Helper enum for button OK commands. (Because we use only one "OK" button
     * for different commands).
     *
     * @author Hai Bison
     */
    private static enum ButtonOkCommand {
        CONTINUE, FORGOT_PATTERN, DONE
    }// ButtonOkCommand

    /**
     * Delay time to reload the lock pattern view after a wrong pattern.
     */
    private static final long DELAY_TIME_TO_RELOAD_LOCK_PATTERN_VIEW = SECOND_IN_MILLIS;

    /*
     * FIELDS
     */
    private int mMaxRetries, mMinWiredDots, mRetryCount = 0, mCaptchaWiredDots;
    private boolean mAutoSave, mStealthMode;
    private IEncrypter mEncrypter;
    private ButtonOkCommand mBtnOkCmd;
    private Intent mIntentResult;
    private LoadingView<Void, Void, Object> mLoadingView;

    /*
     * CONTROLS
     */
    private TextView mTextInfo;
    private LockPatternView mLockPatternView;
    private View mFooter;
    private Button mBtnCancel;
    private Button mBtnConfirm;
    private View mViewGroupProgressBar;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG)
            Log.d(CLASSNAME, "ClassName = " + CLASSNAME);

        /*
         * EXTRA_THEME
         */

//        if (getIntent().hasExtra(EXTRA_THEME))
//            setTheme(getIntent().getIntExtra(EXTRA_THEME,
//                    R.style.AppTheme));

        super.onCreate(savedInstanceState);

        loadSettings();

        mIntentResult = new Intent();
        setResult(RESULT_CANCELED, mIntentResult);

        initContentView();
    }// onCreate()

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (BuildConfig.DEBUG)
            Log.d(CLASSNAME, "onConfigurationChanged()");
        super.onConfigurationChanged(newConfig);
        initContentView();
    }// onConfigurationChanged()

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        /*
         * Use this hook instead of onBackPressed(), because onBackPressed() is
         * not available in API 4.
         */
        if (keyCode == KeyEvent.KEYCODE_BACK
                && ACTION_COMPARE_PATTERN.equals(getIntent().getAction())) {
            if (mLoadingView != null)
                mLoadingView.cancel(true);

            finishWithNegativeResult(RESULT_CANCELED);

            return true;
        }// if

        return super.onKeyDown(keyCode, event);
    }// onKeyDown()

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /*
         * Support canceling dialog on touching outside in APIs < 11.
         *
         * This piece of code is copied from android.view.Window. You can find
         * it by searching for methods shouldCloseOnTouch() and isOutOfBounds().
         */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
                && event.getAction() == MotionEvent.ACTION_DOWN
                && getWindow().peekDecorView() != null) {
            final int x = (int) event.getX();
            final int y = (int) event.getY();
            final int slop = ViewConfiguration.get(this)
                    .getScaledWindowTouchSlop();
            final View decorView = getWindow().getDecorView();
            boolean isOutOfBounds = (x < -slop) || (y < -slop)
                    || (x > (decorView.getWidth() + slop))
                    || (y > (decorView.getHeight() + slop));
            if (isOutOfBounds) {
                finishWithNegativeResult(RESULT_CANCELED);
                return true;
            }
        }// if

        return super.onTouchEvent(event);
    }// onTouchEvent()

    @Override
    protected void onDestroy() {
        if (mLoadingView != null)
            mLoadingView.cancel(true);

        super.onDestroy();
    }// onDestroy()

    /**
     * Loads settings, either from manifest or {@link com.ericyl.themedemo.util.Settings}.
     */
    private void loadSettings() {
        Bundle metaData = null;
        try {
            metaData = getPackageManager().getActivityInfo(getComponentName(),
                    PackageManager.GET_META_DATA).metaData;
        } catch (NameNotFoundException e) {
            /*
             * Never catch this.
             */
            e.printStackTrace();
        }

        if (metaData != null && metaData.containsKey(METADATA_MIN_WIRED_DOTS))
            mMinWiredDots = Display.validateMinWiredDots(this,
                    metaData.getInt(METADATA_MIN_WIRED_DOTS));
        else
            mMinWiredDots = Display.getMinWiredDots(this);

        if (metaData != null && metaData.containsKey(METADATA_MAX_RETRIES))
            mMaxRetries = Display.validateMaxRetries(this,
                    metaData.getInt(METADATA_MAX_RETRIES));
        else
            mMaxRetries = Display.getMaxRetries(this);

        if (metaData != null
                && metaData.containsKey(METADATA_AUTO_SAVE_PATTERN))
            mAutoSave = metaData.getBoolean(METADATA_AUTO_SAVE_PATTERN);
        else
            mAutoSave = Security.isAutoSavePattern(this);

        if (metaData != null
                && metaData.containsKey(METADATA_CAPTCHA_WIRED_DOTS))
            mCaptchaWiredDots = Display.validateCaptchaWiredDots(this,
                    metaData.getInt(METADATA_CAPTCHA_WIRED_DOTS));
        else
            mCaptchaWiredDots = Display.getCaptchaWiredDots(this);

        if (metaData != null && metaData.containsKey(METADATA_STEALTH_MODE))
            mStealthMode = metaData.getBoolean(METADATA_STEALTH_MODE);
        else
            mStealthMode = Display.isStealthMode(this);

        /*
         * Encrypter.
         */
        char[] encrypterClass;
        if (metaData != null && metaData.containsKey(METADATA_ENCRYPTER_CLASS))
            encrypterClass = metaData.getString(METADATA_ENCRYPTER_CLASS)
                    .toCharArray();
        else
            encrypterClass = Security.getEncrypterClass(this);

        if (encrypterClass != null) {
            try {
                mEncrypter = (IEncrypter) Class.forName(
                        new String(encrypterClass), false, getClassLoader())
                        .newInstance();
            } catch (Throwable t) {
                throw new InvalidEncrypterException();
            }
        }
    }// loadSettings()

    /**
     * Initializes UI...
     */
    private void initContentView() {

        /*
         * Save all controls' state to restore later.
         */
        CharSequence infoText = mTextInfo != null ? mTextInfo.getText() : null;
        Boolean btnOkEnabled = mBtnConfirm != null ? mBtnConfirm.isEnabled()
                : null;
        DisplayMode lastDisplayMode = mLockPatternView != null ? mLockPatternView
                .getDisplayMode() : null;
        List<Cell> lastPattern = mLockPatternView != null ? mLockPatternView
                .getPattern() : null;

        setContentView(com.ericyl.themedemo.R.layout.alp_42447968_lock_pattern_activity);
        UI.adjustDialogSizeForLargeScreens(getWindow());

        initToolBar();

        /*
         * MAP CONTROLS
         */

        mTextInfo = (TextView) findViewById(com.ericyl.themedemo.R.id.alp_42447968_textview_info);
        mLockPatternView = (LockPatternView) findViewById(com.ericyl.themedemo.R.id.alp_42447968_view_lock_pattern);

        mFooter = findViewById(com.ericyl.themedemo.R.id.alp_42447968_viewgroup_footer);
        mBtnCancel = (Button) findViewById(com.ericyl.themedemo.R.id.alp_42447968_button_cancel);
        mBtnConfirm = (Button) findViewById(com.ericyl.themedemo.R.id.alp_42447968_button_confirm);

        mViewGroupProgressBar = findViewById(com.ericyl.themedemo.R.id.alp_42447968_view_group_progress_bar);

        /*
         * SETUP CONTROLS
         */

        mViewGroupProgressBar
                .setOnClickListener(mViewGroupProgressBarOnClickListener);

        /*
         * LOCK PATTERN VIEW
         */

        switch (getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
            case Configuration.SCREENLAYOUT_SIZE_XLARGE: {
                final int size = getResources().getDimensionPixelSize(
                        com.ericyl.themedemo.R.dimen.alp_42447968_lockpatternview_size);
                LayoutParams lp = mLockPatternView.getLayoutParams();
                lp.width = size;
                lp.height = size;
                mLockPatternView.setLayoutParams(lp);

                break;
            }// LARGE / XLARGE
        }


        /*
         * Haptic feedback.
         */
        boolean hapticFeedbackEnabled = false;
        try {
            hapticFeedbackEnabled = android.provider.Settings.System
                    .getInt(getContentResolver(),
                            android.provider.Settings.System.HAPTIC_FEEDBACK_ENABLED,
                            0) != 0;
        } catch (Throwable t) {
            /*
             * Ignore it.
             */
        }
        mLockPatternView.setTactileFeedbackEnabled(hapticFeedbackEnabled);

        mLockPatternView.setInStealthMode(mStealthMode
                && !ACTION_VERIFY_CAPTCHA.equals(getIntent().getAction()));
        mLockPatternView.setOnPatternListener(mLockPatternViewListener);
        if (lastPattern != null && lastDisplayMode != null
                && !ACTION_VERIFY_CAPTCHA.equals(getIntent().getAction()))
            mLockPatternView.setPattern(lastDisplayMode, lastPattern);

        /*
         * COMMAND BUTTONS
         */

        if (ACTION_CREATE_PATTERN.equals(getIntent().getAction())) {
            mBtnCancel.setOnClickListener(mBtnCancelOnClickListener);
            mBtnConfirm.setOnClickListener(mBtnConfirmOnClickListener);

            mBtnCancel.setVisibility(View.VISIBLE);
            mFooter.setVisibility(View.VISIBLE);

            if (infoText != null)
                mTextInfo.setText(infoText);
            else
                mTextInfo
                        .setText(com.ericyl.themedemo.R.string.alp_42447968_msg_draw_an_unlock_pattern);

            /*
             * BUTTON OK
             */
            if (mBtnOkCmd == null)
                mBtnOkCmd = ButtonOkCommand.CONTINUE;
            switch (mBtnOkCmd) {
                case CONTINUE:
                    mBtnConfirm.setText(com.ericyl.themedemo.R.string.alp_42447968_cmd_continue);
                    break;
                case DONE:
                    mBtnConfirm.setText(com.ericyl.themedemo.R.string.alp_42447968_cmd_confirm);
                    break;
                default:
                /*
                 * Do nothing.
                 */
                    break;
            }
            if (btnOkEnabled != null)
                mBtnConfirm.setEnabled(btnOkEnabled);
        }// ACTION_CREATE_PATTERN
        else if (ACTION_COMPARE_PATTERN.equals(getIntent().getAction())) {
            if (TextUtils.isEmpty(infoText))
                mTextInfo
                        .setText(com.ericyl.themedemo.R.string.alp_42447968_msg_draw_pattern_to_unlock);
            else
                mTextInfo.setText(infoText);
            if (getIntent().hasExtra(EXTRA_PENDING_INTENT_FORGOT_PATTERN)) {
                mBtnConfirm.setOnClickListener(mBtnConfirmOnClickListener);
                mBtnConfirm.setText(com.ericyl.themedemo.R.string.alp_42447968_cmd_forgot_pattern);
                mBtnConfirm.setEnabled(true);
                mFooter.setVisibility(View.VISIBLE);
            }
        }// ACTION_COMPARE_PATTERN
        else if (ACTION_VERIFY_CAPTCHA.equals(getIntent().getAction())) {
            mTextInfo
                    .setText(com.ericyl.themedemo.R.string.alp_42447968_msg_redraw_pattern_to_confirm);

            /*
             * NOTE: EXTRA_PATTERN should hold a char[] array. In this case we
             * use it as a temporary variable to hold a list of Cell.
             */

            final ArrayList<Cell> pattern;
            if (getIntent().hasExtra(EXTRA_PATTERN))
                pattern = getIntent()
                        .getParcelableArrayListExtra(EXTRA_PATTERN);
            else
                getIntent().putParcelableArrayListExtra(
                        EXTRA_PATTERN,
                        pattern = LockPatternUtils
                                .genCaptchaPattern(mCaptchaWiredDots));

            mLockPatternView.setPattern(DisplayMode.Animate, pattern);
        }// ACTION_VERIFY_CAPTCHA
    }// initContentView()

    private void initToolBar() {
        mToolbar = (Toolbar) findViewById(com.ericyl.themedemo.R.id.toolbar);
        mToolbar.setTitle("aaa");
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
    }

    /**
     * Compares {@code pattern} to the given pattern (
     * {@link #ACTION_COMPARE_PATTERN}) or to the generated "CAPTCHA" pattern (
     * {@link #ACTION_VERIFY_CAPTCHA}). Then finishes the activity if they
     * match.
     *
     * @param pattern the pattern to be compared.
     */
    private void doComparePattern(final List<Cell> pattern) {
        if (pattern == null)
            return;

        /*
         * Use a LoadingView because decrypting pattern might take time...
         */

        mLoadingView = new LoadingView<Void, Void, Object>(this,
                mViewGroupProgressBar) {

            @Override
            protected Object doInBackground(Void... params) {
                if (ACTION_COMPARE_PATTERN.equals(getIntent().getAction())) {
                    char[] currentPattern = getIntent().getCharArrayExtra(
                            EXTRA_PATTERN);
                    if (currentPattern == null)
                        currentPattern = Security
                                .getPattern(LockPatternActivity.this);
                    if (currentPattern != null) {
                        if (mEncrypter != null)
                            return pattern.equals(mEncrypter.decrypt(
                                    LockPatternActivity.this, currentPattern));
                        else
                            return Arrays.equals(currentPattern,
                                    LockPatternUtils.patternToSha1(pattern)
                                            .toCharArray());
                    }
                }// ACTION_COMPARE_PATTERN
                else if (ACTION_VERIFY_CAPTCHA.equals(getIntent().getAction())) {
                    return pattern.equals(getIntent()
                            .getParcelableArrayListExtra(EXTRA_PATTERN));
                }// ACTION_VERIFY_CAPTCHA

                return false;
            }// doInBackground()

            @Override
            protected void onPostExecute(Object result) {
                super.onPostExecute(result);

                if ((Boolean) result)
                    finishWithResultOk(null);
                else {
                    mRetryCount++;
                    mIntentResult.putExtra(EXTRA_RETRY_COUNT, mRetryCount);

                    if (mRetryCount >= mMaxRetries)
                        finishWithNegativeResult(RESULT_FAILED);
                    else {
                        mLockPatternView.setDisplayMode(DisplayMode.Wrong);
                        mTextInfo.setText(com.ericyl.themedemo.R.string.alp_42447968_msg_try_again);
                        mLockPatternView.postDelayed(mLockPatternViewReloader,
                                DELAY_TIME_TO_RELOAD_LOCK_PATTERN_VIEW);
                    }
                }
            }// onPostExecute()

        };

        mLoadingView.execute();
    }// doComparePattern()

    /**
     * Checks and creates the pattern.
     *
     * @param pattern the current pattern of lock pattern view.
     */
    private void doCheckAndCreatePattern(final List<Cell> pattern) {
        if (pattern.size() < mMinWiredDots) {
            mLockPatternView.setDisplayMode(DisplayMode.Wrong);
            mTextInfo.setText(getResources().getQuantityString(
                    com.ericyl.themedemo.R.plurals.alp_42447968_pmsg_connect_x_dots, mMinWiredDots,
                    mMinWiredDots));
            mLockPatternView.postDelayed(mLockPatternViewReloader,
                    DELAY_TIME_TO_RELOAD_LOCK_PATTERN_VIEW);
            return;
        }// if

        if (getIntent().hasExtra(EXTRA_PATTERN)) {
            /*
             * Use a LoadingView because decrypting pattern might take time...
             */
            mLoadingView = new LoadingView<Void, Void, Object>(this,
                    mViewGroupProgressBar) {

                @Override
                protected Object doInBackground(Void... params) {
                    if (mEncrypter != null)
                        return pattern.equals(mEncrypter.decrypt(
                                LockPatternActivity.this, getIntent()
                                        .getCharArrayExtra(EXTRA_PATTERN)));
                    else
                        return Arrays.equals(
                                getIntent().getCharArrayExtra(EXTRA_PATTERN),
                                LockPatternUtils.patternToSha1(pattern)
                                        .toCharArray());
                }// doInBackground()

                @Override
                protected void onPostExecute(Object result) {
                    super.onPostExecute(result);

                    if ((Boolean) result) {
                        mTextInfo
                                .setText(com.ericyl.themedemo.R.string.alp_42447968_msg_your_new_unlock_pattern);
                        mBtnConfirm.setEnabled(true);
                    } else {
                        mTextInfo
                                .setText(com.ericyl.themedemo.R.string.alp_42447968_msg_redraw_pattern_to_confirm);
                        mBtnConfirm.setEnabled(false);
                        mLockPatternView.setDisplayMode(DisplayMode.Wrong);
                        mLockPatternView.postDelayed(mLockPatternViewReloader,
                                DELAY_TIME_TO_RELOAD_LOCK_PATTERN_VIEW);
                    }
                }// onPostExecute()

            };

            mLoadingView.execute();
        } else {
            /*
             * Use a LoadingView because encrypting pattern might take time...
             */
            mLoadingView = new LoadingView<Void, Void, Object>(this,
                    mViewGroupProgressBar) {

                @Override
                protected Object doInBackground(Void... params) {
                    return mEncrypter != null ? mEncrypter.encrypt(
                            LockPatternActivity.this, pattern)
                            : LockPatternUtils.patternToSha1(pattern)
                            .toCharArray();
                }// onCancel()

                @Override
                protected void onPostExecute(Object result) {
                    super.onPostExecute(result);

                    getIntent().putExtra(EXTRA_PATTERN, (char[]) result);
                    mTextInfo
                            .setText(com.ericyl.themedemo.R.string.alp_42447968_msg_pattern_recorded);
                    mBtnConfirm.setEnabled(true);
                }// onPostExecute()

            };

            mLoadingView.execute();
        }
    }// doCheckAndCreatePattern()

    /**
     * Finishes activity with {@link android.app.Activity#RESULT_OK}.
     *
     * @param pattern the pattern, if this is in mode creating pattern. In any
     *                cases, it can be set to {@code null}.
     */
    private void finishWithResultOk(char[] pattern) {
        if (ACTION_CREATE_PATTERN.equals(getIntent().getAction()))
            mIntentResult.putExtra(EXTRA_PATTERN, pattern);
        else {
            /*
             * If the user was "logging in", minimum try count can not be zero.
             */
            mIntentResult.putExtra(EXTRA_RETRY_COUNT, mRetryCount + 1);
        }

        setResult(RESULT_OK, mIntentResult);

        /*
         * ResultReceiver
         */
        ResultReceiver receiver = getIntent().getParcelableExtra(
                EXTRA_RESULT_RECEIVER);
        if (receiver != null) {
            Bundle bundle = new Bundle();
            if (ACTION_CREATE_PATTERN.equals(getIntent().getAction()))
                bundle.putCharArray(EXTRA_PATTERN, pattern);
            else {
                /*
                 * If the user was "logging in", minimum try count can not be
                 * zero.
                 */
                bundle.putInt(EXTRA_RETRY_COUNT, mRetryCount + 1);
            }
            receiver.send(RESULT_OK, bundle);
        }

        /*
         * PendingIntent
         */
        PendingIntent pi = getIntent().getParcelableExtra(
                EXTRA_PENDING_INTENT_OK);
        if (pi != null) {
            try {
                pi.send(this, RESULT_OK, mIntentResult);
            } catch (Throwable t) {
                Log.e(CLASSNAME, "Error sending PendingIntent: " + pi, t);
            }
        }

        finish();
    }// finishWithResultOk()

    /**
     * Finishes the activity with negative result (
     * {@link android.app.Activity#RESULT_CANCELED}, {@link #RESULT_FAILED} or
     * {@link #RESULT_FORGOT_PATTERN}).
     */
    private void finishWithNegativeResult(int resultCode) {
        if (ACTION_COMPARE_PATTERN.equals(getIntent().getAction()))
            mIntentResult.putExtra(EXTRA_RETRY_COUNT, mRetryCount);

        setResult(resultCode, mIntentResult);

        /*
         * ResultReceiver
         */
        ResultReceiver receiver = getIntent().getParcelableExtra(
                EXTRA_RESULT_RECEIVER);
        if (receiver != null) {
            Bundle resultBundle = null;
            if (ACTION_COMPARE_PATTERN.equals(getIntent().getAction())) {
                resultBundle = new Bundle();
                resultBundle.putInt(EXTRA_RETRY_COUNT, mRetryCount);
            }
            receiver.send(resultCode, resultBundle);
        }

        /*
         * PendingIntent
         */
        PendingIntent pi = getIntent().getParcelableExtra(
                EXTRA_PENDING_INTENT_CANCELLED);
        if (pi != null) {
            try {
                pi.send(this, resultCode, mIntentResult);
            } catch (Throwable t) {
                Log.e(CLASSNAME, "Error sending PendingIntent: " + pi, t);
            }
        }

        finish();
    }// finishWithNegativeResult()

    /*
     * LISTENERS
     */

    /**
     * Pattern listener for LockPatternView.
     */
    private final LockPatternView.OnPatternListener mLockPatternViewListener = new LockPatternView.OnPatternListener() {

        @Override
        public void onPatternStart() {
            mLockPatternView.removeCallbacks(mLockPatternViewReloader);
            mLockPatternView.setDisplayMode(DisplayMode.Correct);

            if (ACTION_CREATE_PATTERN.equals(getIntent().getAction())) {
                mTextInfo
                        .setText(com.ericyl.themedemo.R.string.alp_42447968_msg_release_finger_when_done);
                mBtnConfirm.setEnabled(false);
                if (mBtnOkCmd == ButtonOkCommand.CONTINUE)
                    getIntent().removeExtra(EXTRA_PATTERN);
            }// ACTION_CREATE_PATTERN
            else if (ACTION_COMPARE_PATTERN.equals(getIntent().getAction())) {
                mTextInfo
                        .setText(com.ericyl.themedemo.R.string.alp_42447968_msg_draw_pattern_to_unlock);
            }// ACTION_COMPARE_PATTERN
            else if (ACTION_VERIFY_CAPTCHA.equals(getIntent().getAction())) {
                mTextInfo
                        .setText(com.ericyl.themedemo.R.string.alp_42447968_msg_redraw_pattern_to_confirm);
            }// ACTION_VERIFY_CAPTCHA
        }// onPatternStart()

        @Override
        public void onPatternDetected(List<Cell> pattern) {
            if (ACTION_CREATE_PATTERN.equals(getIntent().getAction())) {
                doCheckAndCreatePattern(pattern);
            }// ACTION_CREATE_PATTERN
            else if (ACTION_COMPARE_PATTERN.equals(getIntent().getAction())) {
                doComparePattern(pattern);
            }// ACTION_COMPARE_PATTERN
            else if (ACTION_VERIFY_CAPTCHA.equals(getIntent().getAction())) {
                if (!DisplayMode.Animate.equals(mLockPatternView
                        .getDisplayMode()))
                    doComparePattern(pattern);
            }// ACTION_VERIFY_CAPTCHA
        }// onPatternDetected()

        @Override
        public void onPatternCleared() {
            mLockPatternView.removeCallbacks(mLockPatternViewReloader);

            if (ACTION_CREATE_PATTERN.equals(getIntent().getAction())) {
                mLockPatternView.setDisplayMode(DisplayMode.Correct);
                mBtnConfirm.setEnabled(false);
                if (mBtnOkCmd == ButtonOkCommand.CONTINUE) {
                    getIntent().removeExtra(EXTRA_PATTERN);
                    mTextInfo
                            .setText(com.ericyl.themedemo.R.string.alp_42447968_msg_draw_an_unlock_pattern);
                } else
                    mTextInfo
                            .setText(com.ericyl.themedemo.R.string.alp_42447968_msg_redraw_pattern_to_confirm);
            }// ACTION_CREATE_PATTERN
            else if (ACTION_COMPARE_PATTERN.equals(getIntent().getAction())) {
                mLockPatternView.setDisplayMode(DisplayMode.Correct);
                mTextInfo
                        .setText(com.ericyl.themedemo.R.string.alp_42447968_msg_draw_pattern_to_unlock);
            }// ACTION_COMPARE_PATTERN
            else if (ACTION_VERIFY_CAPTCHA.equals(getIntent().getAction())) {
                mTextInfo
                        .setText(com.ericyl.themedemo.R.string.alp_42447968_msg_redraw_pattern_to_confirm);
                List<Cell> pattern = getIntent().getParcelableArrayListExtra(
                        EXTRA_PATTERN);
                mLockPatternView.setPattern(DisplayMode.Animate, pattern);
            }// ACTION_VERIFY_CAPTCHA
        }// onPatternCleared()

        @Override
        public void onPatternCellAdded(List<Cell> pattern) {
            // TODO Auto-generated method stub
        }// onPatternCellAdded()

    };// mLockPatternViewListener

    /**
     * Click listener for button Cancel.
     */
    private final View.OnClickListener mBtnCancelOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            finishWithNegativeResult(RESULT_CANCELED);
        }// onClick()

    };// mBtnCancelOnClickListener

    /**
     * Click listener for button Confirm.
     */
    private final View.OnClickListener mBtnConfirmOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (ACTION_CREATE_PATTERN.equals(getIntent().getAction())) {
                if (mBtnOkCmd == ButtonOkCommand.CONTINUE) {
                    mBtnOkCmd = ButtonOkCommand.DONE;
                    mLockPatternView.clearPattern();
                    mTextInfo
                            .setText(com.ericyl.themedemo.R.string.alp_42447968_msg_redraw_pattern_to_confirm);
                    mBtnConfirm.setText(com.ericyl.themedemo.R.string.alp_42447968_cmd_confirm);
                    mBtnConfirm.setEnabled(false);
                } else {
                    final char[] pattern = getIntent().getCharArrayExtra(
                            EXTRA_PATTERN);
                    if (mAutoSave)
                        Security.setPattern(LockPatternActivity.this,
                                pattern);
                    finishWithResultOk(pattern);
                }
            }// ACTION_CREATE_PATTERN
            else if (ACTION_COMPARE_PATTERN.equals(getIntent().getAction())) {
                /*
                 * We don't need to verify the extra. First, this button is only
                 * visible if there is this extra in the intent. Second, it is
                 * the responsibility of the caller to make sure the extra is
                 * good.
                 */
                PendingIntent pi = null;
                try {
                    pi = getIntent().getParcelableExtra(
                            EXTRA_PENDING_INTENT_FORGOT_PATTERN);
                    pi.send();
                } catch (Throwable t) {
                    Log.e(CLASSNAME, "Error sending pending intent: " + pi, t);
                }
                finishWithNegativeResult(RESULT_FORGOT_PATTERN);
            }// ACTION_COMPARE_PATTERN
        }// onClick()

    };// mBtnConfirmOnClickListener

    /**
     * This reloads the {@link #mLockPatternView} after a wrong pattern.
     */
    private final Runnable mLockPatternViewReloader = new Runnable() {

        @Override
        public void run() {
            mLockPatternView.clearPattern();
            mLockPatternViewListener.onPatternCleared();
        }// run()

    };// mLockPatternViewReloader

    /**
     * Click listener for view group progress bar.
     */
    private final View.OnClickListener mViewGroupProgressBarOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            /*
             * Do nothing. We just don't want the user to interact with controls
             * behind this view.
             */
        }// onClick()

    };// mViewGroupProgressBarOnClickListener

}
