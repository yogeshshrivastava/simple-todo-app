package com.codepath.simpletodo.homescreen;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.codepath.simpletodo.R;
import com.codepath.simpletodo.entity.Item;

import java.util.List;

/**
 *  Items adapter defining how items should be shown in the recycler view.
 *
 *  @author Yogesh Shrivastava
 */
public class ToDoItemsAdapter extends RecyclerView.Adapter<ToDoItemsAdapter.ViewHolder> {

    private List<Item> mItemsList;

    private OnItemClickListener mListener;
    private onCheckListener mCheckListener;

    /**
     * Array that holds the priority strings.
     */
    String [] priorityArray;

    public void setUpdatedList(List<Item> itemsList) {
        this.mItemsList = itemsList;
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public interface onCheckListener {
        void onItemCheckClicked(boolean check, int position);
    }

    public ToDoItemsAdapter(List<Item> itemsList, String [] priorityArray) {
        this.mItemsList = itemsList;
        this.priorityArray = priorityArray;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public void setOnCheckListener(onCheckListener listener) {
        mCheckListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.todo_list_item, parent, false);
        ViewHolder view = new ViewHolder(itemView);
        return view;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Item item = mItemsList.get(position);
        holder.itemName.setText(item.getName());
        if(item.getPriority() != 0) {
            holder.priority.setText(priorityArray[item.getPriority()]);
            holder.priority.setVisibility(View.VISIBLE);
        } else {
            holder.priority.setVisibility(View.GONE);
        }
        holder.setDone(item.isDone());
    }

    @Override
    public int getItemCount() {
        return mItemsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
        TextView itemName;
        TextView priority;
        CheckBox done;

        public ViewHolder(View itemView) {
            super(itemView);
            itemName = (TextView) itemView.findViewById(R.id.name);
            priority = (TextView) itemView.findViewById(R.id.priority);
            done = (CheckBox) itemView.findViewById(R.id.doneCheckbox);
            itemView.setOnClickListener(this);
            done.setOnCheckedChangeListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClick(v, getLayoutPosition());
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            updateView(isChecked);
            if(mCheckListener != null) {
                mCheckListener.onItemCheckClicked(isChecked, getLayoutPosition());
            }
        }

        private void updateView(boolean isChecked) {
            if(isChecked) {
                itemName.setPaintFlags(itemName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                itemName.setPaintFlags(itemName.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
            }
        }

        /**
         * Helper function to setup the checkbox.
         *
         * @param isChecked
         */
        public void setDone(boolean isChecked) {
            updateView(isChecked);
            //TODO: Current work around to update without triggering the onCheckedChanged Listener.
            done.setOnCheckedChangeListener(null);
            done.setChecked(isChecked);
            done.setOnCheckedChangeListener(this);
        }
    }
}
