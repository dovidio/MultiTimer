package io.dovid.multitimer.ui.preferences;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import io.dovid.multitimer.R;

/**
 * Author: Umberto D'Ovidio
 * Date: 23/09/17
 * Email: umberto.dovidio@gmail.com
 * Website: http://dovid.io
 * Tutorial link : http://dovid.io
 */

public class ColorSchemeAdapter extends RecyclerView.Adapter<ColorSchemeAdapter.ColorSchemeViewHolder> {

    private static final String TAG = "colorschemeadapter";
    private int[] imageResources;
    private String[] titles;
    private String[] descriptions;

    public ColorSchemeAdapter(int[] imageResources, String[] titles, String[] descriptions) {
        this.imageResources = imageResources;
        this.titles = titles;
        this.descriptions = descriptions;
    }

    @Override
    public ColorSchemeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.color_scheme_item, null);
        return new ColorSchemeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ColorSchemeViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    class ColorSchemeViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView title;
        private TextView description;


        public ColorSchemeViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.color_scheme_item_iv);
            title = itemView.findViewById(R.id.color_scheme_item_title_tv);
            description = itemView.findViewById(R.id.color_scheme_item_description_tv);

        }

        public void bind(final int position) {
            imageView.setImageResource(imageResources[position]);
            title.setText(titles[position]);
            description.setText(descriptions[position]);

            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(itemView.getContext());
            String colorTheme = sharedPreferences.getString(itemView.getContext().getString(R.string.preference_color_scheme), "0");

            Log.d(TAG, "bind: colorTheme: " + colorTheme);

            if (colorTheme.equals(String.valueOf(position))) {
                itemView.setBackgroundColor(Color.LTGRAY);
            } else {
                itemView.setBackgroundColor(Color.WHITE);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean success = sharedPreferences.edit().putString(
                            itemView.getContext().getString(R.string.preference_color_scheme),
                            String.valueOf(position)).commit();
                    notifyDataSetChanged();
                }
            });
        }
    }
}
