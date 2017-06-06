package com.example.kleocida.theguardianapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.net.Uri;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<ArticleData>> {

    private ArticleAdapter articleAdapter;
    private static final int ARTICLE_LOADER_ID = 1;
    private ListViewCompat articleListView = null;
    private TextView emptyView;
    private LinearLayoutCompat progressBarLayout;
    private String searchString = "";
    private String searchSection = null;
    private DrawerLayout drawerLayout = null;
    private ActionBarDrawerToggle drawerToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            searchSection = savedInstanceState.getString(getString(R.string.search_section));
            searchString = savedInstanceState.getString(getString(R.string.search_string));
        }
        if (searchSection == null) {
            setTitle(getString(R.string.fresh_news) + searchString);
        } else {
            if (searchString.equals("")) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(getString(R.string.news) + searchSection);
                }
            } else {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(getString(R.string.news) + searchSection + "/" + searchString);
                }
            }
        }

        emptyView = (TextView) findViewById(R.id.empty);
        emptyView.setText(R.string.no_news_found);
        articleListView = (ListViewCompat) findViewById(R.id.article_list_view);
        articleAdapter = new ArticleAdapter(MainActivity.this, new ArrayList<ArticleData>());
        articleListView.setAdapter(articleAdapter);
        articleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                final ArticleData articleSelected = articleAdapter.getItem(position);
                if (articleSelected != null && articleSelected.getArticleURL() != null) {

                    Uri articleUrl = Uri.parse(articleSelected.getArticleURL());

                    Intent urlIntent = new Intent(Intent.ACTION_VIEW, articleUrl);
                    PackageManager packageManager = getPackageManager();
                    List<ResolveInfo> activities = packageManager.queryIntentActivities(urlIntent, 0);
                    boolean isIntentSafe = activities.size() > 0;

                    if (isIntentSafe) {
                        startActivity(urlIntent);
                    }

                }
            }
        });

        progressBarLayout = (LinearLayoutCompat) findViewById(R.id.progress_bar_layout);

        if (checkNetworkConnection()) {
            handleQuery(searchString);
            getLoaderManager().initLoader(ARTICLE_LOADER_ID, null, MainActivity.this);
        } else {
            articleListView.setEmptyView(emptyView);
            if (articleAdapter != null) {
                articleAdapter.clear();
            }
            progressBarLayout.setVisibility(View.GONE);
            emptyView.setText(R.string.no_internet_connection);
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ListView drawerList = (ListView) findViewById(R.id.drawer_list);
        View header = getLayoutInflater().inflate(R.layout.nav_header, null);
        drawerList.addHeaderView(header);
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.drawer_list_item_layout, getResources().getStringArray(R.array.navigation_drawer_list));
        drawerList.setAdapter(adapter);
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                switch (position) {
                    case 1:
                        searchSection = null;
                        drawerLayout.closeDrawer(drawerList);
                        progressBarLayout.setVisibility(View.VISIBLE);
                        articleListView.setVisibility(View.GONE);
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setTitle(getString(R.string.fresh_news));
                        }

                        if (checkNetworkConnection()) {
                            handleQuery(searchString);
                        } else {
                            articleListView.setEmptyView(emptyView);
                            if (articleAdapter != null) {
                                articleAdapter.clear();
                            }
                            progressBarLayout.setVisibility(View.GONE);
                            emptyView.setText(getString(R.string.no_internet_connection));
                        }
                        break;

                    case 2:
                        handleNavigationDrawerClick(getString(R.string.world), drawerLayout, drawerList);
                        break;
                    case 3:
                        handleNavigationDrawerClick(getString(R.string.sport), drawerLayout, drawerList);
                        break;
                    case 4:
                        handleNavigationDrawerClick(getString(R.string.football), drawerLayout, drawerList);
                        break;
                    case 5:
                        handleNavigationDrawerClick(getString(R.string.culture), drawerLayout, drawerList);
                        break;
                    case 6:
                        handleNavigationDrawerClick(getString(R.string.business), drawerLayout, drawerList);
                        break;
                    case 7:
                        handleNavigationDrawerClick(getString(R.string.fashion), drawerLayout, drawerList);
                        break;
                    case 8:
                        handleNavigationDrawerClick(getString(R.string.technology), drawerLayout, drawerList);
                        break;
                    case 9:
                        handleNavigationDrawerClick(getString(R.string.travel), drawerLayout, drawerList);
                        break;
                    case 10:
                        handleNavigationDrawerClick(getString(R.string.money), drawerLayout, drawerList);
                        break;
                    case 11:
                        handleNavigationDrawerClick(getString(R.string.science), drawerLayout, drawerList);
                        break;
                }
            }
        });
        setupDrawer();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    private void setupDrawer() {
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                searchString = "";
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(R.string.navigation);
                }
                invalidateOptionsMenu();
            }

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                // Set title text
                if (getSupportActionBar() != null) {
                    if (searchSection != null) {
                        if (searchString.equals("")) {
                            getSupportActionBar().setTitle(getString(R.string.news) + searchSection);
                        } else {
                            getSupportActionBar().setTitle(getString(R.string.news) + searchSection + "/" + searchString);
                        }
                    } else {
                        getSupportActionBar().setTitle(getString(R.string.fresh_news) + searchString);
                    }
                }
                invalidateOptionsMenu();
            }
        };
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        final MenuItem item = menu.findItem(R.id.toolbar_search_item);
        final SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                progressBarLayout.setVisibility(View.VISIBLE);
                articleListView.setVisibility(View.GONE);
                searchView.onActionViewCollapsed();
                searchString = query;
                if (searchSection == null) {
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(getString(R.string.news_for) + searchString);
                    }
                } else {
                    if (searchString.equals("")) {
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setTitle(getString(R.string.news) + searchSection);
                        }
                    } else {
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setTitle(getString(R.string.news) + searchSection + "/" + searchString);
                        }
                    }
                }
                if (checkNetworkConnection()) {
                    handleQuery(query);
                } else {
                    articleListView.setEmptyView(emptyView);
                    if (articleAdapter != null) {
                        articleAdapter.clear();
                    }
                    progressBarLayout.setVisibility(View.GONE);
                    emptyView.setText(getString(R.string.no_internet_connection));
                }
                return false;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    private void handleQuery(String searchStringQ) {

        emptyView.setVisibility(View.GONE);
        String url;
        if (searchSection == null) {
            url = getString(R.string.base_url) + searchStringQ + getString(R.string.url_tags) + getString(R.string.url_end);
        } else {
            url = getString(R.string.base_url) + searchStringQ + getString(R.string.url_section) + searchSection + getString(R.string.url_end);
        }
        Bundle args = new Bundle();
        args.putString("uri", url);
        getLoaderManager().restartLoader(ARTICLE_LOADER_ID, args, MainActivity.this);
        articleListView.smoothScrollToPosition(0);
    }

    @Override
    public Loader<List<ArticleData>> onCreateLoader(int id, Bundle args) {

        return new ArticleLoader(MainActivity.this, args);
    }

    @Override
    public void onLoadFinished(Loader<List<ArticleData>> loader, List<ArticleData> data) {
        if (articleAdapter != null)
            articleAdapter.clear();
        if (data != null && data.size() > 0) {
            articleAdapter.addAll(data);
            progressBarLayout.setVisibility(View.GONE);
            articleListView.setVisibility(View.VISIBLE);
        } else {
            articleListView.setVisibility(View.GONE);
            articleListView.setEmptyView(emptyView);
            if (articleAdapter != null) {
                articleAdapter.clear();
            }
            progressBarLayout.setVisibility(View.GONE);
            emptyView.setText(getString(R.string.no_news_found));
        }
    }

    @Override
    public void onLoaderReset(Loader<List<ArticleData>> loader) {
        if (articleAdapter != null) {
            articleAdapter.clear();
        }
    }

    protected void handleNavigationDrawerClick(String sString, DrawerLayout dLayout, ListView dList) {
        searchSection = sString;
        dLayout.closeDrawer(dList);
        progressBarLayout.setVisibility(View.VISIBLE);
        articleListView.setVisibility(View.GONE);
        if (getSupportActionBar() != null) {
            if (searchString.equals("")) {
                getSupportActionBar().setTitle(getString(R.string.news) + searchSection);
            } else {
                getSupportActionBar().setTitle(getString(R.string.news) + searchSection + "/" + searchString);
            }
        }
        if (checkNetworkConnection()) {
            handleQuery(searchString);
        } else {
            articleListView.setEmptyView(emptyView);
            if (articleAdapter != null) {
                articleAdapter.clear();
            }
            progressBarLayout.setVisibility(View.GONE);
            emptyView.setText(getString(R.string.no_internet_connection));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(getString(R.string.search_string), searchString);
        outState.putString(getString(R.string.search_section), searchSection);
    }

    protected boolean checkNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
