package travel.maptracking.network;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import travel.maptracking.R;



/**
 * Created by adty on 21/02/18.
 */

public class Api {
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private static final String TAG = "Api";
    private long timeOut = 15;
    private long defaultTimeOut = 15;
    private String defaultApiHost="";
    private static Api api = null;

    public static synchronized Api getInstance(Context context) {
        if (api == null) {
            api = new Api(context);
        } else {
            api.setContext(context);
        }
        return api;
    }

    public String getApiHost() {
        return ApiHost;
    }

    public Api setApiHost(String apiHost) {
        ApiHost = apiHost;
        httpUrlBuilder = new HttpUrl.Builder()
                .scheme(context.getString(R.string.api_scheme))
                .host("api.aladhan.com")
                .addPathSegments("v1/timings");


        httpUrl = httpUrlBuilder.build();
        request = new Request.Builder()
                .url( httpUrl.toString())
                .build();
        return this;
    }

    private String ApiHost="localhost";

    public String getApi_segment() {
        return api_segment;
    }

    public Api setApi_segment(String api_segment) {
        this.api_segment = api_segment;
        return this;
    }

    private String api_segment="";
    private Context context;
    private HttpUrl httpUrl;
    private HttpUrl.Builder httpUrlBuilder;
    private Request request;
    private HttpLoggingInterceptor httpLoggingInterceptor;
    private OkHttpClient okHttpClient;
    private OkHttpClient.Builder okHttpClientBuilder;

    public Api(Context context) {
        this.context = context;

        httpUrlBuilder = new HttpUrl.Builder()
                .scheme(context.getString(R.string.api_scheme))
                .host(context.getString(R.string.api_host))
                .addPathSegment(context.getString(R.string.api_segment));



        httpUrl = httpUrlBuilder.build();
        request = new Request.Builder()
                .url(httpUrl.toString())
                .build();
        httpLoggingInterceptor = new HttpLoggingInterceptor(message -> Log.d(TAG, message)).setLevel(HttpLoggingInterceptor.Level.BODY);

        okHttpClientBuilder = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(getTimeOut(), TimeUnit.SECONDS);

        okHttpClient = okHttpClientBuilder.build();
    }

    public Api(Context context, boolean isPrayApi) {
        this.context = context;

        httpUrlBuilder = new HttpUrl.Builder()
                .scheme(context.getString(R.string.api_scheme))
                .host("time.siswadi.com")
                .addPathSegments("pray");

        httpUrl = httpUrlBuilder.build();
        request = new Request.Builder()
                .url(httpUrl.toString())
                .build();
        httpLoggingInterceptor = new HttpLoggingInterceptor(message -> Log.d(TAG, message)).setLevel(HttpLoggingInterceptor.Level.BODY);

        okHttpClientBuilder = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(getTimeOut(), TimeUnit.SECONDS);

        okHttpClient = okHttpClientBuilder.build();
    }

    public Api(Context context, HttpUrl.Builder httpUrlBuilder) {
        this.context = context;
        this.httpUrlBuilder = httpUrlBuilder;
        httpUrl = httpUrlBuilder.build();
        request = new Request.Builder()
                .url(httpUrl.toString())
                .build();
        httpLoggingInterceptor = new HttpLoggingInterceptor(message -> Log.d(TAG, message)).setLevel(HttpLoggingInterceptor.Level.BODY);

        okHttpClientBuilder = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(getTimeOut(), TimeUnit.SECONDS);

        okHttpClient = okHttpClientBuilder.build();
    }

    public Context getContext() {
        return context;
    }

    public Api setContext(Context context) {
        this.context = context;
        return this;
    }

