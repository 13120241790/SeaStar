package com.rongseal.widget.picture;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sea_monster.exception.BaseException;
import com.sea_monster.network.AbstractHttpRequest;
import com.sea_monster.network.StoreStatusCallback;
import com.sea_monster.resource.ResCallback;
import com.sea_monster.resource.Resource;
import com.sea_monster.resource.ResourceHandler;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import io.rong.imkit.RLog;
import io.rong.imkit.RongContext;
import io.rong.imkit.fragment.BaseFragment;
import io.rong.message.utils.BitmapUtil;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by AMing on 15/10/18.
 * Company RongCloud
 */
public class PreviewFragment extends BaseFragment {
    PhotoView mPhotoView;
    Uri mUri;
    ImageProcess mProcess;

    final static int SHOW_PHOTO = 0x3;
    final static int GET_PHOTO = 0x1;
    final static int REQ_PHOTO = 0x2;


    Uri uri;

    Context context;

    @SuppressLint("ValidFragment")
    public PreviewFragment(Context context, Uri uri) {
        this.uri = uri;
        this.context = context;
    }

    public PreviewFragment(){}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(io.rong.imkit.R.layout.rc_fr_photo2, container, false);
        mPhotoView = (PhotoView) view.findViewById(io.rong.imkit.R.id.rc_icon);
        initPhoto(uri);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mProcess = new ImageProcess();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public boolean handleMessage(Message msg) {

        switch (msg.what) {
            case GET_PHOTO:
                Uri uri = (Uri) msg.obj;

                if (mProcess != null)
                    mProcess.cancel(true);

                mProcess = new ImageProcess();
                mProcess.execute(uri);
                Log.e("TakingPicturesActivity", "GET_PHOTO" + uri);
                break;
            case REQ_PHOTO:
                Log.e("TakingPicturesActivity","REQ_PHOTO"+mUri);
                try {
                    ResourceHandler.getInstance().requestResource(new Resource(mUri), new ResCallback() {
                        @Override
                        public void onComplete(AbstractHttpRequest<File> abstractHttpRequest, File file) {
                            RLog.i(PreviewFragment.this, "onComplete", file.getPath());
                            getHandler().obtainMessage(GET_PHOTO, Uri.fromFile(file)).sendToTarget();
                        }

                        @Override
                        public void onFailure(AbstractHttpRequest<File> abstractHttpRequest, BaseException e) {
                            RLog.e(PreviewFragment.this, "onFailure", e.toString(), e);
                        }

                    }, new StoreStatusCallback() {
                        @Override
                        public void statusCallback(StoreStatus storeStatus) {
                            RLog.d(PreviewFragment.this, "statusCallback", storeStatus.toString());
                        }
                    });
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                break;
            case SHOW_PHOTO:
                Log.e("TakingPicturesActivity","SHOW_PHOTO");
                mPhotoView.setImageBitmap((Bitmap) msg.obj);
                break;
        }

        return true;
    }

    public void initPhoto(final Uri uri) {
        mUri = uri;

        if(mUri == null)
            return;

        if (mUri.getScheme().equals("http")) {

            RongContext.getInstance().executorBackground(new Runnable() {
                @Override
                public void run() {
                    if (ResourceHandler.getInstance().containsInDiskCache(new Resource(mUri))) {
                        mUri = Uri.fromFile(ResourceHandler.getInstance().getFile(new Resource(mUri)));
                        getHandler().obtainMessage(GET_PHOTO, mUri).sendToTarget();
                    } else {
                        getHandler().obtainMessage(REQ_PHOTO, mUri).sendToTarget();
                    }
                }
            });

        } else {
            mProcess = new ImageProcess();
            mProcess.execute(mUri);
        }

    }


    class ImageProcess extends AsyncTask<Uri, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(Uri... params) {

            Bitmap bitmap = null;

            Uri param = params[0];
            Uri uri = null;

            Cursor cursor = null;
            if (param.getScheme() != null && param.getScheme().equals("file")) {
                uri = param;
            } else if (param.getScheme().equals("content")) {

                cursor = context.getContentResolver().query(param, new String[]{android.provider.MediaStore.Images.ImageColumns.DATA}, null, null, null);

                if (cursor == null || cursor.getCount() == 0) {
                    cursor.close();
                    return null;
                }

                cursor.moveToFirst();
                uri = Uri.parse("file://" + cursor.getString(0));
                cursor.close();
            }

            try {
                bitmap = BitmapUtil.getResizedBitmap(getActivity(), uri, 960, 960);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(final Bitmap result) {
            if (result != null)
                mPhotoView.setImageBitmap(result);
        }
    }

    @Override
    public void onRestoreUI(){}
}
