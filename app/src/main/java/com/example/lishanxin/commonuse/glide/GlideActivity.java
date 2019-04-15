package com.example.lishanxin.commonuse.glide;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Option;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestOptions;
import com.example.lishanxin.commonuse.R;
import com.example.lishanxin.commonuse.glide.util.GlideApp;
import com.example.lishanxin.commonuse.widget.recycler.RecyclerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class GlideActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glide);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        Adapter mAdapter = new Adapter();
        mAdapter.add(getData());
        mRecyclerView.setAdapter(mAdapter);
    }

    private List<String> getData(){
        List<String> list = new ArrayList<>();
        list.add("https://goss3.vcg.com/editorial/vcg/800/new/VCG111203782125.jpg");
        list.add("https://goss3.vcg.com/editorial/vcg/800/new/VCG111203438468.jpg");
        list.add("https://goss3.vcg.com/editorial/vcg/800/new/VCG111203438479.jpg");
        return list;
    }


    public class Adapter extends RecyclerAdapter<String>{

        @Override
        protected int getItemViewType(int position, String s) {
            return R.layout.cell_image;
        }

        @Override
        protected ViewHolder<String> onCreateViewHolder(View root, int viewType) {
            return new MyViewHolder(root);
        }
    }


    public class MyViewHolder extends RecyclerAdapter.ViewHolder<String>{

        ImageView mView;

        public MyViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(final String s) {
//            1:
//            final RequestOptions sharedOptions = new RequestOptions().placeholder(R.drawable.test_background).fitCenter();
//            Glide.with(itemView).load(s).apply(sharedOptions).into(mView);
//            2:
            GlideApp.with(itemView).asBitmap().load(s).transition(new BitmapTransitionOptions()).placeholder(R.drawable.test_background).into(mView);
//            3:
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    FutureTarget<Bitmap> futureTarget = Glide.with(itemView).asBitmap().load(s).apply(sharedOptions).submit();
//                    try {
//                        final Bitmap bitmap = futureTarget.get();
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                mView.setImageBitmap(bitmap);
//                            }
//                        });
//                    } catch (ExecutionException e) {
//                        e.printStackTrace();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    Glide.with(itemView).clear(futureTarget);
//                }
//            }).start();

        }

        @Override
        protected void initWidget() {
            mView = itemView.findViewById(R.id.glide_image);
        }
    }

}
