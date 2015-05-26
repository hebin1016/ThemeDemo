package com.ericyl.themedemo.ui.activity;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.ericyl.themedemo.R;
import com.ericyl.themedemo.model.User;
import com.ericyl.themedemo.ui.widget.MyEditText;
import com.ericyl.themedemo.util.DialogUtils;
import com.ericyl.themedemo.util.PhoneUtils;
import com.ericyl.themedemo.util.Settings;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;



public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;

    private MyEditText etUserName;
    private MyEditText etPassword;
    private CheckBox cbRemeberUsername;
    private Button btnSubmit;
    private Button btnForgotPassword;
    private Button btnSignUp;
    private View layoutWaitingLogin;
    private Dialog networkDialog;

    private boolean isLogin = true;
    private HttpHandler loginHandler;

    private static final int REQUEST_ENTER_PATTERN = 1;
    private static final int REQUEST_FORGET_PATTERN = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!PhoneUtils.isNetworkAvailable()){
            if(networkDialog == null) {
                networkDialog = DialogUtils.showDialogWithTwoBtn(this, getString(R.string.setting_network), getString(R.string.network_error), getString(R.string.ok), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent(
                                android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                        startActivity(intent);
                    }
                });
            }else if(!networkDialog.isShowing()){
                networkDialog.show();
            }
        }
    }

    private void init() {
        initToolbar();
        initData();

        etUserName = (MyEditText) findViewById(R.id.et_username);
        etPassword = (MyEditText) findViewById(R.id.et_password);
        cbRemeberUsername = (CheckBox) findViewById(R.id.cb_remeber_username);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        if(isLogin)
            btnSubmit.setText(R.string.login);
        else
            btnSubmit.setText(R.string.cancel);
        btnSubmit.setOnClickListener(this);
        btnForgotPassword = (Button) findViewById(R.id.btn_forgot_password);
        btnForgotPassword.setOnClickListener(this);
        btnSignUp = (Button) findViewById(R.id.btn_sign_up);
        btnSignUp.setOnClickListener(this);
        layoutWaitingLogin = findViewById(R.id.layout_waiting_login);
        layoutWaitingLogin.setVisibility(View.GONE);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView tvTitle = (TextView) toolbar.findViewById(R.id.tv_title);
        tvTitle.setText(R.string.app_name);
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
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.btn_submit:
                if(isLogin) {
                    boolean flag = true;
                    String username = etUserName.getText().toString();
                    String password = etPassword.getText().toString();
                    if (username == null || "".equals(username)) {
                        etUserName.setError(getString(R.string.username_is_required));
                        flag = false;
                    }
                    if (password == null || "".equals(password)) {
                        etPassword.setError(getString(R.string.password_is_required));
                        flag = false;
                    }
                    if (flag)
                        login(username, password);
                }else{
                    loginCancel();
                }
                break;
            case R.id.btn_forgot_password:
                intent.setClass(this, ForgotPasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_sign_up:
                intent.setClass(this, SignUpActivity.class);
                startActivity(intent);
                break;

        }
    }

    private void login(String username, String password){
        RequestParams params = new RequestParams();
        params.addBodyParameter("username", username);
        params.addBodyParameter("password", password);

        HttpUtils http = new HttpUtils();
        loginHandler = http.send(HttpRequest.HttpMethod.POST,
                getString(R.string.host) + "login",
                params,
                new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        etUserName.setEnabled(false);
                        etPassword.setEnabled(false);
                        layoutWaitingLogin.setVisibility(View.VISIBLE);
                        isLogin = false;
                        btnSubmit.setText(R.string.cancel);
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        loginCancel();
                        String result = responseInfo.result;
                        User user = null;
                        if (!"".equals(result))
                            user = JSON.parseObject(result, User.class);
                        if (user == null)
                            Toast.makeText(LoginActivity.this, R.string.username_or_password_incorrect, Toast.LENGTH_SHORT).show();
                        else
                            jumpMainActivity(user);
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        loginCancel();
                        Toast.makeText(LoginActivity.this, error.getExceptionCode() + ":" + msg, Toast.LENGTH_SHORT).show();
                    }

                });
    }

    private void jumpMainActivity(User user){
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        intent.putExtra(User.KEY_NAME, user);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(loginHandler != null) {
            loginCancel();
        }
    }

    private void loginCancel(){
        loginHandler.cancel();
        etUserName.setEnabled(true);
        etPassword.setEnabled(true);
        layoutWaitingLogin.setVisibility(View.GONE);
        isLogin = true;
        btnSubmit.setText(R.string.login);
    }

}
