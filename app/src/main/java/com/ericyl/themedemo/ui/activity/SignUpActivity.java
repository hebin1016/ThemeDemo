package com.ericyl.themedemo.ui.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.ericyl.themedemo.R;
import com.ericyl.themedemo.model.User;
import com.ericyl.themedemo.util.AppProperties;
import com.ericyl.themedemo.util.DialogUtils;
import com.ericyl.themedemo.util.HttpResultCode;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private ActionBar actionBar;

    private EditText etUsername;
    private EditText etEmail;
    private EditText etPassword;
    private Button btnSignUp;

    private ProgressDialog progressDialog = null;

    private HttpHandler signUpHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        init();
    }

    private void init() {
        initToolbar();

        etUsername = (EditText) findViewById(R.id.et_username);
        etEmail = (EditText) findViewById(R.id.et_email);
        etPassword = (EditText) findViewById(R.id.et_password);
        btnSignUp = (Button) findViewById(R.id.btn_sign_up);
        btnSignUp.setOnClickListener(this);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.sign_up_new_account);
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
            case R.id.btn_sign_up:
                if (verifyInfo())
                    showInfoDialog();
                break;
        }
    }

    private boolean verifyInfo() {
        boolean flag = true;
        String username = etUsername.getText().toString();
        if (username == null || "".equals(username)) {
            etUsername.setError(getString(R.string.username_is_required));
            flag = false;
        }

        String email = etEmail.getText().toString();
        if (email == null || "".equals(email)) {
            etEmail.setError(getString(R.string.email_is_required));
            flag = false;
        }else if(!etEmail.getText().toString().matches("^(\\w+[@]\\w+[.]\\w+)$")){
            etEmail.setError("email error");
            flag = false;
        }

        String password = etPassword.getText().toString();
        if (password == null || "".equals(password)) {
            etPassword.setError(getString(R.string.password_is_required));
            flag = false;
        }

        return flag;
    }

    private void showInfoDialog() {
        DialogUtils.showDialogWithTwoBtn(this, getString(R.string.warn), getString(R.string.please_verify_info), getString(R.string.alp_42447968_cmd_continue), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                signUp();
            }
        });
    }

    private void signUp() {
        showProgressDialog();


        String username = etUsername.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        User user = new User(username, password, email);

        RequestParams params = new RequestParams();
        params.addBodyParameter("userInfo", JSON.toJSONString(user));

        HttpUtils http = new HttpUtils();
        signUpHandler = http.send(HttpRequest.HttpMethod.POST,
                getString(R.string.host) + "signUp",
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
                        cancelProgressDialog();
                        String result = responseInfo.result;
                        if ("".equals(result))
                            return;
                        com.alibaba.fastjson.JSONObject object = JSON.parseObject(result);
                        int messageCode = object.getIntValue(HttpResultCode.MESSAGE_CODE);
                        if (messageCode == HttpResultCode.SUCCESS) {
                            showToast(R.string.sign_up_success);
                            finish();
                        } else {
                            showToast(R.string.sign_up_failed);
                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        cancelProgressDialog();
                        showToast(R.string.service_connection_error);
                    }

                });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showToast(int messageId) {
        Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show();
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(AppProperties.getContext().getString(R.string.waiting));
            progressDialog.show();
            progressDialog.setCancelable(false);
        } else if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void cancelProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.cancel();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (signUpHandler != null)
            signUpHandler.cancel();
        cancelProgressDialog();
    }


}
