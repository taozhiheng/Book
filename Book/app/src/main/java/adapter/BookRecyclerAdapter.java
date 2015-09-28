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
    private boolean mComplete;


    private final static int[] mTags = new int[]{
            R.mipmap.ic_red, R.mipmap.ic_orange, R.mipmap.ic_green,
            R.mipmap.ic_blue, R.mipmap.ic_blue_light, R.mipmap.ic_purple};

    public BookRecyclerAdapter(Context context, List<Book> list)
    {
        this(context, list, false);
    }

    public BookRecyclerAdapter(Context context, List<Book> list, boolean complete)
    {
        this.mContext = context;
        this.mList = list;
        this.mComplete = complete;
        if(complete)
            mResources = new int[]{R.layout.book_item, R.id.item_icon, R.id.item_icon_text,
                R.id.book_item_time, R.id.book_item_name,
                R.id.book_item_author, R.id.book_item_num, R.id.book_item_tag};
        else
            mResources = new int[]{R.layout.book_item2, R.id.item_icon, R.id.item_icon_text,
                    R.id.book_item_time, R.id.book_item_name,
                    R.id.book_item_author, R.id.book_item_num, R.id.book_item_tag};
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mResources[0], parent, false);
        if(mComplete)
            return new MyViewHolder(view, mResources[1], mResources[2], mResources[3],
                mResources[4], mResources[5], mResources[6], mResources[7], true);
        else
            return new MyViewHolder(view, mResources[1], mResources[2], mResources[3],
                    mResources[4], mResources[5], mResources[6], mResources[7], false);
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
        int index = book.getColor();
        if(index < 0 || index > 5)
            index = 0;
        holder.mTag.setImageResource(mTags[index]);
        String url = book.getUrl();
        if(url != null && !url.equals("null")) {
            File file = new File(url);
            if(file.exists()) {
                holder.mIconText.setVisibility(View.GONE);
                Picasso.with(mContext).load(file).into(holder.mIcon);
            }
            else if(url.startsWith("http")) {
                holder.mIconText.setVisibility(View.GONE);
                Picasso.with(mContext).load(Uri.parse(url)).placeholder(R.drawable.book_cover).into(holder.mIcon);
            }
            else
            {
                holder.mIconText.setVisibility(View.VISIBLE);
                Picasso.with(mContext).load(R.drawable.book_cover).into(holder.mIcon);
                String name = book.getName();
                if(name != null && name.length() > 2)
                    name = name.substring(0, 2);
                holder.mIconText.setText(name);
            }
        }
        else
        {
            Picasso.with(mContext).load(R.drawable.book_cover).into(holder.mIcon);
            holder.mIconText.setVisibility(View.VISIBLE);
            String name = book.getName();
            if(name != null && name.length() > 2)
                name = name.substring(0, 2);
            holder.mIconText.setText(name);
        }
        if(mComplete) {
            String startTimeStr = book.getStartTime();
            if (startTimeStr != null && !startTimeStr.equals("null"))
                startTimeStr = startTimeStr.substring(startTimeStr.indexOf('-') + 1, startTimeStr.indexOf('T'));
            else
                startTimeStr = "未知";
            String endTimeStr = book.getEndTime();
            if (endTimeStr != null && !endTimeStr.equals("null"))
                endTimeStr = endTimeStr.substring(endTimeStr.indexOf('-') + 1, endTimeStr.indexOf('T'));
            else
                endTimeStr = "未知";
            if (book.getType() != Constant.TYPE_NOW)
                holder.mTime.setText(null);
            else
                holder.mTime.setText(startTimeStr + " ~ " + endTimeStr);
        }
        holder.mName.setText(book.getName());
        holder.mAuthor.setText(book.getAuthor());
        holder.mNum.setText(book.getFinishNum()+"/"+book.getChapterNum()+"章    "+book.getWordNum()+" 千字");
    }

    static class MyViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView mIcon;
        private TextView mIconText;
        private TextView mTime;
        private TextView mName;
        private TextView mAuthor;
        private TextView mNum;
        private ImageView mTag;

        public MyViewHolder(View view, int iconRes, int iconTextRes, int timeRes,
                            int nameRes, int authorRes, int numRes, int tagRes, boolean complete)
        {
            super(view);
            this.mIcon = (ImageView) view.findViewById(iconRes);
            this.mIconText = (TextView) view.findViewById(iconTextRes);
            if(complete)
                this.mTime = (TextView) view.findViewById(timeRes);
            this.mName = (TextView) view.findViewById(nameRes);
            this.mAuthor = (TextView) view.findViewById(authorRes);
            this.mNum = (TextView) view.findViewById(numRes);
            this.mTag = (ImageView) view.findViewById(tagRes);
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
