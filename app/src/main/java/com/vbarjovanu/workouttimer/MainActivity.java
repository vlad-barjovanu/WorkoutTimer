package com.vbarjovanu.workouttimer;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.vbarjovanu.workouttimer.databinding.ActivityMainBinding;
import com.vbarjovanu.workouttimer.ui.generic.events.EventContent;
import com.vbarjovanu.workouttimer.ui.generic.viewmodels.CustomViewModelFactory;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    private IMainActivityViewModel mainActivityViewModel;

    private Observer<MainActivityModel> modelObserver;

    private Observer<EventContent<MainActivityActionData>> actionObserver;

    private ActivityMainBinding activityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initViewModel();

        this.activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton floatingActionButtonNewEntity = findViewById(R.id.floating_action_button_new);
        floatingActionButtonNewEntity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivityViewModel.newEntity();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_workouts, R.id.nav_slideshow,
                R.id.nav_userprofiles, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.removeModelObserver();
        this.removeActionObserver();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItem = menu.findItem(R.id.action_save);
        if (menuItem != null) {
            menuItem.setVisible(this.activityMainBinding.getMainActivityModel().isSaveEntityButtonVisible());
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (this.mainActivityViewModel != null && this.mainActivityViewModel.getModel() != null && this.mainActivityViewModel.getModel().getValue() != null) {
            switch (item.getItemId()) {
                case R.id.action_settings:
                    return false;
                case R.id.action_save:
                    this.mainActivityViewModel.saveEntity();
                    return true;
                case android.R.id.home:
                    if (this.mainActivityViewModel.getModel().getValue().isSaveEntityButtonVisible()) {
                        this.mainActivityViewModel.cancelEntity();
                        return true;
                    }
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
        return false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void initViewModel() {
        this.mainActivityViewModel = ViewModelProviders.of(this, CustomViewModelFactory.getInstance(this.getApplication())).get(IMainActivityViewModel.class);
        this.addActionObserver();
        this.addModelObserver();
        //ar trebui să o facă home fragment-ul + navigarea
        this.mainActivityViewModel.initUserProfile();
    }

    private void removeActionObserver() {
        if (this.mainActivityViewModel != null && this.actionObserver != null) {
            this.mainActivityViewModel.getAction().removeObserver(this.actionObserver);
            this.actionObserver = null;
        }
    }

    private void addActionObserver() {
        this.actionObserver = new Observer<EventContent<MainActivityActionData>>() {
            @Override
            public void onChanged(EventContent<MainActivityActionData> eventContent) {
                onActionChanged(eventContent);
            }
        };
        this.mainActivityViewModel.getAction().observe(this, this.actionObserver);
    }

    private void removeModelObserver() {
        if (this.mainActivityViewModel != null && this.modelObserver != null) {
            this.mainActivityViewModel.getModel().removeObserver(this.modelObserver);
            this.modelObserver = null;
        }
    }

    private void addModelObserver() {
        this.modelObserver = new Observer<MainActivityModel>() {
            @Override
            public void onChanged(MainActivityModel mainActivityModel) {
                onModelChanged(mainActivityModel);
            }
        };
        this.mainActivityViewModel.getModel().observe(this, this.modelObserver);
    }

    private void onModelChanged(MainActivityModel mainActivityModel) {
        this.activityMainBinding.setMainActivityModel(mainActivityModel);
        this.invalidateOptionsMenu();
    }

    private void onActionChanged(@NonNull EventContent<MainActivityActionData> eventContent) {
        MainActivityActionData mainActivityActionData;
        MainActivityAction mainActivityAction;
        mainActivityActionData = eventContent.getContent();
        if (mainActivityActionData != null) {
            mainActivityAction = mainActivityActionData.getAction();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            switch (mainActivityAction) {
                case GOTO_USERPROFILES:
                    eventContent.setHandled();
                    Toast.makeText(this, "Goto user profiles", Toast.LENGTH_LONG).show();
                    navController.navigate(R.id.action_nav_home_to_nav_userprofiles);
                    break;
                case GOTO_HOME:
                    eventContent.setHandled();
                    Toast.makeText(this, "Goto home", Toast.LENGTH_LONG).show();
                    navController.navigate(R.id.action_global_nav_home);
                    break;
                case EXIT:
                    eventContent.setHandled();
                    System.exit(1);
                    break;
            }
        }
    }
}
