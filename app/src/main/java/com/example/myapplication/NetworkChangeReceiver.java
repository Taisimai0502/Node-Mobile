package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
         Network network = connectivityManager.getActiveNetwork();
        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);

        boolean isConnected = capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));

        if (isConnected) {
            Toast.makeText(context, "Mạng đã được kết nối", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Mạng bị ngắt kết nối", Toast.LENGTH_SHORT).show();
        }

    }
}
