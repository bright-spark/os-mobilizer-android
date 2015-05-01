package com.redmart.mobilizer;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.redmart.mobilizer.adapters.SpinnerAdapter;
import com.redmart.mobilizer.events.NavigationMenuItemSelectedEvent;
import com.redmart.mobilizer.events.RefreshReleasesEvent;
import com.redmart.mobilizer.events.RepoBranchChangedEvent;
import com.redmart.mobilizer.events.RepoBranchesLoadedEvent;
import com.redmart.mobilizer.fragments.NavigationDrawerFragment;
import com.redmart.mobilizer.fragments.ReleasesFragment;
import com.redmart.mobilizer.models.Branch;
import com.redmart.mobilizer.models.User;
import com.redmart.mobilizer.utils.BusProvider;
import com.squareup.otto.Subscribe;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class HomeActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Toolbar mToolbar;
    private View mSpinnerContainer;
    private SpinnerAdapter mSpinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        mToolbar.setSubtitleTextAppearance(this, android.R.attr.textAppearanceSmall);
        mToolbar.setSubtitleTextColor(getResources().getColor(R.color.whiteAlphaTwo));
        setSupportActionBar(mToolbar);

        mSpinnerContainer = LayoutInflater.from(this).inflate(R.layout.toolbar_spinner, mToolbar, false);
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mToolbar.addView(mSpinnerContainer, lp);

        mSpinnerAdapter = new SpinnerAdapter(this);

        Spinner spinner = (Spinner) mSpinnerContainer.findViewById(R.id.toolbar_spinner);
        spinner.setAdapter(mSpinnerAdapter);
        spinner.setOnItemSelectedListener(this);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);

        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);

        User currentUser = MobilizerApp.get().getUser();
        mNavigationDrawerFragment.setUserData(currentUser.getName(), currentUser.getEmail(), currentUser.getImageUrl());
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen()) {
            mNavigationDrawerFragment.closeDrawer();
        } else {
            moveTaskToBack(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.home, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            finish();
            return true;
        } else if (id == R.id.action_refresh) {
            BusProvider.getInstance().post(new RefreshReleasesEvent());
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        BusProvider.getInstance().post(new RepoBranchChangedEvent(mSpinnerAdapter.getItems().get(position).getName()));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onNavigationMenuItemSelected(NavigationMenuItemSelectedEvent event) {
        mSpinnerAdapter.setRepo(event.getProject().getName());
        mSpinnerAdapter.clear();
        mSpinnerAdapter.notifyDataSetChanged();

        ReleasesFragment releasesFragment = new ReleasesFragment();
        Bundle bundle = new Bundle();
        bundle.putString("repo", event.getProject().getRepo());
        releasesFragment.setArguments(bundle);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container, releasesFragment).commit();
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onRepoBranchesLoadedEvent(RepoBranchesLoadedEvent event) {
        mSpinnerAdapter.clear();
        mSpinnerAdapter.addItems(event.getBranches());
        mSpinnerAdapter.notifyDataSetChanged();
    }
}
