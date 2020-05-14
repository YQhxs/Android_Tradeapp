package com.example.android;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.android.changeinfo.ChangeIntroduction;
import com.example.android.changeinfo.ChangeName;
import com.example.android.gson.Images;
import com.example.android.gson.User;
import com.example.android.util.BaseActivity;
import com.example.android.util.FileUtils;
import com.example.android.util.GetContext;
import com.example.android.util.HttpUtil;
import com.example.android.util.ImageCompress;
import com.example.android.util.LogUtil;
import com.example.android.util.PhotoUtil;
import com.example.android.widget.InfoItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Info extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "Info";
    private ArrayList<String> sexItems = new ArrayList<>();
    private TextView sex_itemViewById, nick_itemViewById, intro_itemViewById;
    private User user;
    private boolean ischanged = false;
    private ImageView back_image;
    private CircleImageView image_linearViewById;
    private PopupWindow popupWindow;
    //            网络加载慢，设置占位图
    private RequestOptions options = new RequestOptions().placeholder(R.drawable.touxiang);
    private String photopath;
//    private String bigAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        sexItems.add("男");
        sexItems.add("女");

        back_image = findViewById(R.id.back);
        back_image.setOnClickListener(this);

        user = (User) getIntent().getSerializableExtra("user_info");
        Log.e(TAG, "Trade界面传来的值" + user.toString());
//分别是头像，id，昵称，性别，介绍
        LinearLayout iamge_Linear = findViewById(R.id.edit_image);
        iamge_Linear.setOnClickListener(this);
        image_linearViewById = iamge_Linear.findViewById(R.id.info_image);
        image_linearViewById.setOnClickListener(this);
        String header_image = user.getPhoto();
        if (!header_image.equals("未设置")) {

            Glide.with(GetContext.getContext()).load("http://39.97.173.40:8999/file/" + header_image).apply(options).into(image_linearViewById);
        }

        InfoItem id_Item = findViewById(R.id.edit_id);
        TextView id_itemViewById = id_Item.findViewById(R.id.content_edt);
        id_itemViewById.setText(user.getUser_ID());

        InfoItem nick_Item = findViewById(R.id.edit_name);
        nick_itemViewById = nick_Item.findViewById(R.id.content_edt);
        nick_itemViewById.setText(user.getNick_NAME());
        nick_Item.setOnClickListener(this);

        InfoItem sex_Item = findViewById(R.id.edit_sex);
        sex_itemViewById = sex_Item.findViewById(R.id.content_edt);
        if (!user.getSex().equals("未设置")) {
            LogUtil.e("一开始不及进入", "shime");
            sex_itemViewById.setText(user.getSex().equals("man") ? "男" : "女");
        }
        sex_Item.setOnClickListener(this);

        InfoItem intro_Item = findViewById(R.id.edit_introduction);
        intro_itemViewById = intro_Item.findViewById(R.id.content_edt);
        intro_itemViewById.setText(user.getIntroduction());
        intro_Item.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
//        Intent intent = new Intent();
        switch (v.getId()) {
//            对象如果改变则对主页面的头像和名字重新请求
            case R.id.back:
                LogUtil.e("------在返回Trade是，看user对象是否改变", user.toString());
                if (!ischanged) {
                    setResult(RESULT_CANCELED);
                } else {
//                    intent.putExtra("changeduser",user);
                    setResult(RESULT_OK);
                }
                finish();
                break;
            case R.id.edit_image:
                show_popup_window(findViewById(R.id.info));
                break;
            case R.id.info_image:
//              if(user.getPhoto().equals("未设置")){
//                  new AvatarScan(GetContext.getContext(),R.drawable.touxiang).show();
//              }else {
////                  先不考虑本地缓存清掉后，图片不存在的问题
//                  new AvatarScan(GetContext.getContext(),bigAvatar).show();
//              }
                imgMax(findViewById(R.id.info), user.getPhoto());
                break;
            case R.id.edit_name:
                changeName();
                break;
            case R.id.edit_sex:
                changeSex();
                break;
            case R.id.edit_introduction:
                changeIntroduction();
                break;
        }
    }

    private void show_popup_window(View view) {
        LogUtil.e("------参考view为空对象", ":" + view);
//        if (popupWindow != null && popupWindow.isShowing()) {
//            return;
//        }
        LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.window_popup, null);
        popupWindow = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setAnimationStyle(R.style.Popupwindow);
        //点击外部弹出不消失
        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(false);
        //设置透明背景布局
        setTransparentBg();

        int[] location = new int[2];
        view.getLocationOnScreen(location);
        popupWindow.showAtLocation(view, Gravity.LEFT | Gravity.BOTTOM, 0, -location[1]);
        setButtonListeners(layout);
    }


    /**
     * 点击查看大图
     */
    public void imgMax(View view, String bigAvatar) {
        LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_avatar_scan, null);
        int height = getWindowManager().getDefaultDisplay().getHeight();
        popupWindow = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, height * 3 / 4);
        ImageView img = layout.findViewById(R.id.large_image);

