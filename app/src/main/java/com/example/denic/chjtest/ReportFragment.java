package com.example.denic.chjtest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by Denic on 2017/12/15.
 */

public class ReportFragment extends Fragment implements MyDialog.OnButtonClickListener, AdapterView.OnItemClickListener {

    private static final String severPath="http://192.168.8.103/CHJ_API/api/android/";
    private MyDialog dialog;// 图片选择对话框
    public static final int NONE = 0;
    public static final int PHOTOHRAPH = 1;// 拍照
   // public static final int PHOTOZOOM = 2; // 缩放
    public static final int PHOTORESOULT = 3;// 结果
   // public static final String IMAGE_UNSPECIFIED = "image/*";
    private GridView gridView; // 网格显示缩略图
    private final int IMAGE_OPEN = 4; // 打开图片标记
    private String pathImage; // 选择图片路径
    private Bitmap bmp; // 导入临时图片
    private ArrayList<HashMap<String, Object>> imageItem=new ArrayList<HashMap<String, Object>>();
    private SimpleAdapter simpleAdapter; // 适配器
    private static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");
    private final OkHttpClient client = new OkHttpClient();
    private  Uri imageUri;
    View view;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.report_fragment, container, false);
        init();
        initData();
        GetSelectData();
        Button upload=(Button) view.findViewById(R.id.report_button);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImg();
            }
        });

        return view;
    }
    private void GetSelectData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //OkHttpClient client=new OkHttpClient();
                    Request request=new Request.Builder()
                            .url(severPath+"GetMap")
                            .build();
                    Response response=client.newCall(request).execute();
                    String responseDate = response.body().string();
                    parseSelectJson(responseDate);
                    initSpinner(parseSelectJson(responseDate),(Spinner) view.findViewById(R.id.map_spinner));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void initSpinner(final ArrayList<spinner_class> dataList,final Spinner spinner){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayAdapter<spinner_class> adapter=new ArrayAdapter<spinner_class>(getActivity(),android.R.layout.simple_spinner_item,dataList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            }
        });

    }
    private  ArrayList<spinner_class> parseSelectJson(String JsonData) {
        Gson gson = new Gson();
        ArrayList<spinner_class> listData = gson.fromJson(JsonData,new TypeToken<ArrayList<spinner_class>>(){}.getType());
        return  listData;

    }
    private void init() {
        gridView = (GridView) view.findViewById(R.id.grid_view);
        gridView.setOnItemClickListener(this);
        dialog = new MyDialog(getActivity());
        dialog.setOnButtonClickListener(this);


//        Spinner spinner=
//        ArrayList<spinner_class> dataList = new ArrayList<spinner_class>();
//        ArrayList<String> datalist=new ArrayList<String>();
//
//        for (int i = 0; i < 18; i++) {
//
//            spinner_class c = new spinner_class(i + "",i+"q");
//            dataList.add(c);
//        }
//        ArrayAdapter<spinner_class> adapter=new ArrayAdapter<spinner_class>(getActivity(),android.R.layout.simple_spinner_item,dataList);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);

    }

    private void initData() {
        /*
         * 载入默认图片添加图片加号
         */
        bmp = BitmapFactory.decodeResource(getResources(),
                R.drawable.gridview_addpic); // 加号
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("itemImage", bmp);
        imageItem.add(map);
        simpleAdapter = new SimpleAdapter(getActivity(), imageItem,
                R.layout.griditem_addpic, new String[]{"itemImage"},
                new int[]{R.id.imageView1});
        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                // TODO Auto-generated method stub
                if (view instanceof ImageView && data instanceof Bitmap) {
                    ImageView i = (ImageView) view;
                    i.setImageBitmap((Bitmap) data);
                    return true;
                }
                return false;
            }
        });
        gridView.setAdapter(simpleAdapter);
    }


    @Override
    public void camera() {
        // TODO Auto-generated method stub
        File outputImage = new File(getActivity().getExternalCacheDir(), "output_image.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT>=24){
            imageUri= FileProvider.getUriForFile(getActivity(),"com.example.denic.chjtest.fileprovider",outputImage);

        }else{
            imageUri=Uri.fromFile(outputImage);
        }
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, PHOTOHRAPH);
    }

    @Override
    public void gallery() {
        // TODO Auto-generated method stub
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_OPEN);

    }

    @Override
    public void cancel() {
        // TODO Auto-generated method stub
        dialog.cancel();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == NONE)
            return;
        if (data == null)
            return;
        // 拍照
        if (requestCode == PHOTOHRAPH) {

            try {
                Bitmap photo = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri));
                setGridViewPhoto(photo);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
        // 相册
        if (requestCode == PHOTORESOULT) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap photo = extras.getParcelable("data");
                setGridViewPhoto(photo);
            }

        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    public void setGridViewPhoto(Bitmap photo){

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 75, stream);// (0-100)压缩文件
        // 将图片放入gridview中
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("itemImage", photo);
        imageItem.add(0,map);
        simpleAdapter = new SimpleAdapter(getActivity(), imageItem,
                R.layout.griditem_addpic, new String[]{"itemImage"},
                new int[]{R.id.imageView1});
        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                // TODO Auto-generated method stub
                if (view instanceof ImageView && data instanceof Bitmap) {
                    ImageView i = (ImageView) view;
                    i.setImageBitmap((Bitmap) data);
                    return true;
                }
                return false;
            }
        });
        gridView.setAdapter(simpleAdapter);
        simpleAdapter.notifyDataSetChanged();
        dialog.dismiss();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (!TextUtils.isEmpty(pathImage)) {
            Bitmap addbmp = BitmapFactory.decodeFile(pathImage);
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("itemImage", addbmp);
            imageItem.add(map);
            simpleAdapter = new SimpleAdapter(getActivity(), imageItem,
                    R.layout.griditem_addpic, new String[]{"itemImage"},
                    new int[]{R.id.imageView1});
            simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object data,
                                            String textRepresentation) {
                    // TODO Auto-generated method stub
                    if (view instanceof ImageView && data instanceof Bitmap) {
                        ImageView i = (ImageView) view;
                        i.setImageBitmap((Bitmap) data);
                        return true;
                    }
                    return false;
                }
            });
            gridView.setAdapter(simpleAdapter);
            simpleAdapter.notifyDataSetChanged();
            // 刷新后释放防止手机休眠后自动添加
            pathImage = null;
            dialog.dismiss();
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        // TODO Auto-generated method stub
        if (imageItem.size() == 10) { // 第一张为默认图片
            Toast.makeText(getActivity(), "图片数9张已满",
                    Toast.LENGTH_SHORT).show();
        } else if (imageItem.size()-1 ==position) { // 点击图片位置为+ 0对应0张图片
            // 选择图片
            dialog.show();
            // 通过onResume()刷新数据
        } else {
            dialog(position);
        }

    }

    /*
     * Dialog对话框提示用户删除操作 position为删除图片位置
     */
    protected void dialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("确认移除已添加图片吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                imageItem.remove(position);
                simpleAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    private void uploadImg() {

        // mImgUrls为存放图片的url集合
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (int i = 0; i <imageItem.size()-1 ; i++) {
            HashMap<String, Object> map = imageItem.get(i);
            Bitmap photo =(Bitmap) map.get("itemImage");
            File f=new File(saveMyBitmap(photo).getAbsolutePath());
            if (f!=null) {
                builder.addFormDataPart("img", f.getName(), RequestBody.create(MEDIA_TYPE_JPG, f));
            }
        }
        //添加其它信息
        Spinner map=(Spinner) view.findViewById(R.id.map_spinner);
        String mapid=((spinner_class)map.getSelectedItem()).getValue();
       builder.addFormDataPart("mapid",mapid);
//        builder.addFormDataPart("mapX", SharedInfoUtils.getLongitude());
//        builder.addFormDataPart("mapY",SharedInfoUtils.getLatitude());
//        builder.addFormDataPart("name",SharedInfoUtils.getUserName());


        MultipartBody requestBody = builder.build();
        //构建请求
        Request request = new Request.Builder()
                .url(severPath+"PostFormData")//地址
                .post(requestBody)//添加请求体
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                System.out.println("上传失败:e.getLocalizedMessage() = " + e.getLocalizedMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                System.out.println("上传照片成功：response = " + response.body().string());
                //ToastCustom.makeText(PictureListActivity.this, "上传成功", Toast.LENGTH_LONG).show();


            }
        });

    }
    public File saveMyBitmap(Bitmap mBitmap){
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File file = null;
        try {
            file = File.createTempFile(
                    "up_",  /* prefix */
                    ".jpg",         /* suffix */
                    getActivity().getCacheDir()     /* directory */
            );

            FileOutputStream out=new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 20, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  file;
    }


}
