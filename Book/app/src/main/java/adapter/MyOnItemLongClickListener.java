package adapter;

import android.view.View;

import data.Book;

/**
 * Created by taozhiheng on 15-7-5.
 * BookRecyclerAdapter interface
 */
public interface MyOnItemLongClickListener {
    void onItemLongClick(View view, Book book, int position);
}