//        popupWindow.setHeight();
        popupWindow.setAnimationStyle(R.style.Popupwindow);
        //点击外部弹出不消失
        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(false);
        //设置透明背景布局
        setTransparentBg();

        int[] location = new int[2];
        view.getLocationOnScreen(location);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        if (!bigAvatar.equals("未设置")) {
            Glide.with(GetContext.getContext()).load("http://39.97.173.40:8999/file/" + bigAvatar).into(img);
        } else {
            Glide.with(GetContext.getContext()).load(R.drawable.touxiang).into(img);
        }
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });

//
//        LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_avatar_scan, null);
//        // 加载自定义的布局文件
//        final AlertDialog dialog = new AlertDialog.Builder(GetContext.getContext(),R.style.Theme_AppCompat_Light_Dialog_Alert).create();
//        ImageView img = layout.findViewById(R.id.large_image);
//        if(!bigAvatar.equals("未设置")){
//            Glide.with(GetContext.getContext()).load("http://39.97.173.40:8999/file/"+bigAvatar).into(img);
//        }else {
//            Glide.with(GetContext.getContext()).load(R.drawable.touxiang).into(img);
//        }
//        dialog.setView(layout); // 自定义dialog
//        dialog.show();
//        // 点击布局文件（也可以理解为点击大图）后关闭dialog，这里的dialog不需要按钮
//        img.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View paramView) {
//
//                dialog.cancel();
//            }
//        });
    }

    //设置背景透明，且不可点击
    private void setTransparentBg() {
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;//调节透明度
        getWindow().setAttributes(lp);
        //监听弹窗
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //弹窗关闭  dismiss()时恢复原样
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //拦截弹窗外部点击事件
        if (popupWindow != null && popupWindow.isShowing()) {
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void setButtonListeners(LinearLayout layout) {
        Button album_button = layout.findViewById(R.id.album);
        album_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popupWindow != null && popupWindow.isShowing()) {
//                    toastUtil.showShortToast("movie");
                    PhotoUtil.start_album(Info.this, 1);
                    popupWindow.dismiss();
                }
            }
        });

        Button photo_button = layout.findViewById(R.id.photo);
        photo_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    photopath = PhotoUtil.start_camera(Info.this, 2);
                    popupWindow.dismiss();
                }
            }
        });

        Button cancel_button = layout.findViewById(R.id.cancel);
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LogUtil.e("-------授权请求吗是", "" + requestCode);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    PhotoUtil.openAlbum(Info.this, 1);
                } else {
                    Toast.makeText(GetContext.getContext(), "无法使用相册", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LogUtil.e("-------进入判断了么相机权限1", "--------");
                    photopath = PhotoUtil.start_camera(Info.this, 2);
                } else {
                    Toast.makeText(GetContext.getContext(), "拒绝后无法拍照设置头像", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    private void changeSex() {
        OptionsPickerView pvOptions = new OptionsPickerBuilder(Info.this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //选择了则显示并暂存LoginUser，退出时在保存至数据库
                sex_itemViewById.setText(sexItems.get(options1));
                updatechange();

            }
        }).setCancelColor(Color.GRAY).build();
        pvOptions.setPicker(sexItems);
        pvOptions.show();
    }

    private void changeName() {
        Intent intent = new Intent(GetContext.getContext(), ChangeName.class);
        intent.putExtra("user", user);
        startActivityForResult(intent, 3);
    }

    private void changeIntroduction() {
        Intent intent = new Intent(GetContext.getContext(), ChangeIntroduction.class);
        intent.putExtra("user", user);
        startActivityForResult(intent, 4);
    }

    private void updatechange() {
//把更改的性别上传到数据库
        final String sex = sex_itemViewById.getText().toString();
        if (sex.equals("男")) {
            user.setSex("man");
        } else if (sex.equals("女")) {
            user.setSex("woman");
        } else {
            return;
        }
        Gson gson = new Gson();
        String param = gson.toJson(user);
        LogUtil.d("-----在Info更新性别后观察sex值", "param-----" + param);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), param);
        Request request = new Request.Builder()
                .url("http://39.97.173.40:8999/user/update")
                .post(requestBody)
                .build();
        HttpUtil.sendOkHttpRequest(request, new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                LogUtil.e("服务响应错误", "---" + e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sex_itemViewById.setText(sex);
                        ischanged = true;
                    }
                });
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri image_uri;
        switch (requestCode) {
//              1表示相册2表示相机 3表示更改昵称的活动，4表示更改介绍的活动
            case 1:
                if (resultCode == RESULT_OK) {
                    image_uri = data.getData();
                    LogUtil.e("----从相册返回的图片uri", "" + image_uri);
                    Glide.with(this).load(image_uri).apply(options).into(image_linearViewById);

//                    压缩图片并返回所在路径，然后再上传服务器
                    uploadfile(ImageCompress.getimage(FileUtils.getFilePathByUri(GetContext.getContext(), image_uri)));
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
//                    LogUtil.e("----从相机拍摄返回的图片路径", "" + mfile.getAbsolutePath());
                    LogUtil.e("----从相机拍摄返回的图片路径", "" + photopath);
                    Glide.with(this).load(photopath).apply(options).into(image_linearViewById);

                    uploadfile(ImageCompress.getimage(photopath));
                }
                break;
            case 3:
                if (resultCode == RESULT_OK && data != null) {
                    ischanged = true;
                    String name = data.getStringExtra("name");
                    nick_itemViewById.setText(name);
                    user.setNick_NAME(name);
                }
                break;
            case 4:
                ischanged = true;
                String introduction = data.getStringExtra("introduction");
                intro_itemViewById.setText(introduction);
                user.setIntroduction(introduction);
                break;
            case 5:
                break;
        }
    }

    //  如果返回时，popupwindow未关闭，先关闭popupwindow
    @Override
    public void onBackPressed() {

        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            if (!ischanged) {
                setResult(RESULT_CANCELED);
            } else {
                setResult(RESULT_OK);
            }
            finish();
        }
    }

    //下面完全没必要，之所以用file做参数，因为之前，不知道uri与图片真实路径的关系。通过FileUtils工具类去转换到真实路径，然后在androidmanifest加上访问权限，注意android10；
