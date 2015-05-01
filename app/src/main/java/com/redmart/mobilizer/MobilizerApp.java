package com.redmart.mobilizer;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.redmart.mobilizer.api.ApiConstants;
import com.redmart.mobilizer.api.ApiInterface;
import com.redmart.mobilizer.models.User;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by Joshua on 26/3/15.
 */
public class MobilizerApp extends Application {

    private static MobilizerApp sMobilizerApp;
    private static User sUser;

    private ApiInterface mApiInterface;
    private ApiInterface mApiInterfaceWithoutBase;

    public static MobilizerApp get() {
        return sMobilizerApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sMobilizerApp = this;

        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

        mApiInterface = new RestAdapter.Builder().setEndpoint(ApiConstants.BASEAPIURL).setConverter(new GsonConverter(gson)).build().create(ApiInterface.class);
        mApiInterfaceWithoutBase = new RestAdapter.Builder().setEndpoint("http:").setConverter(new GsonConverter(gson)).build().create(ApiInterface.class);

        SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                sUser = null;
            }
        };

        SharedPreferences mSharedPreference = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPreference.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    public ApiInterface getApiInterface(boolean absoluteUrl) {
        return absoluteUrl ? mApiInterfaceWithoutBase : mApiInterface;
    }

    public User getUser() {
        if (sUser == null) {
            sUser = new User();
        }

        return sUser;
    }

    public void reset() {
        sUser = null;
    }
}
