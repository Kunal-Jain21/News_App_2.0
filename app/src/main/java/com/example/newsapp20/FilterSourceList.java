package com.example.newsapp20;

import android.widget.Filter;

import java.util.ArrayList;

public class FilterSourceList extends Filter {

    private AdapterSourceList adapter;
    private ArrayList<ModelSourceList> filterList;

    public FilterSourceList(AdapterSourceList adapter, ArrayList<ModelSourceList> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        // check constraint validity
        if (constraint != null && constraint.length() > 0) {
            // change to upper case to make it not case sensitive
            constraint = constraint.toString().toUpperCase();
            //store our filtered records
            ArrayList<ModelSourceList> filteredModels = new ArrayList<>();
            for (int i=0; i<filterList.size(); i++) {

                if (filterList.get(i).getName().toUpperCase().contains(constraint)) {
                    filteredModels.add(filterList.get(i));
                }
            }

            results.count = filteredModels.size();
            results.values = filteredModels;
        }
        else {
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        adapter.sourceLists = (ArrayList<ModelSourceList>) filterResults.values;
        adapter.notifyDataSetChanged();
    }
}
