package com.ericyl.themedemo.ui.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.ericyl.themedemo.R;
import com.ericyl.themedemo.utils.Settings;

public class LoginActivity extends ActionBarActivity {


    private static final int REQ_ENTER_PATTERN = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        char[] pattern =
                Settings.Security.getPattern(this);
        if (pattern != null) {

            Intent intent = new Intent(LockPatternActivity.ACTION_COMPARE_PATTERN, null,
                    this, LockPatternActivity.class);
            intent.putExtra(LockPatternActivity.EXTRA_PATTERN, pattern);


            Settings.Display.setCaptchaWiredDots(this, 9);
            Intent intent1 = new Intent(LockPatternActivity.ACTION_VERIFY_CAPTCHA, null,
                    this, LockPatternActivity.class);
            PendingIntent piForgotPattern = PendingIntent.getActivity(this, 1, intent1, 0);

//            intent.putExtra(LockPatternActivity.EXTRA_RETRY_COUNT, 1000000);


            intent.putExtra(
                    LockPatternActivity.EXTRA_PENDING_INTENT_FORGOT_PATTERN,
                    piForgotPattern);

            startActivityForResult(intent, REQ_ENTER_PATTERN);
        } else {
            Intent intent = new Intent(
                    this, SetLockActivity.class);
            startActivity(intent);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        switch (requestCode) {
            case REQ_ENTER_PATTERN: {
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
                System.out.println(retryCount+"");

                break;
            }// REQ_ENTER_PATTERN
        }
    }
}
