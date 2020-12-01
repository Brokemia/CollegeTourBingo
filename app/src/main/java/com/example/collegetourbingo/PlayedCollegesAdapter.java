package com.example.collegetourbingo;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayedCollegesAdapter extends RecyclerView.Adapter<PlayedCollegesAdapter.CollegeViewHolder> implements Filterable {
    public static class CollegeViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout containerView;
        public TextView textView;

        CollegeViewHolder(final PlayedCollegesActivity activity, View view) {
            super(view);

            containerView = view.findViewById(R.id.college_row);
            textView = view.findViewById(R.id.college_row_text_view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, ViewCollegeActivity.class);
                    intent.putExtra("name", textView.getText().toString());
                    activity.startActivity(intent);
                }
            });
        }
    }

    private class CollegeFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<CollegeData> filteredColleges = new ArrayList<>();
            for(CollegeData c : colleges) {
                if(c.name.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                    filteredColleges.add(c);
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredColleges;
            results.count = filteredColleges.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults results) {
            filtered = (List<CollegeData>) results.values;
            notifyDataSetChanged();
        }
    }

    private List<CollegeData> colleges = new ArrayList<>();
    private List<CollegeData> filtered = new ArrayList<>();
    private Context context;
    private PlayedCollegesActivity activity;

    PlayedCollegesAdapter(PlayedCollegesActivity activity, Context context) {
        this.context = context;
        this.activity = activity;
        colleges = MainActivity.database.collegeDataDao().getOnePerCollege();
        Collections.sort(colleges);
        filtered = colleges;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CollegeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.college_row, parent, false);

        return new CollegeViewHolder(activity, view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollegeViewHolder holder, int position) {
        holder.textView.setText(filtered.get(position).name);
        holder.containerView.setTag(filtered.get(position));
    }

    @Override
    public int getItemCount() {
        return filtered.size();
    }

    @Override
    public Filter getFilter() {
        return new CollegeFilter();
    }

    public void reload() {
        colleges = MainActivity.database.collegeDataDao().getOnePerCollege();
        Collections.sort(colleges);
        notifyDataSetChanged();
    }


}
