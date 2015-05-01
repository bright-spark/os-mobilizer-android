package com.redmart.mobilizer.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pnikosis.materialishprogress.ProgressWheel;
import com.redmart.mobilizer.MobilizerApp;
import com.redmart.mobilizer.R;
import com.redmart.mobilizer.adapters.ReleaseAdapter;
import com.redmart.mobilizer.events.InstallButtonClickedEvent;
import com.redmart.mobilizer.events.RefreshReleasesEvent;
import com.redmart.mobilizer.events.RepoBranchChangedEvent;
import com.redmart.mobilizer.events.RepoBranchesLoadedEvent;
import com.redmart.mobilizer.events.UninstallButtonClickedEvent;
import com.redmart.mobilizer.models.Branch;
import com.redmart.mobilizer.models.Release;
import com.redmart.mobilizer.models.ReleaseCollection;
import com.redmart.mobilizer.utils.BusProvider;
import com.squareup.otto.Subscribe;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Joshua on 26/3/15.
 */
public class ReleasesFragment extends BaseFragment {

    private String mRepo;
    private String mCurrentBranch;
    private RecyclerView mReleasesList;
    private ProgressWheel mProgressBar;
    private ReleaseAdapter mReleaseAdapter;
    private HashMap<String, List<Release>> mReleaseCollection = new HashMap<>();
    private List<Release> mReleases = new ArrayList<>();
    private List<Branch> mBranches = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_releases, container, false);

        mReleasesList = (RecyclerView) view.findViewById(R.id.releasesList);
        mProgressBar = (ProgressWheel) view.findViewById(R.id.progressBar);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mReleasesList.setLayoutManager(layoutManager);
        mReleasesList.setHasFixedSize(true);

        mReleaseAdapter = new ReleaseAdapter(getActivity(), mReleases);
        mReleasesList.setAdapter(mReleaseAdapter);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRepo = getArguments().getString("repo");

        getRepoBranches();
    }

    @Override
    public void onResume() {
        super.onResume();
        mReleaseAdapter.setCurrentInstallId(0);
        mReleaseAdapter.setCurrentUnInstallId(0);
    }

    private void filterRelease() {
        mReleases.clear();

        Iterator<String> branchIterator = mReleaseCollection.keySet().iterator();
        while (branchIterator.hasNext()) {
            String branch = branchIterator.next();
            if (branch.equals(mCurrentBranch)) {
                mReleases.addAll(mReleaseCollection.get(branch));
            }
        }

        mReleaseAdapter.notifyDataSetChanged();
        mReleasesList.smoothScrollToPosition(0);
    }

    private void getRepoBranches() {
        MobilizerApp.get().getApiInterface(false).getBranches(mRepo, new Callback<List<Branch>>() {
            @Override
            public void success(List<Branch> branchList, Response response) {
                mCurrentBranch = branchList.get(0).getName();
                mBranches = branchList;
                BusProvider.getInstance().post(new RepoBranchesLoadedEvent(branchList));
                getReleases();
            }

            @Override
            public void failure(RetrofitError error) {
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getReleases() {
        MobilizerApp.get().getApiInterface(false).getReleases(mRepo, new Callback<ReleaseCollection>() {
            @Override
            public void success(ReleaseCollection releaseCollection, Response response) {
                mReleaseCollection = releaseCollection.getReleases();

                filterRelease();

                mProgressBar.setVisibility(View.GONE);
                mReleasesList.setVisibility(View.VISIBLE);
            }

            @Override
            public void failure(RetrofitError error) {
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onRepoBranchChangedEvent(RepoBranchChangedEvent event) {
        mCurrentBranch = event.getBranch();
        filterRelease();
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onInstallButtonClickedEvent(InstallButtonClickedEvent event) {
        String url = event.getDownloadLink().replace("http:/", "");
        MobilizerApp.get().getApiInterface(true).getAPK(url, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    String fileName = response.getUrl().substring(response.getUrl().lastIndexOf('/') + 1);
                    File file = new File(getActivity().getFilesDir() + File.separator + fileName);

                    FileOutputStream output = getActivity().openFileOutput(fileName, Context.MODE_WORLD_READABLE);
                    IOUtils.write(IOUtils.toByteArray(response.getBody().in()), output);

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(new File(getActivity().getFilesDir() + File.separator + fileName)), "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (IOException e) {
                    Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                mReleaseAdapter.setCurrentInstallId(0);
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onUninstallButtonClickedEvent(UninstallButtonClickedEvent event) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + event.getPackageName()));
        startActivity(intent);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onRefreshReleasesEvent(RefreshReleasesEvent event) {
        mReleases.clear();
        mReleaseAdapter.notifyDataSetChanged();

        mBranches.clear();
        BusProvider.getInstance().post(new RepoBranchesLoadedEvent(mBranches));

        mProgressBar.setVisibility(View.VISIBLE);
        mReleasesList.setVisibility(View.GONE);

        getRepoBranches();
    }
}
