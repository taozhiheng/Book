package adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hustunique.myapplication.R;

import java.io.File;
import java.util.List;

import data.Chapter;
import util.Constant;

import com.daimajia.swipe.SwipeLayout;
import com.squareup.picasso.Picasso;

/**
 * Created by taozhiheng on 15-7-5.
 * ReadingFragment chapters data adapter
 */
public class ReadingAdapter extends RecyclerView.Adapter<ReadingAdapter.MyViewHolder> {

    private List<Chapter> mList;

    private MyOnItemFunctionListener mOnItemFunctionListener;


    private Context mContext;

    public ReadingAdapter(Context context, List<Chapter> list)
    {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == mList.size())
            return 1;
        return super.getItemViewType(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 1)
        {
            TextView textView = new TextView(parent.getContext());
            textView.setText("已经没有要读的章节了");
            textView.setTextSize(13);
            textView.setPadding(0, 20, 0, 20);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(parent.getContext().getResources().getColor(R.color.search_hint));
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new MyViewHolder(textView);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reading_item, parent, false);
        return new MyViewHolder(view,
                R.id.reading_item_finish, R.id.reading_item_icon, R.id.reading_item_icon_text,
                R.id.reading_item_content, R.id.reading_item_book_name,
                R.id.reading_item_delete, R.id.reading_item_top);
    }

    @Override
    public void onBindViewHolder(ReadingAdapter.MyViewHolder holder, int position) {
        if(position == mList.size()) {

            Log.d("reading", "pos:"+position);
            return;
        }
        SwipeLayout sample1 = (SwipeLayout) holder.itemView;
        sample1.addDrag(SwipeLayout.DragEdge.Left, sample1.findViewById(R.id.bottom_wrapper));
        sample1.addDrag(SwipeLayout.DragEdge.Right, sample1.findViewById(R.id.bottom_wrapper_2));

        holder.mFinish.setTag(new Info(holder.itemView, position, 0));
        holder.mTop.setTag(new Info(holder.itemView, position, 1));
        holder.mDelete.setTag(new Info(holder.itemView, position, 2));
        holder.mFinish.setOnClickListener(mOnClickListener);
        holder.mTop.setOnClickListener(mOnClickListener);
        holder.mDelete.setOnClickListener(mOnClickListener);
        Chapter chapter = mList.get(position);
        String url = chapter.getUrl();
        if(url != null && !url.equals("null")) {
            File file = new File(url);
            if(file.exists()) {
                holder.mIconText.setVisibility(View.GONE);
                Log.d("reading", "load file:"+url);
                Picasso.with(mContext).load(file).into(holder.mIcon);
            }
            else if(url.startsWith("http")) {
                holder.mIconText.setVisibility(View.GONE);
                Log.d("reading", "load from internet:"+url);
                Picasso.with(mContext).load(Uri.parse(url)).placeholder(R.drawable.book_cover).into(holder.mIcon);
            }
            else
            {
                Log.d("reading", "load default");
                Picasso.with(mContext).load(R.drawable.book_cover).into(holder.mIcon);
                holder.mIconText.setVisibility(View.VISIBLE);
                String name = chapter.getBookName();
                if(name != null && name.length() > 2)
                    name = name.substring(0, 2);
                holder.mIconText.setText(name);
            }
        }
        else
        {
            Log.d("reading", "load default");
            Picasso.with(mContext).load(R.drawable.book_cover).into(holder.mIcon);
            holder.mIconText.setVisibility(View.VISIBLE);
            String name = chapter.getBookName();
            if(name != null && name.length() > 2)
                name = name.substring(0, 2);
            holder.mIconText.setText(name);
        }
        holder.mChapter.setText(chapter.getName());
        holder.mBook.setText(chapter.getBookName());
    }

    @Override
    public int getItemCount() {
        if(mList.size() == 0)
            return 1;
        return mList.size();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mOnItemFunctionListener != null)
            {
                Info info = (Info) v.getTag();
                int position = info.parentId;
                mOnItemFunctionListener.onItemFunction(info.parent, mList.get(position), position, info.id);
            }
        }
    };

    class Info
    {
        View parent;
        int parentId;
        int id;

        public Info(View parent, int parentId, int id)
        {
            this.parent = parent;
            this.parentId = parentId;
            this.id = id;
        }
    }

    static class MyViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView mFinish;
        private ImageView mIcon;
        private TextView mIconText;
        private TextView mChapter;
        private TextView mBook;
        private ImageView mDelete;
        private ImageView mTop;

        public MyViewHolder(View view)
        {
            super(view);
        }

        public MyViewHolder(View view, int finishRes, int iconRes, int iconTextRes, int contentRes, int bookRes, int delRes, int topRes)
        {
            super(view);
            this.mFinish = (ImageView) view.findViewById(finishRes);
            this.mIcon = (ImageView) view.findViewById(iconRes);
            this.mIconText = (TextView) view.findViewById(iconTextRes);
            this.mChapter = (TextView) view.findViewById(contentRes);
            this.mBook = (TextView) view.findViewById(bookRes);
            this.mDelete = (ImageView) view.findViewById(delRes);
            this.mTop = (ImageView) view.findViewById(topRes);
        }
    }


    public void setOnItemFunctionListener(MyOnItemFunctionListener listener)
    {
        this.mOnItemFunctionListener = listener;
    }
}
