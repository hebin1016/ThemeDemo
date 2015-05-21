package com.ericyl.themedemo.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ericyl.themedemo.R;

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
        layoutVerificationCode = findViewById(R.id.layout_verification_code);
        layoutVerificationCode.setVisibility(View.GONE);
        etVerificationCode = (EditText) findViewById(R.id.et_verification_code);
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
        switch (v.getId()){
            case R.id.btn_resend:
                break;
            case R.id.btn_submit:
                if (flagSubmit){

                }else{

                }
                break;
        }
    }

    private void jumpChangePasswordActivity(){

    }
}
