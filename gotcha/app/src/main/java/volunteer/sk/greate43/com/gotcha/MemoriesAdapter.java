package volunteer.sk.greate43.com.gotcha;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MemoriesAdapter extends RecyclerView.Adapter<MemoriesAdapter.ViewHolder> {

    private final LayoutInflater layoutInflater;
    ArrayList<Memories> mMemories;

    public ArrayList<Memories> getMemories() { return mMemories; }

    public MemoriesAdapter(Activity activity) {
        layoutInflater = activity.getLayoutInflater();
        mMemories = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.list_memories, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mMemories == null || mMemories.size() == 0) {
        } else {
            holder.MemoryName.setText(mMemories.get(position).getMemoryName());
        }
    }


    @Override
    public int getItemCount() {
        if (mMemories != null
                && !mMemories.isEmpty()

                ) {
            return mMemories.size();
        } else {
            return 0;
        }
    }

    public void clear() {
        int size = mMemories.size();
        if (size > 0) {
            mMemories.clear();
            notifyItemRangeRemoved(0, size);
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView MemoryName;
        public ViewHolder(View itemView) {
            super(itemView);
            MemoryName = itemView.findViewById(R.id.Memory_name);
        }
    }
}
