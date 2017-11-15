package tj.xona.infinitescroll;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final int spanCount = 5;
    private static final int totalItemCount = 15;

    private GridLayoutManager gridLayoutManager;
    private RecyclerView recyclerView;

    private int orientation = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setAdapter(new MyAdapter(totalItemCount));
        recyclerView.addItemDecoration(new GridDividerDecoration(this));

        gridLayoutManager = new GridLayoutManager(this, spanCount);
        recyclerView.setLayoutManager(gridLayoutManager);

        RecyclerView.OnScrollListener listener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recycler, int dx, int dy) {
                super.onScrolled(recycler, dx, dy);
                Log.i("TAG", "current orientation: " + gridLayoutManager.getOrientation());
                Log.i("TAG", "dx = " + dx + ", dy = " + dy);

                if (dx > 0) {
                    orientation = LinearLayoutManager.HORIZONTAL;

                } else if (dx < 0) {
                    orientation = LinearLayoutManager.VERTICAL;

                } else if (dy > 0) {
                    orientation = LinearLayoutManager.VERTICAL;

                } else if (dy < 0) {
                    orientation = LinearLayoutManager.HORIZONTAL;
                }

                recycler.post(new Runnable() {
                    @Override
                    public void run() {
                        gridLayoutManager.setOrientation(orientation);
                        recyclerView.setLayoutManager(gridLayoutManager);
                    }
                });
            }
        };

        recyclerView.addOnScrollListener(listener);
    }

    static class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        private final int totalSizeOfItems;
        private boolean hasBeenSetup = false;

        MyAdapter(int totalSizeOfItems) {
            this.totalSizeOfItems = totalSizeOfItems;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
            final int cellSize = parent.getMeasuredWidth() / spanCount;
            view.setMinimumWidth(cellSize);
            view.setMinimumHeight(cellSize);
            setupRecyclerHeightIfNeeded(parent, cellSize);
            return new MyViewHolder(view);
        }

        // We need to perform this operation once, not each time `onCreateViewHolder` is called
        private void setupRecyclerHeightIfNeeded(final View parent, int cellSize) {
            if (hasBeenSetup) return;

            hasBeenSetup = true;
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) parent.getLayoutParams();

            int numOfRows = (int) (totalItemCount / (double) spanCount);
            params.height = numOfRows * cellSize + 100;

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    parent.requestLayout();
                }
            });
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int pos) {
            int position = holder.getAdapterPosition() % totalSizeOfItems;
            holder.textView.setText(Integer.toString(position + 1));
        }

        @Override
        public int getItemCount() {
            // this will result the list to be "infinite"
            return Integer.MAX_VALUE;
        }
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        MyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }
    }
}
