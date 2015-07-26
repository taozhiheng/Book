package adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hustunique.myapplication.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import data.Book;
import util.Constant;

/**
 * Created by taozhiheng on 15-7-5.
 * before,now,after fragment book data adapter
 */
public class BookRecyclerAdapter extends RecyclerView.Adapter<BookRecyclerAdapter.MyViewHolder> {

    private List<Book> mList;
    private int[] mResources;

    private MyOnItemClickListener mOnItemClickListener;
    private MyOnItemLongClickListener mOnItemLongClickListener;

    private Context mContext;

    public BookRecyclerAdapter(Context context, List<Book> list)
    {
        this.mContext = context;
        this.mList = list;
        mResources = new int[]{R.layout.book_item, R.id.item_icon, R.id.item_icon_text,
                R.id.book_item_time, R.id.book_item_name,
                R.id.book_item_author, R.id.book_item_num};
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mResources[0], parent, false);
        return new MyViewHolder(view, mResources[1], mResources[2], mResources[3],
                mResources[4], mResources[5], mResources[6]);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Book book = mList.get(position);
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(mOnClickListener);
        holder.itemView.setOnLongClickListener(mOnLongClickListener);
        String url = book.getUrl();
        if(url != null && !url.equals("null")) {
            holder.mIconText.setVisibility(View.GONE);
            holder.mIcon.setVisibility(View.VISIBLE);
            File file = new File(url);
            if(file.exists())
                Picasso.with(mContext).load(file).into(holder.mIcon);
            else
                Picasso.with(mContext).load(Uri.parse(url)).into(holder.mIcon);
        }
        else
        {
            holder.mIcon.setVisibility(View.GONE);
            holder.mIconText.setVisibility(View.VISIBLE);
            holder.mIconText.setText(book.getName());
            holder.mIconText.setBackgroundColor(Constant.COLOR);
        }
        String startTimeStr = book.getStartTime();
        if(startTimeStr != null && !startTimeStr.equals("null"))
            startTimeStr = startTimeStr.substring(startTimeStr.indexOf('-')+1, startTimeStr.indexOf('T'));
        else
            startTimeStr = "未知";
        String endTimeStr = book.getEndTime();
        if(endTimeStr != null && !endTimeStr.equals("null"))
            endTimeStr = endTimeStr.substring(endTimeStr.indexOf('-')+1, endTimeStr.indexOf('T'));
        else
            endTimeStr = "未知";
        holder.mTime.setText(startTimeStr+" ~ "+endTimeStr);
        holder.mName.setText(book.getName());
        holder.mAuthor.setText(book.getAuthor());
        holder.mNum.setText(book.getFinishNum()+"/"+book.getChapterNum()+"章    "+book.getWordNum()+" K字");
    }

    static class MyViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView mIcon;
        private TextView mIconText;
        private TextView mTime;
        private TextView mName;
        private TextView mAuthor;
        private TextView mNum;

        public MyViewHolder(View view, int iconRes, int iconTextRes, int timeRes, int nameRes, int authorRes, int numRes)
        {
            super(view);
            this.mIcon = (ImageView) view.findViewById(iconRes);
            this.mIconText = (TextView) view.findViewById(iconTextRes);
            this.mTime = (TextView) view.findViewById(timeRes);
            this.mName = (TextView) view.findViewById(nameRes);
            this.mAuthor = (TextView) view.findViewById(authorRes);
            this.mNum = (TextView) view.findViewById(numRes);
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mOnItemClickListener != null) {
                Log.d("drag", "onClick");
                int position = (int)v.getTag();
                mOnItemClickListener.onItemClick(v, mList.get(position), position);
            }
        }
    };

    private View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if(mOnItemLongClickListener != null) {
                Log.d("drag", "onLongClick");
                int position = (int)v.getTag();
                mOnItemLongClickListener.onItemLongClick(v, mList.get(position), position);
            }
            return true;
        }
    };


    public void setOnItemClickListener(MyOnItemClickListener listener)
    {
        this.mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(MyOnItemLongClickListener listener)
    {
        this.mOnItemLongClickListener = listener;
    }

}
