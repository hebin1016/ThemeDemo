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
import com.alibaba.fastjson.JSONObject;
import com.ericyl.themedemo.R;
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

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private ActionBar actionBar;

    private boolean showOldPassword = true;
    private String index;

    private EditText etOldPassword;
    private EditText etPassword;
    private Button btnSubmit;

    private ProgressDialog progressDialog = null;

    private HttpHandler verifyOldPasswordHandler;
    private HttpHandler changePasswordHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        showOldPassword = getIntent().getExtras().getBoolean("ShowOldPassword", true);
        index = getIntent().getExtras().getString("Index");
        init();
    }

    private void init() {
        initToolbar();

        etOldPassword = (EditText) findViewById(R.id.et_old_password);
        if (showOldPassword)
            etOldPassword.setVisibility(View.VISIBLE);
        else
            etOldPassword.setVisibility(View.GONE);
        etPassword = (EditText) findViewById(R.id.et_password);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(this);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.title_activity_change_password);
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
            case R.id.btn_submit:
                if (verifyPassword())
                    showInfoDialog();
                break;
        }
    }

    private boolean verifyPassword() {
        boolean flag = true;
        if (showOldPassword) {
            String oldPassword = etOldPassword.getText().toString();
            if (oldPassword == null || "".equals(oldPassword)) {
                etOldPassword.setError(getString(R.string.password_is_required));
                flag = false;
            }
        }
        String password = etPassword.getText().toString();
        if (password == null || "".equals(password)) {
            etPassword.setError(getString(R.string.password_is_required));
            flag = false;
        }
        return flag;
    }

    private void verifyOldPassword() {
        showProgressDialog();
        RequestParams params = new RequestParams();
        params.addBodyParameter("index", index);
        params.addBodyParameter("password", etOldPassword.getText().toString());

        HttpUtils http = new HttpUtils();
        verifyOldPasswordHandler = http.send(HttpRequest.HttpMethod.POST,
                getString(R.string.host) + "verifyPassword",
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
                            changePassword();
                        } else {
                            cancelProgressDialog();
                            etOldPassword.setError(getString(R.string.verify_password_is_error));
                        }

                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        showToast(R.string.service_connection_error);
                    }

                });
    }

    private void showInfoDialog() {
        DialogUtils.showDialogWithTwoBtn(this, getString(R.string.warn), getString(R.string.please_verify_info), getString(R.string.alp_42447968_cmd_continue), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (showOldPassword)
                    verifyOldPassword();
                else
                    changePassword();
            }
        });
    }

    private void changePassword() {
        showProgressDialog();

        RequestParams params = new RequestParams();
        params.addBodyParameter("index", index);
        params.addBodyParameter("password", etPassword.getText().toString());

        HttpUtils http = new HttpUtils();
        changePasswordHandler = http.send(HttpRequest.HttpMethod.POST,
                getString(R.string.host) + "changePassword",
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
                        JSONObject object = JSON.parseObject(result);
                        int messageCode = object.getIntValue(HttpResultCode.MESSAGE_CODE);
                        if (messageCode == HttpResultCode.SUCCESS) {
                            showToast(R.string.change_password_success);
                            finish();
                        } else {
                            showToast(R.string.change_password_failed);
                        }

                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        cancelProgressDialog();
                        showToast(R.string.service_connection_error);
                    }

                }

        );
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
        if (verifyOldPasswordHandler != null)
            verifyOldPasswordHandler.cancel();
        if (changePasswordHandler != null)
            changePasswordHandler.cancel();
        cancelProgressDialog();
    }


}
