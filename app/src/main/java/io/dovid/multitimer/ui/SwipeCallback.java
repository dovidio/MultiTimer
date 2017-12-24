package io.dovid.multitimer.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.dovid.multitimer.R;

/**
 * Created by Umberto D'Ovidio on 12/24/2017.
 */

abstract class SwipeCallback extends ItemTouchHelper.SimpleCallback {

    private static final String TAG = "SWIPECALLBACK";
    private static final int RED = Color.parseColor("#f44336");
    private static final int GREEN = Color.parseColor("#22cc16");

    private Drawable deleteIcon;
    private Drawable editIcon;
    private int intrinsicHeight;
    private int intrinsicWidth;

    SwipeCallback(Context context) {
        this(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete_white_24dp);
        editIcon = ContextCompat.getDrawable(context, R.drawable.ic_edit_white_24dp);

        intrinsicHeight = deleteIcon.getIntrinsicHeight() * 2;
        intrinsicWidth = deleteIcon.getIntrinsicWidth() * 2;
    }

    private SwipeCallback(int dragDirs, int swipeDirs) {
        super(dragDirs, swipeDirs);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
        int position = viewHolder.getAdapterPosition();

        if (i == ItemTouchHelper.LEFT) {
            swipeLeft(position);
        } else {
            swipeRight(position);
        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        Log.d(TAG, "horizontal displacement: " + dX);

        final View itemView = viewHolder.itemView;

        TextView tv = itemView.findViewById(R.id.textViewTimerName);
        LinearLayout ll = itemView.findViewById(R.id.linearLayoutTimerPause);

        CardView cv = (CardView) ll.getParent();
        if (tv != null && cv == null) {
            ViewParent p = (ViewParent) tv.getParent();
            Log.d(TAG, "found tv parent");
            if (p != null) {
                cv = (CardView) p.getParent();
                Log.d(TAG, "found cardview");
            }
        }

        Log.d(TAG, "cv is null: " + itemView.findViewById(R.id.cardViewTimerPause));

        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) cv.getLayoutParams();
        final int itemHeight = itemView.getBottom() - itemView.getTop();

        ColorDrawable background = new ColorDrawable();

        if (dX < 0) {
            int left = itemView.getRight() + Float.valueOf(dX).intValue() - lp.rightMargin;
            int top = itemView.getTop() + lp.topMargin;
            int bottom = itemView.getBottom() - lp.bottomMargin;
            int right = itemView.getRight() + lp.rightMargin;
            background.setBounds(left, top, right, bottom);
            background.setColor(RED);

            background.draw(c);

            int deleteIconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
            int deleteIconMargin = (itemHeight - intrinsicHeight) / 2;
            int deleteIconLeft = itemView.getRight() - deleteIconMargin - intrinsicWidth;
            int deleteIconRight = itemView.getRight() - deleteIconMargin;
            int deleteIconBottom = deleteIconTop + intrinsicHeight;

            deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
            deleteIcon.draw(c);
        } else {
            int left = itemView.getLeft() + lp.leftMargin;
            int top = itemView.getTop() + lp.topMargin;
            int bottom = itemView.getBottom() - lp.bottomMargin;
            int right = itemView.getLeft() + Float.valueOf(dX).intValue() + lp.rightMargin;
            background.setBounds(left, top, right, bottom);
            background.setColor(GREEN);

            background.draw(c);

            int editIconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
            int editIconMargin = (itemHeight - intrinsicHeight) / 2;
            int editIconLeft = itemView.getLeft() + editIconMargin;
            int editIconRight = itemView.getLeft() + editIconMargin + intrinsicWidth;
            int editiconBottom = editIconTop + intrinsicHeight;

            editIcon.setBounds(editIconLeft, editIconTop, editIconRight, editiconBottom);
            editIcon.draw(c);
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    abstract void swipeLeft(int position);

    abstract void swipeRight(int position);
}
