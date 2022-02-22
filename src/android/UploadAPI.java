package com.ahmedayachi.webview;

import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.Call;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import java.util.ArrayList;


public interface UploadAPI {

    @Multipart
    @POST("/")
    Call<ResponseBody> uploadFile(@Part ArrayList<MultipartBody.Part> fileParts,@Part("upload") RequestBody requestbody);
}
