package adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hustunique.myapplication.R;
import java.util.List;
import data.ChapterInfo;

/**
 * Created by taozhiheng on 15-7-5.
 * AddActivity book chapters data preview adapter
 */
public class PreviewAdapter extends RecyclerView.Adapter<PreviewAdapter.MyViewHolder> {


    List<ChapterInfo> mGroupList;

    public PreviewAdapter(List<ChapterInfo> list)
    {
        this.mGroupList = list;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.preview_item, parent, false);
        return new MyViewHolder(view, R.id.preview_name);
    }

    @Override
    public void onBindViewHolder(PreviewAdapter.MyViewHolder holder, int position) {
        ChapterInfo chapterInfo = mGroupList.get(position);
        holder.mContent.setText(chapterInfo.getName());
        Log.d("load", "position:"+position+" text:"+chapterInfo.getName());
    }

    @Override
    public int getItemCount() {
        return mGroupList.size();
    }



    static class MyViewHolder extends RecyclerView.ViewHolder
    {

        private TextView mContent;
        public MyViewHolder(View view, int contentRes)
        {
            super(view);
            this.mContent = (TextView) view.findViewById(contentRes);
        }
    }

}
