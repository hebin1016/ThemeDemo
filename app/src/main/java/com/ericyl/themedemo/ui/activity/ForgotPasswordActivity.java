package com.ericyl.themedemo.ui.activity;

import android.os.Bundle;
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

import com.ericyl.themedemo.R;
import com.ericyl.themedemo.util.CodeUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;


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
    private String code;

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
        etEmail.addTextChangedListener(new EmailTextWatcher());
        layoutVerificationCode = findViewById(R.id.layout_verification_code);
        layoutVerificationCode.setVisibility(View.GONE);
        etVerificationCode = (EditText) findViewById(R.id.et_verification_code);
        etVerificationCode.addTextChangedListener(new VerificationCodeTextWatcher());
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
                break;
            case R.id.btn_submit:
                if (flagSubmit) {

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

    private void jumpChangePasswordActivity() {

    }

    class EmailTextWatcher implements TextWatcher {

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

    class VerificationCodeTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().length() > 0)
                btnSubmit.setEnabled(true);
            else
                btnSubmit.setEnabled(false);
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
                        int result = Integer.parseInt(responseInfo.result);
                        if (result == 200) {
                            etEmail.setEnabled(false);
                            layoutVerificationCode.setVisibility(View.VISIBLE);
                            code = CodeUtils.getRandomCode(6);
                            sendVerificationCode(email, code);
                        } else if (result == 501) {
                            Toast.makeText(ForgotPasswordActivity.this, R.string.can_not_find_account, Toast.LENGTH_SHORT).show();
                        } else if (result == 502) {
                            Toast.makeText(ForgotPasswordActivity.this, R.string.email_not_be_verified, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        Toast.makeText(ForgotPasswordActivity.this, R.string.service_connection_error, Toast.LENGTH_SHORT).show();
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
                        if (!"".equals(result)){
                            try {
                                JSONObject object = new JSONObject(result);
                                int code = object.getInt("messageCode");
                                if(code != 200){
                                    Toast.makeText(ForgotPasswordActivity.this, R.string.service_connection_error, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        Toast.makeText(ForgotPasswordActivity.this, R.string.service_connection_error, Toast.LENGTH_SHORT).show();
                    }

                });
    }

}
