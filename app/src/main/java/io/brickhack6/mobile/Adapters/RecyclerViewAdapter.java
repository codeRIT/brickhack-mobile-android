package io.brickhack6.mobile.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.brickhack6.mobile.Model.Event;
import io.brickhack6.mobile.R;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.EventViewHolder> {

    private static final String TAG = "ADAPTER";
    private Context context;
    private List<Event> events;

    public RecyclerViewAdapter(Context context, List<Event> events) {
        this.context = context;
        this.events = events;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event, null);
        EventViewHolder holder = new EventViewHolder(view);
        holder.desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "Position " + holder.getAdapterPosition() , Toast.LENGTH_SHORT).show();
            }
        });

        holder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Event status = events.get(holder.getAdapterPosition());
                Log.e(TAG, "onClick: Status --> " + status.isFavorited());
                if (!status.isFavorited()) {
                    holder.favorite.setImageResource(R.drawable.button_pressed);
                    status.setFavorited(true);
                } else {
                    holder.favorite.setImageResource(R.drawable.btn_star);
                    status.setFavorited(false);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event e = events.get(position);
//        Log.e(TAG, "onBindViewHolderrrrrr: " + e.toString());

        String time = events.get(position).getTime();
        String desc = events.get(position).getDesc();
        String day = events.get(position).getDay();

        if (time == null && desc == null) {
            Log.e(TAG, "onBindViewHolder: I am empty");
        } else {
            holder.time.setText(events.get(position).getTime());
            holder.desc.setText(events.get(position).getDesc());
        }

        boolean status = events.get(position).isFavorited();
        if (status) {
            holder.favorite.setImageResource(R.drawable.button_pressed);
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    class EventViewHolder extends RecyclerView.ViewHolder {

        private TextView time;
        private TextView desc;
        private ImageButton favorite;

        EventViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.start_time);
            desc = itemView.findViewById(R.id.event);
            favorite = itemView.findViewById(R.id.favorite);
        }
    }
}
