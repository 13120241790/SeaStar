package com.rupeng.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.rupeng.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import io.rong.imkit.tools.PhotoFragment;

/**
 * rongcloud 放大查看大图依赖 class
 */
public class PhotoActivity extends BaseActivity implements View.OnClickListener {

    private static final String SAVE_PIC_PATH=Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED) ? Environment.getExternalStorageDirectory().getAbsolutePath() : "/mnt/sdcard";//保存到SD卡
    private static final String SAVE_REAL_PATH = SAVE_PIC_PATH + "/rong/savePic";//保存的确切位置


    PhotoFragment mPhotoFragment;
    Uri mUri;
    Uri mDownloaded;

    private Button mLeftBtn , mRigthBth;

    // TODO  保存功能未使用
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.de_ac_photo);
        initView();
        initData();
        mLeftBtn = getBtn_left();
        mLeftBtn.setText("查看大图");
        mRigthBth = getBtn_right();
        mRigthBth.setText("保存");
        mRigthBth.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.icon) {
//            if(mDownloaded == null) {
//                Toast.makeText(this, "正在下载，请稍后保存！", Toast.LENGTH_SHORT).show();
//                return true;
//            }
//
//            File path = Environment.getExternalStorageDirectory();
//            File dir = new File(path, "RongCloud/Image");
//            if(!dir.exists())
//                dir.mkdirs();
//
//            File from = new File(mDownloaded.getPath());
//            String name = from.getName() + ".jpg";
//            File to = new File(dir.getAbsolutePath(), name);
//            if(to.exists()) {
//                Toast.makeText(this, "文件保存成功！", Toast.LENGTH_SHORT).show();
//                return true;
//            }
//            copyFile(from.getAbsolutePath(), to.getAbsolutePath());
        }
        return super.onOptionsItemSelected(item);
    }

    public void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) {
                InputStream inStream = new FileInputStream(oldPath);
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread;
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }

//            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//            Uri uri = Uri.fromFile(oldfile);
//            intent.setData(uri);
//            context.sendBroadcast(intent);
        }
        catch (Exception e) {
            Toast.makeText(this, "文件保存出错！", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            Toast.makeText(this, "文件保存成功！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(mUri != null && mUri.getScheme() != null && mUri.getScheme().startsWith("http")) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.de_fix_username, menu);
            return super.onCreateOptionsMenu(menu);
        } else {
            return super.onCreateOptionsMenu(menu);
        }
    }

    protected void initView() {
        mPhotoFragment = (PhotoFragment)getSupportFragmentManager().findFragmentById(R.id.photo_fragment);
    }

    protected void initData() {
        Uri uri = getIntent().getParcelableExtra("photo");
        Uri thumbUri = getIntent().getParcelableExtra("thumbnail");

        mUri = uri;
        if (uri != null)
            mPhotoFragment.initPhoto(uri, thumbUri, new PhotoFragment.PhotoDownloadListener() {
                @Override
                public void onDownloaded(Uri uri) {
                    mDownloaded = uri;
                }

                @Override
                public void onDownloadError() {

                }
            });
    }

    @Override
    public void onClick(View v) {

//        if (mUri != null && mUri.getScheme() != null && mUri.getScheme().startsWith("http")) {
            if(mDownloaded == null) {
                Toast.makeText(this, "正在下载，请稍后保存！", Toast.LENGTH_SHORT).show();
                return;
            }

            File path = Environment.getExternalStorageDirectory();
            File dir = new File(path, "RongCloud/Image");
            if(!dir.exists())
                dir.mkdirs();

            File from = new File(mDownloaded.getPath());
            String name = from.getName();
            File to = new File(SAVE_REAL_PATH, name);
            if(to.exists()) {
                Toast.makeText(this, "文件保存成功！", Toast.LENGTH_SHORT).show();
            }
            copyFile(from.getAbsolutePath(), to.getAbsolutePath());
        }
//    }
}
