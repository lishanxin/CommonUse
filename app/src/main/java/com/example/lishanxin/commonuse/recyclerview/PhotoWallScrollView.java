package com.example.lishanxin.commonuse.recyclerview;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.lishanxin.commonuse.R;
import com.example.lishanxin.commonuse.testdata.Images;
import com.example.lishanxin.commonuse.utils.ImageLoader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class PhotoWallScrollView extends ScrollView implements View.OnTouchListener {

    public static final int PAGE_SIZE = 15;

    private int page;

    private int columnWidth;

    private int firstColumnHeight;

    private int secondColumnHeight;

    private int thirdColumnHeight;

    private boolean loadOnce;

    private ImageLoader imageLoader;

    private LinearLayout firstColumn;
    private LinearLayout secondColumn;
    private LinearLayout thirdColumn;

    //记录所有正在下载或等待下载的任务
    private static Set<LoadImageTask> taskCollection;

    private static View scrollLayout;

    private static int scrollViewHeight;

    private static int lastScrollY = -1;

    private List<ImageView> imageViews = new ArrayList<>();

    private static Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            PhotoWallScrollView myScrollView = (PhotoWallScrollView) msg.obj;
            int scrollY = myScrollView.getScrollY();
            // 如果当前的滚动位置和上次相同，表示已停止滚动
            if (scrollY == lastScrollY) {
                // 当滚动到最底部，并且当前没有正在下载的任务时，开始加载下一页的图片
                if (scrollViewHeight + scrollY >= scrollLayout.getHeight()
                        && taskCollection.isEmpty()) {
                    myScrollView.loadMoreImages();
                }
                myScrollView.checkVisibility();
            } else {
                lastScrollY = scrollY;

                Message message = new Message();
                message.obj = myScrollView;
                handler.sendMessageDelayed(message, 10);
            }
        }
    };

    /**
     * 便利imageViews中的每张图片，对图片的可见性进行检查，如果图片已经离开屏幕可见范围，则将图片替换成一张空图。
     */
    private void checkVisibility() {
        for (int i = 0; i < imageViews.size(); i++){
            ImageView imageView = imageViews.get(i);
            int borderTop = (Integer) imageView.getTag(R.string.border_top);
            int borderBottom = (Integer) imageView.getTag(R.string.border_bottom);

            if (borderBottom > getScrollY() && borderTop < getScrollY() + scrollViewHeight){
                String imageUrl = (String) imageView.getTag(R.string.image_url);
                Bitmap bitmap = imageLoader.getBitmapFromMemoryCache(imageUrl);
                if (bitmap != null){
                    imageView.setImageBitmap(bitmap);
                }else {
                    LoadImageTask task = new LoadImageTask(imageView);
                    task.execute(imageUrl);
                }
            }else {
                imageView.setImageResource(android.R.drawable.ic_btn_speak_now);
            }
        }
    }

    public PhotoWallScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);

        imageLoader = ImageLoader.getInstance();
        taskCollection = new HashSet<>();

        setOnTouchListener(this);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (changed && !loadOnce){
            scrollViewHeight = getHeight();
            scrollLayout = getChildAt(0);
            firstColumn = findViewById(R.id.first_column);
            secondColumn = findViewById(R.id.second_column);
            thirdColumn = findViewById(R.id.third_column);

            columnWidth = firstColumn.getWidth();

            loadOnce = true;
            loadMoreImages();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP){
            Message message = new Message();
            message.obj = this;
            handler.sendMessageDelayed(message, 10);
        }

        return false;
    }

    private void loadMoreImages() {
        if (hasSDCard()){
            int startIndex = page * PAGE_SIZE;
            int endIndex = page * PAGE_SIZE + PAGE_SIZE;

            if (startIndex < Images.imageUrls.length){
                toast("正在加载、、、");

                if (endIndex > Images.imageUrls.length){
                    endIndex = Images.imageUrls.length;
                }

                for (int i = startIndex; i < endIndex; i++){
                    LoadImageTask task = new LoadImageTask();
                    taskCollection.add(task);
                    task.execute(Images.imageUrls[i]);
                }
                page ++ ;
            }else {
                toast("没有更多图片");
            }
        }else {
            toast("未发现SD卡");
        }
    }

    /**
     * 判断手机是否有sd卡
     * @return
     */
    private boolean hasSDCard() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public void toast(String msg){
        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
    }

    private class LoadImageTask extends AsyncTask<String, Void, Bitmap> {

        private String mImageUrl;
        private ImageView mImageView;

        public LoadImageTask() {
        }

        public LoadImageTask(ImageView imageView) {
            this.mImageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            mImageUrl = strings[0];

            Bitmap imageBitmap = imageLoader.getBitmapFromMemoryCache(mImageUrl);
            if (imageBitmap == null){
                imageBitmap = loadImage(mImageUrl);
            }
            return imageBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null){
                double ratio = bitmap.getWidth() / (columnWidth * 1.0);
                int scaledHeight = (int) (bitmap.getHeight()/ratio);
                addImage(bitmap, columnWidth, scaledHeight);
            }
            taskCollection.remove(this);
        }

        private void addImage(Bitmap bitmap, int imageWidth, int imageHeight) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageWidth, imageHeight);

            if (mImageView != null){
                mImageView.setImageBitmap(bitmap);
            }else {
                ImageView imageView = new ImageView(getContext());
                imageView.setLayoutParams(params);
                imageView.setImageBitmap(bitmap);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setPadding(5,5,5,5);
                imageView.setTag(R.string.image_url, mImageUrl);
                findColumnToAdd(imageView, imageHeight).addView(imageView);
                imageViews.add(imageView);
            }
        }

        /**
         *找到此时应添加图片的一列。原则就是对三列的高度进行判断，当前高度最小的一列就是应该添加的一列
         */
        private LinearLayout findColumnToAdd(ImageView imageView, int imageHeight) {
            if (firstColumnHeight <= secondColumnHeight){
                if (firstColumnHeight <= thirdColumnHeight){
                    imageView.setTag(R.string.border_top, firstColumnHeight);
                    firstColumnHeight += imageHeight;
                    imageView.setTag(R.string.border_bottom, firstColumnHeight);
                    return firstColumn;
                }
                imageView.setTag(R.string.border_top, thirdColumnHeight);
                thirdColumnHeight += imageHeight;
                imageView.setTag(R.string.border_bottom, thirdColumnHeight);
                return thirdColumn;
            }else {
                if (secondColumnHeight <= thirdColumnHeight){
                    imageView.setTag(R.string.border_top, secondColumnHeight);
                    secondColumnHeight += imageHeight;
                    imageView.setTag(R.string.border_bottom, secondColumnHeight);
                    return secondColumn;
                }
                imageView.setTag(R.string.border_top, thirdColumnHeight);
                thirdColumnHeight += imageHeight;
                imageView.setTag(R.string.border_bottom, thirdColumnHeight);
                return thirdColumn;
            }
        }

        private void downloadImage(String imageUrl) {
            HttpURLConnection con = null;
            FileOutputStream fos = null;
            BufferedOutputStream bos = null;
            BufferedInputStream bis = null;
            File imageFile = null;
            try {
                URL url = new URL(imageUrl);
                con = (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(5 * 1000);
                con.setReadTimeout(15 * 1000);
                con.setDoInput(true);
                con.setDoOutput(true);
                bis = new BufferedInputStream(con.getInputStream());
                imageFile = new File(getImagePath(imageUrl));
                fos = new FileOutputStream(imageFile);
                bos = new BufferedOutputStream(fos);
                byte[] b = new byte[1024];
                int length;
                while ((length = bis.read(b)) != -1) {
                    bos.write(b, 0, length);
                    bos.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bis != null) {
                        bis.close();
                    }
                    if (bos != null) {
                        bos.close();
                    }
                    if (con != null) {
                        con.disconnect();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (imageFile != null) {
                Bitmap bitmap = ImageLoader.decodeSampledBitmapFromResource(
                        imageFile.getPath(), columnWidth);
                if (bitmap != null) {
                    imageLoader.addBitmapToMemoryCache(imageUrl, bitmap);
                }
            }
        }


        private Bitmap loadImage(String url){
            File imageFile = new File(getImagePath(url));
            if (!imageFile.exists()){
                downloadImage(url);
            }
            if (url != null){
                Bitmap bitmap = ImageLoader.decodeSampledBitmapFromResource(imageFile.getPath(), columnWidth);
                if (bitmap != null){
                    imageLoader.addBitmapToMemoryCache(url, bitmap);
                    return bitmap;
                }
            }
            return null;
        }
    }

    private String getImagePath(String url) {
        int lastSlashIndex = url.lastIndexOf("/");
        String imageName = url.substring(lastSlashIndex + 1);
        String imageDir = Environment.getExternalStorageDirectory().getPath() + "/PhotoWallFalls/";
        File file = new File(imageDir);
        if (!file.exists()){
            file.mkdirs();
        }
        String imagePath = imageDir + imageName;
        return imagePath;
    }
}
