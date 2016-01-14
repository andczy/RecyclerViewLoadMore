package com.example.czy.recyclerviewloadmore;

import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.czy.recyclerviewloadmore.adapter.IntAdapter;
import com.example.czy.recyclerviewloadmore.adapter.LoadMoreAdapter;
import com.example.czy.recyclerviewloadmore.adapter.StringAdapter;
import com.example.czy.recyclerviewloadmore.model.IntModel;
import com.example.czy.recyclerviewloadmore.model.StringModel;
import com.example.czy.recyclerviewloadmore.widget.PullDownLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_main2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements LoadMoreAdapter.OnLoadMoreListener , AdapterView.OnItemClickListener {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private int index;
        int currentPage ;
        private IntAdapter adapter;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            final PullDownLayout pullDownLayout = (PullDownLayout) rootView.findViewById(R.id.pulldown);
            pullDownLayout.setOnRefreshListener(new PullDownLayout.OnRefreshListener() {
                @Override
                public void refresh() {
                    Toast.makeText(getActivity() , "快看，你下拉啦！"  , Toast.LENGTH_LONG).show();
                    new AsyncTask<Void , Void , Void>(){

                        @Override
                        protected Void doInBackground(Void... params) {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            pullDownLayout.refreshOver();
                        }
                    }.execute();
                }
            });
            RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
            index = getArguments().getInt(ARG_SECTION_NUMBER) ;
            switch (index)
            {
                case 1:
                    ArrayList<IntModel> models = new ArrayList<>();
                    for(int i = 0 ; i < 10 ; i ++ )
                    {
                        IntModel model = new IntModel();
                        model.id = i ;
                        models.add(model);
                    }
                    adapter = new IntAdapter();
                    adapter.setData(models);
                    adapter.setLoadMoreListener(this);
                    recyclerView.setAdapter(adapter);
                    adapter.setOnItemClickListener(this);
                    break ;
                case 2:
                    ArrayList<StringModel> models1 = new ArrayList<>();
                    for(int i = 0 ; i < 9 ; i ++ )
                    {
                        StringModel model = new StringModel();
                        model.name = String.valueOf((char)(65 + i )) ;
                        models1.add(model);
                    }
                    StringAdapter stringAdapter = new StringAdapter();
                    stringAdapter.setData(models1);
                    if(models1.size()%10!=0)
                    {
                        stringAdapter.setCanLoadMore(false);
                    }
                    recyclerView.setAdapter(stringAdapter);
                    break ;
                case 3:
                    recyclerView.setAdapter(new StringAdapter());
                    break ;
            }
            return rootView;
        }

        @Override
        public void loadMore() {
            currentPage++ ;
            new AsyncTask<Void , Void , List<IntModel>>(){

                @Override
                protected List<IntModel> doInBackground(Void... params) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ArrayList<IntModel> models = new ArrayList<>();
                    int start = currentPage * 10 ;
                    for(int i = 0 ; i < 10 ; i ++ )
                    {
                        IntModel model = new IntModel();
                        model.id = i + start ;
                        models.add(model);
                    }
                    return models;
                }

                @Override
                protected void onPostExecute(List<IntModel> intModels) {
                    if(intModels == null || intModels.isEmpty())
                        adapter.setCanLoadMore(false);
                    else if(intModels.size()!=10)
                    {
                        adapter.setCanLoadMore(false);
                        adapter.addData(intModels);
                    }else{
                        adapter.addData(intModels);
                    }
                    adapter.loadOver();
                }
            }.execute();
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            IntModel model = adapter.getItem(position);
            Toast.makeText(view.getContext() , "click on me " + model.id , Toast.LENGTH_LONG).show();
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
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
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }
}
