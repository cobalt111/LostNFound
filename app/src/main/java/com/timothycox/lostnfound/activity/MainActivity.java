package com.timothycox.lostnfound.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.widget.Toast;

import com.timothycox.lostnfound.fragment.HomeFragment;
import com.timothycox.lostnfound.fragment.ListingsFragment;
import com.timothycox.lostnfound.R;
import com.timothycox.lostnfound.utilities.FileUtils;
import com.timothycox.lostnfound.fragment.YourPetsFragment;

import java.util.List;


public class MainActivity extends AppCompatActivity implements ListingsFragment.OnMenuItemSelectedListener {


    private static final int WRITE_STORAGE_REQUEST = 3;


    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                getApplicationContext().startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    // The BottomNavigationView creates the 3 buttons on the bottom which switch the fragments
    // between HomeFragment, ListingsFragment, and YourPetsFragment
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    setTitle("Home");
                    mViewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_listings:
                    setTitle("Listings");
                    mViewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_animals:
                    setTitle("Your Pets");
                    mViewPager.setCurrentItem(2);
                    return true;
            }
            return false;
        }

    };

    // Basic options menu, this will be overridden by ListingsFragment
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getTitle().toString()) {
            case "Map":
                startActivity(new Intent(MainActivity.this, Map.class));
                Log.d("menu", "map yes");
                return true;
        }
        Log.d("menu", item.getTitle().toString());
        return false;
    }

    @Override
    public void onMenuItemSelected(String filter) {

    }



    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);


        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_STORAGE_REQUEST);
        }





        // initialize nav bar, set listener
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //Log.d("scrolled", Float.toString(positionOffset));

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 2) {

                    List<String> animalLinkedList = FileUtils.readFromFile(getApplicationContext());

                    if (animalLinkedList.size() == 0) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Your list of animals is empty!", Toast.LENGTH_LONG);
                        toast.show();
                    }
                }

            }

            int previousState;
            @Override
            public void onPageScrollStateChanged(int state) {
                if (previousState == ViewPager.SCROLL_STATE_DRAGGING && state == ViewPager.SCROLL_STATE_SETTLING) {
                    //user scroll
                    //Log.d("nav", "user scroll");
                }

                else if (previousState == ViewPager.SCROLL_STATE_SETTLING && state == ViewPager.SCROLL_STATE_IDLE) {
                    //programmatic scroll
                    //Log.d("nav", "programmatic scroll");
                }

                previousState = state;
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();

//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//            showSettingsAlert();
//            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                finish();
//            }
//        }

        mSectionsPagerAdapter.notifyDataSetChanged();
//        mViewPager.setAdapter(null);
//        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(Object object){
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a SecurityAuditFragment (defined as a static inner class below).
            // return SecurityAuditFragment.newInstance(position + 1);
            switch (position) {
                case 0:
                    return HomeFragment.newInstance(position+1);
                case 1:
                    return ListingsFragment.newInstance(position+1);
                case 2:
                    return YourPetsFragment.newInstance(position+1);
                default:
                    return HomeFragment.newInstance(position+1);
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    setTitle("Home");
                    return "page 1";
                case 1:
                    setTitle("Listings");
                    return "page 2";
                case 2:
                    return "page 3";
            }
            return null;
        }
    }
}
