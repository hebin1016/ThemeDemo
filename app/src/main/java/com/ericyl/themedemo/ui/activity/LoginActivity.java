package com.ericyl.themedemo.ui.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.ericyl.themedemo.R;
import com.ericyl.themedemo.util.Settings;

public class LoginActivity extends ActionBarActivity implements View.OnClickListener {

    private ActionBar actionBar;
    private Toolbar toolbar;

    private EditText etUserName;
    private EditText etPassword;
    private CheckBox cbRemeberUsername;
    private Button btnSubmit;

    private static final int REQUEST_ENTER_PATTERN = 1;
    private static final int REQUEST_FORGET_PATTERN = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init() {
        initToolbar();
        initData();

        etUserName = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        cbRemeberUsername = (CheckBox) findViewById(R.id.cb_remeber_username);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(this);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //actionBar = getSupportActionBar();
    }

    private void initData() {
        char[] pattern =
                Settings.Security.getPattern(this);
        if (pattern != null)
            jumpLockPatternActivity(pattern);

    }

    private void jumpLockPatternActivity(char[] pattern) {
        Intent intent = new Intent(LockPatternActivity.ACTION_COMPARE_PATTERN, null,
                this, LockPatternActivity.class);
        intent.putExtra(LockPatternActivity.EXTRA_PATTERN, pattern);


        Settings.Display.setCaptchaWiredDots(this, 9);
        Intent intent1 = new Intent(LockPatternActivity.ACTION_VERIFY_CAPTCHA, null,
                this, LockPatternActivity.class);
        PendingIntent piForgotPattern = PendingIntent.getActivity(this, REQUEST_FORGET_PATTERN, intent1, 0);

        intent.putExtra(
                LockPatternActivity.EXTRA_PENDING_INTENT_FORGOT_PATTERN,
                piForgotPattern);

        startActivityForResult(intent, REQUEST_ENTER_PATTERN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        switch (requestCode) {
            case REQUEST_ENTER_PATTERN: {
        /*
         * NOTE that there are 4 possible result codes!!!
         */
                switch (resultCode) {
                    case RESULT_OK:
                        // The user passed
                        Settings.Security.setPattern(this, null);
                        break;
                    case RESULT_CANCELED:
                        // The user cancelled the task
                        break;
                    case LockPatternActivity.RESULT_FAILED:
                        // The user failed to enter the pattern
                        break;
                    case LockPatternActivity.RESULT_FORGOT_PATTERN:
                        // The user forgot the pattern and invoked your recovery Activity.
                        break;
                }

        /*
         * In any case, there's always a key EXTRA_RETRY_COUNT, which holds
         * the number of tries that the user did.
         */
                int retryCount = data.getIntExtra(
                        LockPatternActivity.EXTRA_RETRY_COUNT, 0);
                System.out.println(retryCount + "");

                break;
            }// REQ_ENTER_PATTERN
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_submit:
                Intent intent = new Intent();
                intent.setClass(this, SettingActivity.class);
                startActivity(intent);
                break;

        }
    }
}
