package volunteer.sk.greate43.com.gotcha;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {

    private final LayoutInflater layoutInflater;
    ArrayList<Reminder> mReminders;

    public ArrayList<Reminder> getReminders() {
        return mReminders;
    }

    public ReminderAdapter(Activity activity) {
        layoutInflater = activity.getLayoutInflater();
        mReminders = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.list_reminder, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mReminders == null || mReminders.size() == 0) {
        } else {
            holder.reminderName.setText(mReminders.get(position).getReminderName());
        }

    }


    @Override
    public int getItemCount() {
        if (mReminders != null
                && !mReminders.isEmpty()

                ) {
            return mReminders.size();
        } else {
            return 0;
        }
    }

    public void clear() {
        int size = mReminders.size();
        if (size > 0) {
            mReminders.clear();
            notifyItemRangeRemoved(0, size);
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView reminderName;
        public ViewHolder(View itemView) {
            super(itemView);
            reminderName = itemView.findViewById(R.id.reminder_name);
        }
    }


}
