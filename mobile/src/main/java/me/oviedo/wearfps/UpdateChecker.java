package me.oviedo.wearfps;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

class UpdateChecker {

    private static final String TAG = "UpdateChecker";
    private static boolean alreadyChecked = false;

    public static void check(Context context) {
        if (!alreadyChecked) {
            new Checker().execute(context);
            //alreadyChecked = true;
        }
    }

    public static void reset() {
        alreadyChecked = false;
    }

    static class Checker extends AsyncTask<Context, Void, Integer> {

        private Context context;

        @Override
        protected Integer doInBackground(Context... param) {
            context = param[0];
            PackageInfo packageInfo;
            try {
                packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                Log.w(TAG, "Could not find package info for " + context.getPackageName());
                return -1;
            }
            int version = packageInfo.versionCode;
            int remoteVersion = -1;


            URL url;
            InputStream is = null;
            BufferedReader br;
            String line;

            try {
                url = new URL("http://pc.oviedo.me/wfps/v");
                is = url.openStream();  // throws an IOException
                br = new BufferedReader(new InputStreamReader(is));

                if ((line = br.readLine()) != null) {
                    remoteVersion = Integer.parseInt(line);
                }
            } catch (MalformedURLException mue) {
                mue.printStackTrace();
            } catch (IOException ioe) {
                Log.w(TAG, "No se ha podido obtener la información de la actualización: " + ioe.getMessage());
            } finally {
                try {
                    if (is != null) is.close();
                } catch (IOException ioe) {
                    // nothing to see here
                }
            }

            if (remoteVersion > version) {
                Log.i(TAG, "Local version: " + version + ", remote version: " + remoteVersion);
                return remoteVersion;
            } else {
                Log.i(TAG, "Our version " + version + " is up to date (remote: " + remoteVersion + ")");
            }

            return -1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result > 0) {
                final String downloadUrl = "http://pc.oviedo.me/wfps/mobile-release-" + result + ".apk";
                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle("Update");
                alertDialog.setMessage("Hay una actualización disponible. ¿Descargar?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Sí",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(downloadUrl));
                                context.startActivity(i);
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
            super.onPostExecute(result);
        }
    }
}
