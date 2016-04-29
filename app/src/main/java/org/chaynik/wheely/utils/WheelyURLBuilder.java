package org.chaynik.wheely.utils;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Map;


public class WheelyURLBuilder {

    public static URL createURL(Map<String, String> params) {
        Uri.Builder builder = Uri.parse(Const.BASE_URL).buildUpon();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.appendQueryParameter(entry.getKey(), entry.getValue());
        }
        URL url = null;
        try {
            url = new URL(builder.build().toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }
}
