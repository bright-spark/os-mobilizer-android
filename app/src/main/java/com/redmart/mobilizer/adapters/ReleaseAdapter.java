package com.redmart.mobilizer.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.redmart.mobilizer.MobilizerApp;
import com.redmart.mobilizer.R;
import com.redmart.mobilizer.events.InstallButtonClickedEvent;
import com.redmart.mobilizer.events.UninstallButtonClickedEvent;
import com.redmart.mobilizer.models.Release;
import com.redmart.mobilizer.utils.BusProvider;

import java.util.List;

/**
 * Created by Joshua on 26/3/15.
 */
public class ReleaseAdapter extends RecyclerView.Adapter<ReleaseAdapter.ReleaseViewHolder> {

    private List<Release> mReleaseList;
    private Context mContext;
    private long mCurrentInstallId;
    private boolean mCurrentInstallIsAlpha;
    private long mCurrentUnInstallId;
    private boolean mCurrentUnInstallIsAlpha;

    public ReleaseAdapter(Context context, List<Release> releaseList) {
        this.mReleaseList = releaseList;
        this.mContext = context;
    }

    public void setCurrentInstallId(long currentInstallId) {
        this.mCurrentInstallId = currentInstallId;
        notifyDataSetChanged();
    }

    public void setCurrentUnInstallId(long currentUnInstallId) {
        this.mCurrentUnInstallId = currentUnInstallId;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mReleaseList.size();
    }

    @Override
    public void onBindViewHolder(final ReleaseViewHolder releaseViewHolder, int position) {
        final Release release = mReleaseList.get(position);

        releaseViewHolder.title.setText(release.getName() + " (" + (release.isAlpha() ? "Alpha" : "Beta") + ")");
        releaseViewHolder.changeLog.setText(release.getBody());
        releaseViewHolder.date.setText(release.getReleaseDate());

        try {
            PackageInfo manager= mContext.getPackageManager().getPackageInfo(release.isAlpha() ? release.getAlphaPackageName() : release.getBetaPackageName(), 0);
            releaseViewHolder.installBtn.setTag(R.id.version_name, manager.versionName);
            releaseViewHolder.installBtn.setTag(R.id.is_alpha, release.isAlpha());
        } catch (PackageManager.NameNotFoundException e) {
            releaseViewHolder.installBtn.setTag(null);
        }

        if (release.getVersionName() != null && release.getVersionName().equals(releaseViewHolder.installBtn.getTag(R.id.version_name))
            && release.isAlpha() == releaseViewHolder.installBtn.getTag(R.id.is_alpha)) {
            releaseViewHolder.installBtn.setText(mContext.getString(R.string.uninstall));
            releaseViewHolder.installBtn.setEnabled(release.getId() != mCurrentInstallId || release.isAlpha() != mCurrentInstallIsAlpha);
            releaseViewHolder.installBtn.setBackgroundResource(release.getId() == mCurrentInstallId && release.isAlpha() == mCurrentInstallIsAlpha ? R.drawable.green_button_disabled : R.drawable.green_button_selector);
        } else {
            releaseViewHolder.installBtn.setText(release.getId() == mCurrentInstallId && release.isAlpha() == mCurrentInstallIsAlpha ? mContext.getString(R.string.installing) : mContext.getString(R.string.install));
            releaseViewHolder.installBtn.setEnabled(release.getId() != mCurrentInstallId || release.isAlpha() != mCurrentInstallIsAlpha);
            releaseViewHolder.installBtn.setBackgroundResource(release.getId() == mCurrentInstallId && release.isAlpha() == mCurrentInstallIsAlpha ? R.drawable.green_button_disabled : R.drawable.green_button_selector);
        }

        releaseViewHolder.installBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (release.getVersionName() != null && release.getVersionName().equals(releaseViewHolder.installBtn.getTag(R.id.version_name))
                && release.isAlpha() == releaseViewHolder.installBtn.getTag(R.id.is_alpha)) {
                    mCurrentUnInstallId = release.getId();
                    mCurrentUnInstallIsAlpha = release.isAlpha();
                    BusProvider.getInstance().post(new UninstallButtonClickedEvent(release.getVersionName()));
                } else {
                    if (mCurrentInstallId != 0) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                        dialog.setTitle("Oops");
                        dialog.setMessage("There is another installation in progress. Please wait for it to finish.");
                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    } else {
                        mCurrentInstallId = release.getId();
                        mCurrentInstallIsAlpha = release.isAlpha();
                        notifyDataSetChanged();
                        BusProvider.getInstance().post(new InstallButtonClickedEvent(release.isAlpha() ? release.getAlphaDownloadLink() : release.getBetaDownloadLink()));
                    }
                }
            }
        });
    }

    @Override
    public ReleaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_release, viewGroup, false);

        return new ReleaseViewHolder(itemView);
    }

    public static class ReleaseViewHolder extends RecyclerView.ViewHolder {
        protected TextView title;
        protected TextView changeLog;
        protected TextView date;
        protected Button installBtn;

        public ReleaseViewHolder(View v) {
            super(v);
            title =  (TextView) v.findViewById(R.id.title);
            changeLog = (TextView) v.findViewById(R.id.changeLog);
            date = (TextView) v.findViewById(R.id.date);
            installBtn = (Button) v.findViewById(R.id.installBtn);
        }
    }
}
