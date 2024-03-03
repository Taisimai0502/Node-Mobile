package com.example.myapplication;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class ExampleAppWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context, MainActivity.class);
            updateAppWidget(context, appWidgetManager, appWidgetId);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onEnabled(Context context) {

    }


    @Override
    public void onDisabled(Context context) {

    }


    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {

    }


    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                 int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);


        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        views.setTextViewText(R.id.appwidget_text, widgetText);


        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}
