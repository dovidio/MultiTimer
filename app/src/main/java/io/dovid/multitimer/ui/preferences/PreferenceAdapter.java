package io.dovid.multitimer.ui.preferences;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import io.dovid.multitimer.R;

/**
 * Author: Umberto D'Ovidio
 * Date: 23/09/17
 * Email: umberto.dovidio@gmail.com
 * Website: http://dovid.io
 * Tutorial link : http://dovid.io
 */

public class PreferenceAdapter extends RecyclerView.Adapter<PreferenceAdapter.PreferenceViewHolder> {

    private static final int VIBRATE = 2;
    private preferenceItemOnClickListener listener;
    private int[] imageResources;
    private String[] names;

    public PreferenceAdapter(Context context, int[] imageResources, String[] names) {
        try {
            listener = (preferenceItemOnClickListener) context;
        } catch (ClassCastException e) {
            throw new RuntimeException("class " + context.toString() + " does not implement preferenceItemOnClickListener interface");
        }

        this.imageResources = imageResources;
        this.names = names;
    }

    @Override
    public PreferenceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.preference_item, parent, false);
        return new PreferenceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return imageResources.length;
    }

    class PreferenceViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private ImageView image;

        public PreferenceViewHolder(final View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.preference_item_tv);
            image = itemView.findViewById(R.id.preference_item_iv);
        }

        public void bind(final int position) {
            name.setText(names[position]);
            image.setImageResource(imageResources[position]);
            final Switch switchButton = itemView.findViewById(R.id.preference_item_switch);
            final boolean isChecked = PreferenceManager
                    .getDefaultSharedPreferences(itemView.getContext())
                    .getBoolean(itemView.getContext().getString(R.string.preference_vibrate), false);

            if (position == VIBRATE) {
                switchButton.setVisibility(View.VISIBLE);
                switchButton.setChecked(isChecked);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.preferenceItemClicked(position);
                    switchButton.performClick();
                }
            });
        }
    }

    public interface preferenceItemOnClickListener {
        public void preferenceItemClicked(int position);
    }
}
