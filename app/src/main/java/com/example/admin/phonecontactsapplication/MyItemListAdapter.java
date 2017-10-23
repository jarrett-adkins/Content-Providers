package com.example.admin.phonecontactsapplication;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.admin.phonecontactsapplication.model.Contact;

import java.util.ArrayList;
import java.util.List;

public class MyItemListAdapter extends RecyclerView.Adapter<MyItemListAdapter.ViewHolder> {

    private static final String TAG = "MyItemListAdapter";
    Context context;
    List<Contact> list = new ArrayList<>();

    public MyItemListAdapter(List<Contact> list) {
        this.list = list;
    }

    @Override
    public MyItemListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();

        View view = LayoutInflater
                .from( parent.getContext() )
                .inflate( R.layout.list_item, parent, false );

        return new ViewHolder( view );
    }

    @Override
    public void onBindViewHolder(MyItemListAdapter.ViewHolder holder, int position) {
        Contact c = list.get( position );

        holder.name.setText( c.getName() );
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                context, android.R.layout.simple_list_item_1, c.getPhoneNumbers() );
        holder.listView.setAdapter( arrayAdapter );

        setListViewHeightBasedOnChildren( holder.listView );
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ListView listView;

        public ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById( R.id.tvContactName );
            listView = itemView.findViewById( R.id.lvListView );
        }
    }
}
