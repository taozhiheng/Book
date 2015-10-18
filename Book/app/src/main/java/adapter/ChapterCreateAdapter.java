package adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.hustunique.myapplication.R;

import java.util.List;

import data.ChapterInfo;
import ui.Pointwithcolor;

/**
 * Created by taozhiheng on 15-7-5.
 * CreateActivity book chapters data adapter
 */
public class ChapterCreateAdapter extends RecyclerView.Adapter<ChapterCreateAdapter.MyViewHolder> {


    List<ChapterInfo> mGroupList;

    private MyOnItemChangedListener mOnItemChangedListener;



    private int validColor = Color.rgb(0xe9, 0x1e, 0x63);
    private boolean isFirst;



    public ChapterCreateAdapter(List<ChapterInfo> list)
    {
        this.mGroupList = list;
        this.isFirst = true;
    }


    public void setValidColor(int color)
    {
        this.validColor = color;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if(position == mGroupList.size())
            return 1;
        return super.getItemViewType(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 1) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.create_chapter_item, parent, false);
            return new MyViewHolder(view, R.id.add_pointwithcolor, R.id.add_name, R.id.add_commit);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.addbooklist_item, parent, false);
        return new MyViewHolder(view, R.id.add_pointwithcolor, R.id.add_name);
    }

    @Override
    public void onBindViewHolder(ChapterCreateAdapter.MyViewHolder holder, int position) {
        if(position < mGroupList.size())
        {
            ChapterInfo chapterInfo = mGroupList.get(position);
            holder.mIcon.setColor(validColor);
            holder.mContent.setText(chapterInfo.getName());
            Log.d("load", "position:"+position+" text:"+chapterInfo.getName());
        }
        else
        {
            int waitColor = Color.GRAY;
            holder.mIcon.setColor(waitColor);
            holder.mContent.setText(null);
            holder.mContent.setHint("点击输入章节");
            holder.mCommit.setTag(holder.mContent);
            holder.mCommit.setOnClickListener(mCommitListener);
            if(isFirst)
                isFirst = false;
//            else {
//                holder.mContent.requestFocus();
//                Log.d("create", "request focus");
//            }
        }
        holder.mContent.setTag(position);
        holder.mContent.setOnKeyListener(mOnKeyListener);
        holder.mContent.setFocusable(true);
        holder.mContent.setOnFocusChangeListener(mOnFocusChangedListener);
        holder.mContent.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        return mGroupList.size()+1;
    }



    static class MyViewHolder extends RecyclerView.ViewHolder
    {

        private Pointwithcolor mIcon;
        private EditText mContent;
        private View mCommit;

        public MyViewHolder(View view,int iconRes, int contentRes)
        {
            super(view);
            this.mIcon =  (Pointwithcolor) view.findViewById(iconRes);
            this.mContent = (EditText) view.findViewById(contentRes);
        }

        public MyViewHolder(View view,int iconRes, int contentRes, int commitRes)
        {
            super(view);
            this.mIcon =  (Pointwithcolor) view.findViewById(iconRes);
            this.mContent = (EditText) view.findViewById(contentRes);
            this.mCommit =  view.findViewById(commitRes);
        }
    }

    private View.OnClickListener mCommitListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOnItemChangedListener != null) {
                EditText t = (EditText) v.getTag();
                String str = t.getText().toString();
                int position = (Integer) t.getTag();
                mOnItemChangedListener.onItemInsert(t, position, str);
            }
        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mOnItemChangedListener != null)
                mOnItemChangedListener.onFocus();
        }
    };

    private View.OnKeyListener mOnKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            EditText t = (EditText) v;
            String str = t.getText().toString();
            int position = (Integer) v.getTag();
            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN
                    && position < mGroupList.size() && str.compareTo("") == 0)
            {
                v.clearFocus();
                if (mOnItemChangedListener != null)
                    mOnItemChangedListener.onItemDelete(position);
            }else if (keyCode == KeyEvent.KEYCODE_ENTER
                    && event.getAction() == KeyEvent.ACTION_UP
                    && position == mGroupList.size() )
            {
                    if (mOnItemChangedListener != null) {
                        mOnItemChangedListener.onItemInsert(v, position, str);
                        return true;
                    }
            }
            return false;
        }
    };

    private View.OnFocusChangeListener mOnFocusChangedListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(!hasFocus)
            {
                if(mOnItemChangedListener != null) {
                    int position = (Integer) v.getTag();
                    if(position < mGroupList.size())
                        mOnItemChangedListener.onItemModify(position,
                            ((EditText)v).getText().toString().trim());
                }
            }
            else if(mOnItemChangedListener != null)
                mOnItemChangedListener.onFocus();

        }
    };

    public void setOnItemChangedListener(MyOnItemChangedListener listener)
    {
        this.mOnItemChangedListener = listener;
    }



    public interface MyOnItemChangedListener
    {
        /**
         * when the last item view(TextView) has focus and the key "Enter" is pressed and released,
         * the method will be called
         * */
        void onItemInsert(View v, int position, String str);

        /**
         * when the item(not the last TextView) has focus,
         * the key "Delete" is pressed and the content of the item TextView is empty,
         * the method will be called
         * */
        void onItemDelete(int position);

        /**
         * when the item(not the last TextView) loses focus,
         * the method will be called
         * */
        void onItemModify(int position, String str);

        void onFocus();
    }

}
