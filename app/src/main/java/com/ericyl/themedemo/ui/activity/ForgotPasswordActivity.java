package com.ericyl.themedemo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ericyl.themedemo.R;
import com.ericyl.themedemo.util.AppProperties;
import com.ericyl.themedemo.util.CodeUtils;
import com.ericyl.themedemo.util.HttpResultCode;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;


public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private ActionBar actionBar;
    private Toolbar toolbar;
    private TextView tvMessage;
    private EditText etEmail;
    private View layoutVerificationCode;
    private EditText etVerificationCode;
    private Button btnResend;
    private Button btnSubmit;
    private boolean flagSubmit = false;
    private String code = null;
    private String index = null;

    private TimeCount time;

    private HttpHandler verifyEmailHandler;
    private HttpHandler sendVerificationCodeHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        init();
    }

    private void init() {
        initToolbar();

        tvMessage = (TextView) findViewById(R.id.tv_message);
        etEmail = (EditText) findViewById(R.id.et_email);
        etEmail.addTextChangedListener(new EditTextWatcher());
        layoutVerificationCode = findViewById(R.id.layout_verification_code);
        layoutVerificationCode.setVisibility(View.GONE);
        etVerificationCode = (EditText) findViewById(R.id.et_verification_code);
        etVerificationCode.addTextChangedListener(new EditTextWatcher());
        btnResend = (Button) findViewById(R.id.btn_resend);
        btnResend.setOnClickListener(this);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        btnSubmit.setEnabled(false);
        btnSubmit.setOnClickListener(this);

    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.forgot_password);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_resend:
                verifyEmail(etEmail.getText().toString());
                break;
            case R.id.btn_submit:
                if (flagSubmit) {
                    verifyCode();
                } else {
                    if (verifyEmail()) {
                        verifyEmail(etEmail.getText().toString());
                    } else {
                        etEmail.setError("email error");
                    }
                }
                break;
        }
    }

    private void verifyCode() {
        if (code.equals(etVerificationCode.getText().toString())) {
            Intent intent = new Intent();
            intent.putExtra("ShowOldPassword", false);
            intent.putExtra("Index", index);
            intent.setClass(this, ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        } else {
            showToast(R.string.verify_code_is_error);
        }
    }

    class EditTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().length() > 0) {
                btnSubmit.setEnabled(true);
            } else {
                btnSubmit.setEnabled(false);
            }

        }
    }

    private boolean verifyEmail() {
        return (etEmail.getText().toString().matches("^(\\w+[@]\\w+[.]\\w+)$"));
    }

    private void verifyEmail(final String email) {
        RequestParams params = new RequestParams();
        params.addBodyParameter("email", email);

        HttpUtils http = new HttpUtils();
        verifyEmailHandler = http.send(HttpRequest.HttpMethod.POST,
                getString(R.string.host) + "verifyEmail",
                params,
                new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        if ("".equals(result))
                            return;
                        JSONObject object = JSON.parseObject(result);
                        int messageCode = object.getIntValue(HttpResultCode.MESSAGE_CODE);
                        if (messageCode == HttpResultCode.SUCCESS) {
                            code = CodeUtils.getRandomCode(6);
                            index = object.getString("userIndex");
                            sendVerificationCode(email, code);
                        } else if (messageCode == HttpResultCode.NO_ACCOUNT_ERROR) {
                            showToast(R.string.can_not_find_account);
                        } else if (messageCode == HttpResultCode.EMAIL_UNVERIFIED_ERROR) {
                            showToast(R.string.email_not_be_verified);
                        }


                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        showToast(R.string.service_connection_error);
                    }

                });
    }

    private void sendVerificationCode(String email, String code) {
        RequestParams params = new RequestParams();
        params.addBodyParameter("email", email);
        params.addBodyParameter("code", code);

        HttpUtils http = new HttpUtils();
        sendVerificationCodeHandler = http.send(HttpRequest.HttpMethod.POST,
                getString(R.string.host) + "sendForgotPasswordCode",
                params,
                new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        System.out.println(result);
                        if ("".equals(result))
                            return;
                        JSONObject object = JSON.parseObject(result);
                        int messageCode = object.getIntValue(HttpResultCode.MESSAGE_CODE);
                        if (messageCode != HttpResultCode.SUCCESS) {
                            showToast(R.string.service_connection_error);
                        } else {
                            tvMessage.setText(R.string.input_verification_code);
                            btnResend.setEnabled(false);
                            time = new TimeCount(60000, 1000);
                            time.start();
                            etEmail.setEnabled(false);
                            layoutVerificationCode.setVisibility(View.VISIBLE);
                            flagSubmit = true;
                            btnSubmit.setEnabled(false);
                        }

                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        showToast(R.string.service_connection_error);
                    }

                });
    }

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            btnResend.setText(R.string.resend);
            btnResend.setEnabled(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            String text = String.format(AppProperties.getContext().getString(R.string.resend_with_time), millisUntilFinished / 1000 - 1);
            btnResend.setText(text);
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showToast(int messageId) {
        Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (verifyEmailHandler != null)
            verifyEmailHandler.cancel();
        if (sendVerificationCodeHandler != null)
            sendVerificationCodeHandler.cancel();
    }
}
