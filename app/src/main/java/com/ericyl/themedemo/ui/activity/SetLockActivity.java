package com.ericyl.themedemo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.ericyl.themedemo.R;
import com.ericyl.themedemo.utils.Settings;

public class SetLockActivity extends ActionBarActivity {

    private static final int REQ_CREATE_PATTERN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_lock);
        Intent intent = new Intent(LockPatternActivity.ACTION_CREATE_PATTERN, null,
                this, LockPatternActivity.class);
        startActivityForResult(intent, REQ_CREATE_PATTERN);
        Settings.Security.setAutoSavePattern(this, true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_set_lock, menu);
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
            case REQ_CREATE_PATTERN: {
                if (resultCode == RESULT_OK) {
                    char[] pattern = data.getCharArrayExtra(
                            LockPatternActivity.EXTRA_PATTERN);
                }

                break;
            }// REQ_CREATE_PATTERN
        }
    }

}
