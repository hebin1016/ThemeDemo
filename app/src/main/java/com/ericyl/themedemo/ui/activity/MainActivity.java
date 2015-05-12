package com.ericyl.themedemo.ui.activity;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ericyl.themedemo.R;
import com.ericyl.themedemo.ui.fragment.MainFragment;

public class MainActivity extends AppCompatActivity {

    private int oldPosition;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private View layoutContent;
    private ActionBar mActionBar;
    private Toolbar mToolbar;
    private View contennt;

    private View layoutDrawerMenu;
    private TextView tvMenu;
    private ListView lvMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        initToolBar();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.layout_drawer);

        layoutDrawerMenu = findViewById(R.id.layout_drawer_menu);

        tvMenu = (TextView) findViewById(R.id.tv_menu);
        tvMenu.setText("ABBBCC");

        lvMenu = (ListView) findViewById(R.id.lv_menu);
        lvMenu.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_expandable_list_item_1, getResources().getStringArray(R.array.drawer_array)));


        lvMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getSupportFragmentManager().beginTransaction().replace(R.id.content, MainFragment.newInstance(null, null)).commit();
                oldPosition = position;
                mDrawerLayout.closeDrawer(layoutDrawerMenu);
            }
        });
        initDrawerLayout();

    }

    private void initDrawerLayout() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open_drawer, R.string.close_drawer) {
            public void onDrawerClosed(View view) {
                supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                supportInvalidateOptionsMenu();
            }
        };
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }

    private void initToolBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("AppThemeDemo");
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
    }

}
