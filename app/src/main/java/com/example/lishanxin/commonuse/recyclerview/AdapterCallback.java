package com.example.lishanxin.commonuse.recyclerview;

/**
 * @author: Li Shanxin
 * @version 1.0.0
 */
public interface AdapterCallback<Data> {
    void update(Data data, RecyclerAdapter.ViewHolder<Data> holder);
}
