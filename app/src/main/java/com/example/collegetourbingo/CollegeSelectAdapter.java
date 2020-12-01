package com.example.collegetourbingo;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

public class CollegeSelectAdapter extends RecyclerView.Adapter<CollegeSelectAdapter.CollegeViewHolder> implements Filterable {
    public static class CollegeViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout containerView;
        public TextView textView;

        CollegeViewHolder(final CollegeSelectActivity activity, View view) {
            super(view);

            containerView = view.findViewById(R.id.college_row);
            textView = view.findViewById(R.id.college_row_text_view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                MainActivity.getInstance().setCollege((CharSequence) v.getTag());

                activity.finish();
                }
            });
        }
    }

    private class CollegeFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<String> filteredColleges = new ArrayList<>();
            for(String c : colleges) {
                if(c.toLowerCase().contains(charSequence.toString().toLowerCase())) {
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
            filtered = (List<String>) results.values;
            notifyDataSetChanged();
        }
    }

    private List<String> colleges = new ArrayList<>();
    private List<String> filtered = new ArrayList<>();
    private Context context;
    private CollegeSelectActivity activity;

    CollegeSelectAdapter(CollegeSelectActivity activity, Context context) {
        this.context = context;
        this.activity = activity;
        loadColleges();
    }

    public void loadColleges() {
        InputStream is = null;
        try {
            is = context.getAssets().open("college_list.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            // Throw out header
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                // The first element in the row is an ID, which I don't use
                String[] row = line.split(",");
                colleges.add(row[1]);
                filtered.add(row[1]);
            }
        }
        catch (IOException ex) {
        }
        finally {
            try {
                if(is != null)
                    is.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(filtered);
        Collections.sort(colleges);
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
        holder.textView.setText(filtered.get(position));
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


}
