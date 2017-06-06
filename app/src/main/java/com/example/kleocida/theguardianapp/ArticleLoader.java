package com.example.kleocida.theguardianapp;

/**
 * Created by Kleocida on 2017. 06. 04..
 */

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * Loads a list of Article by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class ArticleLoader extends AsyncTaskLoader {

    private Bundle pBundle;


    public ArticleLoader(final Context context, final Bundle bundle) {
        super(context);
        pBundle = bundle;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    @Nullable
    public Object loadInBackground() {
        List<ArticleData> articleList = null;
        if (pBundle != null) {
            articleList = QueryUtils.fetchArticleData(pBundle.getString("uri"));
        }
        return articleList;
    }
}