    private Response delete(String url, @Nullable RequestBody requestBody, @Nullable FormBody.Builder formBody, @Nullable Callback callback) {
        String fullUrl = httpUrl.newBuilder().addPathSegments(url).build().toString();
        Response response = null;

        if (formBody == null) {
            formBody = new FormBody.Builder();
        }

        if (requestBody == null) {
            requestBody = formBody.build();
        }

        Request deleteRequest = request.newBuilder()
                .url(fullUrl)
                .delete(requestBody)
                .build();

        if (callback == null) {
            try {
                response = okHttpClient.newBuilder().authenticator(Authenticator.NONE).build().newCall(deleteRequest).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            okHttpClient.newBuilder().authenticator(Authenticator.NONE).build().newCall(deleteRequest).enqueue(callback);
        }

        setTimeOut(defaultTimeOut);

        return response;
    }

    public Response delete(String url, FormBody.Builder formBody) {
        return delete(url, null, formBody, null);
    }

    public Response delete(String url, FormBody.Builder formBody, Callback callback) {
        return delete(url, null, formBody, callback);
    }

    public Response delete(String url, RequestBody requestBody) {
        return delete(url, requestBody, null, null);
    }

    public Response delete(String url, RequestBody requestBody, Callback callback) {
        return delete(url, requestBody, null, callback);
    }

    public static String requestBodyToString(RequestBody requestBody) {
        String requestBodyString = "";
        try {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            requestBodyString = buffer.readUtf8();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return requestBodyString;
    }

    public Response download(String url) {
        return download(url, null);
    }

    public Response download(String url, @Nullable Callback callback) {
        Response response = null;

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(getTimeOut(), TimeUnit.SECONDS)
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        if (callback == null) {
            try {
                response = okHttpClient.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            okHttpClient.newCall(request).enqueue(callback);
        }

        setTimeOut(defaultTimeOut);

        return response;
    }

    public long getDefaultTimeOut() {
        return defaultTimeOut;
    }

    public Api setDefaultTimeOut(long defaultTimeOut) {
        this.defaultTimeOut = defaultTimeOut;
        setTimeOut(defaultTimeOut);
        return this;
    }

    public long getTimeOut() {
        return timeOut;
    }

    public Api setTimeOut(long timeOut) {
        this.timeOut = timeOut;
        okHttpClient = okHttpClient.newBuilder().connectTimeout(timeOut, TimeUnit.SECONDS).build();
        return this;
    }



    public Response get(String url, @Nullable HttpUrl.Builder httpUrlBuilder, @Nullable Callback callback) {
        Response response = null;

        if (httpUrlBuilder == null) {
            httpUrlBuilder = new HttpUrl.Builder();
        }

        String fullUrl = httpUrl.newBuilder()
                .addPathSegments(url)
                .query(httpUrlBuilder
                        .scheme(httpUrl.scheme())
                        .host(httpUrl.host())
                        .build().query())
                .build().toString();

        Request getRequest = request.newBuilder()
                .url(fullUrl)
                .get()
                .build();

        if (callback == null) {
            try {
                response = okHttpClient.newBuilder().authenticator(Authenticator.NONE).build().newCall(getRequest).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            okHttpClient.newBuilder().authenticator(Authenticator.NONE).build().newCall(getRequest).enqueue(callback);
        }

        setTimeOut(defaultTimeOut);

        return response;
    }

    public Response get(String url) {
        return get(url, null, null);
    }

    public Response get(String url, HttpUrl.Builder httpUrlBuilder) {
        return get(url, httpUrlBuilder, null);
    }

    public Response get(String url, Callback callback) {
        return get(url, null, callback);
    }

    private Response post(String url, @Nullable RequestBody requestBody, @Nullable FormBody.Builder formBody, @Nullable Callback callback) {
        String fullUrl = httpUrl.newBuilder().addPathSegments(url).build().toString();
        Response response = null;

        if (formBody == null) {
            formBody = new FormBody.Builder();
        }

        if (requestBody == null) {
            requestBody = formBody.build();
        }

        Request postRequest = request.newBuilder()
                .url(fullUrl)
                .post(requestBody)
                .build();

        if (callback == null) {
            try {
                response = okHttpClient.newBuilder().authenticator(new ApiAuthenticator()).build().newCall(postRequest).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            okHttpClient.newBuilder().authenticator(Authenticator.NONE).build().newCall(postRequest).enqueue(callback);
        }

        setTimeOut(defaultTimeOut);

        return response;
    }

    public Response post(String url, FormBody.Builder formBody) {
        return post(url, null, formBody, null);
    }

    public Response post(String url, FormBody.Builder formBody, Callback callback) {
        return post(url, null, formBody, callback);
    }

    public Response post(String url, RequestBody requestBody) {
        return post(url, requestBody, null, null);
    }

    public Response post(String url, RequestBody requestBody, Callback callback) {
        return post(url, requestBody, null, callback);
    }

    private Response put(String url, @Nullable RequestBody requestBody, @Nullable FormBody.Builder formBody, @Nullable Callback callback) {
        String fullUrl = httpUrl.newBuilder().addPathSegments(url).build().toString();
        Response response = null;

        if (formBody == null) {
            formBody = new FormBody.Builder();
        }

        if (requestBody == null) {
            requestBody = formBody.build();
        }

        Request putRequest = request.newBuilder()
                .url(fullUrl)
                .put(requestBody)
                .build();

        if (callback == null) {
            try {
                response = okHttpClient.newBuilder().authenticator(Authenticator.NONE).build().newCall(putRequest).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            okHttpClient.newBuilder().authenticator(Authenticator.NONE).build().newCall(putRequest).enqueue(callback);
        }

        setTimeOut(defaultTimeOut);

        return response;
    }

    public Response put(String url, FormBody.Builder formBody) {
        return put(url, null, formBody, null);
    }

    public Response put(String url, FormBody.Builder formBody, Callback callback) {
        return put(url, null, formBody, callback);
    }

    public Response put(String url, RequestBody requestBody) {
        return put(url, requestBody, null, null);
    }

    public Response put(String url, RequestBody requestBody, Callback callback) {
        return put(url, requestBody, null, callback);
    }



    private class ApiAuthenticator implements Authenticator {
        @Nullable
        @Override
        public Request authenticate(Route route, Response response) {
            String authErrorType = "";
            String expiredToken = "";
            int refreshTokenResponseCode = -1;
            if (response.code() == 401) {


        }
            return null;
    }}
}
