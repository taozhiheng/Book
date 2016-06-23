package adapter;

import android.view.View;

import data.Chapter;

/**
 * Created by taozhiheng on 15-7-5.
 * ReadingAdapter interface
 */
public interface MyOnItemFunctionListener {
    void onItemFunction(View view, Chapter chapter, int position, int function);
}
