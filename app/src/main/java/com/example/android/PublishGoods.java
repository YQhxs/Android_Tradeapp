package com.example.android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.util.GetContext;
import com.example.android.widget.NineGridAdapter;

import java.util.ArrayList;
import java.util.List;

public class PublishGoods extends AppCompatActivity {
    private RecyclerView mRcyvImage;
    private NineGridAdapter mNineGridAdapter;
    private List<String> mSDImageList = new ArrayList<>();
    private Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_goods);

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
                Toast.makeText(GetContext.getContext(), "分类：" + mcategory[position], Toast.LENGTH_SHORT).show();
                //此处是下拉框逻辑
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showImageDialog() {
        Toast.makeText(GetContext.getContext(), "添加照片", Toast.LENGTH_SHORT).show();
        Bitmap bitmap;
        Uri uri;
        bitmap = BitmapFactory.decodeResource(GetContext.getContext().getResources(), R.drawable.carpool);
        uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null));
        mSDImageList.add(String.valueOf(uri));
        mNineGridAdapter.notifyDataSetChanged();
//        选择照片或拍照逻辑
    }

    private void showDeletePop(View view, int position) {
//    长按删除逻辑
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
}
