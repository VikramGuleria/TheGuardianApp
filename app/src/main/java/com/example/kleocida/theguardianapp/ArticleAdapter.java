package com.example.kleocida.theguardianapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Kleocida on 2017. 06. 04..
 */

class ArticleAdapter extends ArrayAdapter<ArticleData> {
    private static final String LOG_TAG = ArticleAdapter.class.getSimpleName();

    ArticleAdapter(Context context, List<ArticleData> articleList) {
        super(context, -1, articleList);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate
                    (R.layout.item_list_layout, parent, false);
        }

        ArticleData currentArticle = getItem(position);

        if (currentArticle != null) {
            ImageView articleThumbnail = (ImageView) listItemView.findViewById(R.id.article_thumbnail_image_view);
            articleThumbnail.setImageBitmap(formatImageFromURL(currentArticle.getArticleThumbnail()));
            TextView section = (TextView) listItemView.findViewById(R.id.article_section_text_view);
            section.setText(currentArticle.getArticleSection());
            TextView title = (TextView) listItemView.findViewById(R.id.article_title_text_view);
            title.setText(currentArticle.getArticleTitle());
            TextView publishTime = (TextView) listItemView.findViewById(R.id.article_publish_time_text_view);
            publishTime.setText(formatPublishTime(currentArticle.getArticlePublishTime()));
        }
        return listItemView;
    }

    private String formatPublishTime(final String time) {
        String rTime = "N.A.";
        if ((time != null) && (!time.isEmpty())) {
            try {
                SimpleDateFormat currentSDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                SimpleDateFormat newSDF = new SimpleDateFormat("yyyy.MM.dd / HH:mm");//("MM dd, yyyy");
                rTime = newSDF.format(currentSDF.parse(time));
            } catch (ParseException parseEx) {
                rTime = "N.A.";
                Log.e(LOG_TAG, "Error while parsing the published date", parseEx);
            }
        }

        return rTime;
    }

    private Bitmap formatImageFromURL(Bitmap articleThumbnail) {
        Bitmap returnBitmap;
        if (articleThumbnail == null) {
            returnBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.placeholder);
        } else {
            returnBitmap = articleThumbnail;
        }
        return returnBitmap;
    }
}
