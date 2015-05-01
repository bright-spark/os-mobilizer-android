package com.redmart.mobilizer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.redmart.mobilizer.R;
import com.redmart.mobilizer.models.Branch;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joshua on 27/3/15.
 */
public class SpinnerAdapter extends BaseAdapter {

    private List<Branch> mItems = new ArrayList<>();
    private String mRepo;
    private Context mContext;

    public SpinnerAdapter(Context context) {
        this.mContext = context;
    }

    public void clear() {
        mItems.clear();
    }

    public List<Branch> getItems() {
        return mItems;
    }

    public void addItems(List<Branch> branchList) {
        mItems.addAll(branchList);
    }

    public void setRepo(String repo) {
        this.mRepo = repo;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals("DROPDOWN")) {
            view = LayoutInflater.from(mContext).inflate(R.layout.toolbar_spinner_item_dropdown, parent, false);
            view.setTag("DROPDOWN");
        }

        TextView textView = (TextView) view.findViewById(R.id.branchName);
        textView.setText(getTitle(position));

        return view;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals("NON_DROPDOWN")) {
            view = LayoutInflater.from(mContext).inflate(R.layout.toolbar_spinner_item_ab, parent, false);
            view.setTag("NON_DROPDOWN");
        }

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(mRepo);
        TextView subTitle = (TextView) view.findViewById(R.id.subTitle);
        subTitle.setText(getTitle(position));

        return view;
    }

    private String getTitle(int position) {
        return position >= 0 && position < mItems.size() ? mItems.get(position).getName() : "";
    }
}
