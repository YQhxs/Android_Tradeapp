package com.example.android.ui.publish;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.R;
import com.example.android.gson.Images;
import com.example.android.gson.PublishGood;
import com.example.android.util.BaseActivity;
import com.example.android.util.FileUtils;
import com.example.android.util.GetContext;
import com.example.android.util.HttpUtil;
import com.example.android.util.ImageCompress;
import com.example.android.util.LogUtil;
import com.example.android.util.PhotoUtil;
import com.example.android.util.UriUtils;
import com.example.android.widget.NineGridAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PublishGoods extends BaseActivity {
    private static final String TAG = "PublishGoods";
    private RecyclerView mRcyvImage;
    private NineGridAdapter mNineGridAdapter;
    private List<String> mSDImageList = new ArrayList<>();

    private Spinner spinner;
    private EditText editText, priceEdit;
    private ImageView imageView;
    private Button button;
    private String photosname, userid, category = "日用品";
    private PublishGood publishGood = new PublishGood();
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_goods);
//        Home.java 传来的userid
        userid = getIntent().getStringExtra("user_id");

        editText = findViewById(R.id.publish_goods_title);
        priceEdit = findViewById(R.id.publish_goods_price);
        imageView = findViewById(R.id.back);
        button = findViewById(R.id.save);
//        LogUtil.e(TAG, imageView + "--" + button + "----" + infoTitle);
//        返回
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        保存
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!"".equals(editText.getText().toString()) && mSDImageList.size() > 0) {
                    uploadalldata();
                    Toast.makeText(GetContext.getContext(), "发布成功", Toast.LENGTH_SHORT).show();
                    finish();
                }
                Toast.makeText(GetContext.getContext(), "内容为空，不发布", Toast.LENGTH_SHORT).show();

            }
        });
        mRcyvImage = findViewById(R.id.nine_grid);
        mRcyvImage.setLayoutManager(new GridLayoutManager(PublishGoods.this, 3));
        mNineGridAdapter = new NineGridAdapter(mSDImageList);
        mNineGridAdapter.setOnItemClickListener(new NineGridAdapter.OnItemClickListener() {
            @Override
            public void onTakePhotoClick() {
                showImageDialog();
            }

            @Override
            public void onItemLongClick(View view, int position) {
                showDeletePop(view, position);
            }
        });
        mRcyvImage.setAdapter(mNineGridAdapter);
        spinner = findViewById(R.id.category_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] mcategory = getResources().getStringArray(R.array.allCategory);
                category = mcategory[position];
                //此处是下拉框逻辑
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showImageDialog() {
//        选择照片或拍照逻辑
        PhotoUtil.openAlbum(this, 1);
    }

    private void showDeletePop(View view, int position) {
//        后期可把长按删除用户体验增强
        mSDImageList.remove(position);
        mNineGridAdapter.notifyDataSetChanged();
//    长按删除逻辑
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri image_uri;
//        1代表从相册选取，后期可根据需要相机选取
        switch (requestCode) {
            case 1:
//                Bitmap bitmap;
//                Uri uri;
//                bitmap = BitmapFactory.decodeResource(GetContext.getContext().getResources(), R.drawable.carpool);
//                uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null));
//                LogUtil.e(TAG,""+uri);
//                LogUtil.e(TAG, String.valueOf(uri));
                if (resultCode == RESULT_OK && data.getData() != null) {
                    image_uri = data.getData();
                    LogUtil.e(TAG, "在九宫格PublishGood是" + image_uri);
                    String realurl = FileUtils.getFilePathByUri(GetContext.getContext(), UriUtils.getFileUri(GetContext.getContext(), image_uri));
                    LogUtil.e("----PublishGood从uri获取真实路径", "" + realurl);
                    mSDImageList.add(realurl);
                    mNineGridAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    private void uploadalldata() {
        updatePhotos(mSDImageList);
//        其实可以不用msdImagelist参数，下面方法可以直接使用这个变量；
        LogUtil.e(TAG, "fdsaf" + publishGood.getPhotos());
//        上传所有信息
        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                publishGood.setPhotos((String) msg.obj);
                publishGood.setCategory(category);
                publishGood.setIntroduction(editText.getText().toString());
                publishGood.setTitle(editText.getText().toString());
                publishGood.setUseid(userid);
                publishGood.setPrice(priceEdit.getText().toString());
                LogUtil.e(TAG, "在publishgood里面，在异步外整体数据为" + publishGood.toString());
                Gson gson = new Gson();
                RequestBody requestBody = RequestBody.Companion.create(gson.toJson(publishGood), MediaType.Companion.parse("application/json"));
                Request request = new Request.Builder()
                        .url("http://39.97.173.40:8999/transaction/addtransinfo")
                        .post(requestBody)
                        .build();
                HttpUtil.sendOkHttpRequest(request, new okhttp3.Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        LogUtil.e(TAG, "在PublishGoods出错");
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) {

                    }
                });

            }
        };

    }

    /*
        * // 添加图片
    mSDImageList.add(imageUri);
    mImageAdapter.notifyDataSetChanged();

    // 长按删除
    mSDImageList.remove(position);
    mImageAdapter.notifyDataSetChanged();
    这里获取图片
        * */
    private void updatePhotos(List<String> mlist) {
        LogUtil.e(TAG, "更新最大图片" + mlist.toString());
// 在测试时发现照片总大小过大时，会出错java.net.SocketException:Connection reset;
//        可以压缩后再上传
        List<String> compressPhotos = new ArrayList<>();
        for (String inputphoto : mlist
        ) {
            compressPhotos.add(ImageCompress.getimage(inputphoto));
        }
        LogUtil.e(TAG, "压缩后的路径" + compressPhotos.toString());
        mlist = compressPhotos;
        LogUtil.e(TAG, "直接赋值看mlist内容" + mlist.toString());
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        for (String photo :
                mlist) {
            File file = new File(photo);
            builder.addFormDataPart( //给Builder添加上传的文件
                    "files",  //请求的名字
                    file.getName(), //文件的文字，服务器端用来解析的
                    RequestBody.create(MediaType.parse("application/octet-stream"), file) //创建RequestBody，把上传的文件放入
            );
        }
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url("http://39.97.173.40:8999/file/upload")
                .post(requestBody)
                .build();

        HttpUtil.sendOkHttpRequest(request, new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                LogUtil.e(TAG, e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responsedata = response.body().string();
                LogUtil.e(TAG, "在publishgood里面，上传后返回的图片数组" + responsedata);
                Gson gson = new Gson();
                Images images = gson.fromJson(responsedata, new TypeToken<Images>() {
                }.getType());
                photosname = images.getPhotos();
//                publishGood.setPhotos(photosname);
//                LogUtil.e(TAG, "在publishgood里面，上传后返回的整合图片字符串" + publishGood.getPhotos());
                Message msg = new Message();
                msg.obj = photosname;
                handler.sendMessage(msg);
            }
        });

    }


}