//    private void uploadfile(File fileimage) {
//        MediaType mediaType = MediaType.parse("multipart/form-data");
//        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
//                .addFormDataPart("files", fileimage.getAbsolutePath(),
//                        RequestBody.create(MediaType.parse("application/octet-stream"),
//                                fileimage))
//                .build();
//        Request request = new Request.Builder()
//                .url("http://39.97.173.40:8999/file/upload")
//                .method("POST", body)
//                .addHeader("Content-Type", "multipart/form-data")
//                .build();
//        HttpUtil.sendOkHttpRequest(request, new okhttp3.Callback() {
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                LogUtil.e("----上传文件错误", "" + e.getMessage());
//            }
//
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                String responsedata = response.body().string();
//                Gson gson = new Gson();
//                Images images;
//                images = gson.fromJson(responsedata, new TypeToken<Images>() {
//                }.getType());
//                final String path = images.getIamgespath().get(0);
//                LogUtil.e("----上传文件后返回的图片数组，这里只有一个头像", "" + images.toString() + ":" + path);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        user.setPhoto(path);
//                        updateimage();
//                        ischanged = true;
//                    }
//                });
//            }
//        });
//
//    }
    private void uploadfile(String filepath) {
        File mfile = new File(filepath);
        LogUtil.e("----能否打开图片", ":" + mfile);
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("files", filepath,
                        RequestBody.create(MediaType.parse("application/octet-stream"),
                                new File(filepath)))
                .build();
        Request request = new Request.Builder()
                .url("http://39.97.173.40:8999/file/upload")
                .method("POST", body)
                .addHeader("Content-Type", "multipart/form-data")
                .build();
        HttpUtil.sendOkHttpRequest(request, new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                LogUtil.e("----上传文件错误", "" + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responsedata = response.body().string();
                Gson gson = new Gson();
                Images images;
                images = gson.fromJson(responsedata, new TypeToken<Images>() {
                }.getType());
                final String path = images.getIamgespath().get(0);
                LogUtil.e("----上传文件后返回的图片数组，这里只有一个头像", ":" + path);
                user.setPhoto(path);
                updateimage();
                ischanged = true;

            }
        });

    }

    private void updateimage() {
//向服务器相应用户更新头像路径；
        Gson gson = new Gson();
        String param = gson.toJson(user);
        LogUtil.e("-----在Info更新图片后观察photo值", "param-----" + param);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), param);
        Request request = new Request.Builder()
                .url("http://39.97.173.40:8999/user/update")
                .post(requestBody)
                .build();
        HttpUtil.sendOkHttpRequest(request, new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                LogUtil.e("服务响应错误", "---" + e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {

            }
        });

    }
}
