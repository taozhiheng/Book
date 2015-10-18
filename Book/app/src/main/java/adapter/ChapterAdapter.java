package adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.hustunique.myapplication.R;

import java.util.List;

import data.Chapter;
import util.Constant;

/**
 * Created by taozhiheng on 15-7-5.
 * DetailActivity book chapters data adapter
 */
public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.MyViewHolder> {

    private List<Chapter> mList;

    private ChapterOnItemClickListener mOnItemClickListener;

    private boolean mVisible;

    private int mColorIndex = 0;

    private final static int[] bkgRes = new int[]{
            R.drawable.chapter_background0, R.drawable.chapter_background1,
            R.drawable.chapter_background2, R.drawable.chapter_background3,
            R.drawable.chapter_background4, R.drawable.chapter_background5};


    private final static int[] iconRes = new int[]{
            R.drawable.chapter_icon_background0, R.drawable.chapter_icon_background1,
            R.drawable.chapter_icon_background2, R.drawable.chapter_icon_background3,
            R.drawable.chapter_icon_background4, R.drawable.chapter_icon_background5};

    private final static int iconResDark = R.drawable.chapter_icon_background;

    private final static int[] itemRes = new int[]{
            R.drawable.recycler_item_background0, R.drawable.recycler_item_background1,
            R.drawable.recycler_item_background2, R.drawable.recycler_item_background3,
            R.drawable.recycler_item_background4, R.drawable.recycler_item_background5};

    public ChapterAdapter(List<Chapter> list)
    {
        this(list, true, 0);
    }

    public ChapterAdapter(List<Chapter> list, boolean visible, int index)
    {
        this.mList = list;
        this.mVisible = visible;
        this.mColorIndex = index;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chapter_item, parent, false);
        return new MyViewHolder(view,
                R.id.chapter_icon, R.id.chapter_content, R.id.chapter_flag );
    }

    @Override
    public void onBindViewHolder(ChapterAdapter.MyViewHolder holder, int position) {
        Chapter chapter = mList.get(position);
        holder.itemView.setBackgroundResource(itemRes[mColorIndex]);
        holder.mContent.setText(chapter.getName());
        holder.mIcon.setBackgroundResource(bkgRes[mColorIndex]);
        if(mVisible) {
            holder.mFlag.setVisibility(View.VISIBLE);
            int type = chapter.getType();
            if(type == Constant.TYPE_AFTER || type == Constant.TYPE_NOW)
            {
                holder.mFlag.setBackgroundResource(iconRes[mColorIndex]);
            }
            else
            {
                holder.mFlag.setBackgroundResource(iconResDark);
            }
            if(type == Constant.TYPE_BEFORE)
                holder.mContent.setTextColor(Constant.chapterFinish);
            else
                holder.mContent.setTextColor(Constant.chapterNormal);


            holder.mFlag.setTag(new Info(holder.itemView, holder.mIcon, holder.mContent, position));
            holder.mFlag.setOnClickListener(mOnClickListener);
//            holder.itemView.setTag(new Info(holder.itemView, holder.mIcon, holder.mFlag, position));
//            holder.itemView.setOnClickListener(mOnClickListener);

            //false <-after - now-> true
            //false <-before - repeat-> true
            if(type == Constant.TYPE_NOW || type == Constant.TYPE_REPEAT) {
                holder.mIcon.setSelected(true);
                holder.mFlag.setSelected(true);
                holder.itemView.setSelected(true);
            }
            else
            {
                holder.mIcon.setSelected(false);
                holder.mFlag.setSelected(false);
                holder.itemView.setSelected(false);
            }

        }
        else
        {
            holder.mFlag.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    //设置变色
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mOnItemClickListener != null)
            {
                Info info = (Info) v.getTag();
                if(info == null)
                    return;
                int position = info.position;
                int type = mList.get(position).getType();
                //now to after
                //repeat to before
                if(type == Constant.TYPE_NOW || type == Constant.TYPE_REPEAT)
                {
                    if(type == Constant.TYPE_NOW)
                    {
                        type = Constant.TYPE_AFTER;
                    }
                    else
                    {
                        type = Constant.TYPE_BEFORE;
                        if(info.contentView instanceof TextView)
                            ((TextView)info.contentView).setTextColor(Constant.chapterFinish);
                    }
                    v.setSelected(false);
                    info.itemView.setSelected(false);
                    info.iconView.setSelected(false);
                }
                //before to now
                //after to repeat
                else
                {
                    if(type == Constant.TYPE_BEFORE && info.contentView instanceof TextView)
                        ((TextView)info.contentView).setTextColor(Constant.chapterNormal);
                    if(type == Constant.TYPE_AFTER)
                        type = Constant.TYPE_NOW;
                    else
                        type = Constant.TYPE_REPEAT;
                    v.setSelected(true);
                    info.itemView.setSelected(true);
                    info.iconView.setSelected(true );
                }
                mOnItemClickListener.onItemClick(position, type);
            }
        }
    };


    class Info
    {
        private View itemView;
        private View iconView;
        private View contentView;
        private int position;

        public Info(View itemView, View iconView, View contentView, int position)
        {
            this.itemView = itemView;
            this.iconView = iconView;
            this.contentView = contentView;
            this.position = position;
        }
    }

    static class MyViewHolder extends RecyclerView.ViewHolder
    {

        private View mIcon;
        private TextView mContent;
        private ImageView mFlag;
        public MyViewHolder(View view,int iconRes, int contentRes, int flagRes)
        {
            super(view);
            this.mIcon =  view.findViewById(iconRes);
            this.mContent = (TextView) view.findViewById(contentRes);
            this.mFlag = (ImageView) view.findViewById(flagRes);
        }
    }

    public void setOnItemClickListener(ChapterOnItemClickListener listener)
    {
        this.mOnItemClickListener = listener;
    }


    public interface ChapterOnItemClickListener
    {
        void onItemClick(int position, int type);
    }

}
