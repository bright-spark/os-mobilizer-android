package com.redmart.mobilizer.api;

import com.redmart.mobilizer.models.Branch;
import com.redmart.mobilizer.models.Project;
import com.redmart.mobilizer.models.ReleaseCollection;

import java.util.List;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by Joshua on 26/3/15.
 */
public interface ApiInterface {

    @GET("/api/projects/android")
    void getProjects(Callback<List<Project>> callback);

    @GET("/api/android/{repo}/branches")
    void getBranches(@Path("repo") String repo, Callback<List<Branch>> callback);

    @GET("/api/releases/android/{repo}")
    void getReleases(@Path("repo") String repo, Callback<ReleaseCollection> callback);

    @GET("/{url}")
    void getAPK(@Path(value = "url", encode = false) String url, Callback<Response> callback);
}
