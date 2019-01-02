package travel.maptracking.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;
import travel.maptracking.model.db.OutboxDB;
import travel.maptracking.network.Api;
import travel.maptracking.util.ConnectionUtil;
import travel.maptracking.util.Constant;
import travel.maptracking.util.Util;

public class OutBoxService extends Service {
    private static final String TAG = "OutBoxService";
    private volatile HandlerThread handlerThread;
    private StartOutBoxHandler startOutBoxHandler;
    private ExecuteOutBoxHandler executeOutBoxHandler;
    private Context context = null;
    private ArrayList<Long> outBoxIDInProccessList = new ArrayList<>();
    private travel.maptracking.network.Api api;
    private Handler handler;
    private Boolean isHandlerThreadStarted = false;
    private Runnable checkOutBox = () -> {
        restartHandlerThread();
        startOutBoxHandler.post(startOutBoxHandler.runnable);
        checkOutBox();
    };
    private Runnable stopService = this::stopSelf;
    private Boolean isCheckOutBoxStarted = false;
    private Boolean isExecutingOutBox = false;
    private Boolean isStopRequest = false;

    public OutBoxService() {
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        isStopRequest = true;
        handler.removeCallbacks(checkOutBox);
        handler.postDelayed(stopService, 1000 * 3);
    }

    private void restartHandlerThread() {
        if (!isHandlerThreadStarted) {
            if (!handlerThread.isAlive()) {
                startOutBoxHandler.removeCallbacksAndMessages(null);
                executeOutBoxHandler.removeCallbacksAndMessages(null);
                handlerThread.start();
                startOutBoxHandler = new StartOutBoxHandler(handlerThread.getLooper());
                executeOutBoxHandler = new ExecuteOutBoxHandler(handlerThread.getLooper());
                isHandlerThreadStarted = true;
            }
        }
    }

    private void checkOutBox() {
        handler.postDelayed(checkOutBox, 1000 * 60);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;
        api = Api.getInstance(context);
        handlerThread = new HandlerThread("OutBoxService.HandlerThread");
        handlerThread.start();
        isHandlerThreadStarted = true;
        startOutBoxHandler = new StartOutBoxHandler(handlerThread.getLooper());
        executeOutBoxHandler = new ExecuteOutBoxHandler(handlerThread.getLooper());
        handler = new Handler();
        checkOutBox();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Long idOutBox = 0L;

        if (intent != null) {
            if (intent.hasExtra("OUTBOX_ID")) {
                idOutBox = intent.getLongExtra("OUTBOX_ID", 0);
            }
            if (intent.getAction() != null) {
                switch (intent.getAction()) {
                    case Constant.ACTION_START_OUTBOX:
                        if (isStopRequest) {
                            handler.removeCallbacks(stopService);
                            checkOutBox();
                        } else {
                            restartHandlerThread();
                            startOutBoxHandler.post(startOutBoxHandler.runnable);
                        }
                        isStopRequest = false;
                        break;
                    case Constant.ACTION_STOP_OUTBOX:
                        isStopRequest = true;
                        handler.removeCallbacks(checkOutBox);
                        handler.postDelayed(stopService, 1000 * 3);
                        break;
                    case Constant.ACTION_STOP_OUTBOX_NOW:
                        isStopRequest = true;
                        handler.removeCallbacks(checkOutBox);
                        handler.post(stopService);
                        break;
                    case Constant.ACTION_EXECUTE_OUTBOX:
                        if (idOutBox > 0 ) {
                            OutboxDB outboxDB = OutboxDB.findById(OutboxDB.class, idOutBox);
                            if (outboxDB != null){
                                if (ConnectionUtil.isConnectionAvailable(context)) {
                                    if (!outBoxIDInProccessList.contains(outboxDB.getId())) {
                                        restartHandlerThread();
                                        isStopRequest = false;
                                        outBoxIDInProccessList.add(outboxDB.getId());
                                        if (outboxDB.getMethod().equals(OutboxDB.FLAG_METHOD_POST)) {
                                            executeOutBoxHandler.post(executeOutBoxHandler.new PostRunnable(outboxDB));
                                        } else if (outboxDB.getMethod().equals(OutboxDB.FLAG_METHOD_PUT)) {
                                            executeOutBoxHandler.post(executeOutBoxHandler.new PutRunnable(outboxDB));
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    case Constant.ACTION_CANCEL_OUTBOX:
                        break;
                }
            }
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        startOutBoxHandler.removeCallbacksAndMessages(null);
        executeOutBoxHandler.removeCallbacksAndMessages(null);
        handlerThread.quit();
        isHandlerThreadStarted = false;
        super.onDestroy();
    }

    private class StartOutBoxHandler extends Handler {
        private Runnable runnable = () -> {
            if (!isCheckOutBoxStarted) {
                isCheckOutBoxStarted = true;
                if (!isStopRequest) {
                    if (ConnectionUtil.isConnectionAvailable(context)) {
                            String whereclause[] = new String[]{"3"};
                            List<OutboxDB> outbox = OutboxDB.find(OutboxDB.class, "status != ?", whereclause);

                            if (outbox.size() > 0 && ConnectionUtil.isConnectionAvailable(context)) {
                                for (OutboxDB outboxDB :
                                        outbox) {
                                    if (!outBoxIDInProccessList.contains(outboxDB.getId()) ) {
                                        outBoxIDInProccessList.add(outboxDB.getId());
                                        if (outboxDB.getMethod().equals(OutboxDB.FLAG_METHOD_POST)) {
                                            executeOutBoxHandler.post(executeOutBoxHandler.new PostRunnable(outboxDB));
                                        } else if (outboxDB.getMethod().equals(OutboxDB.FLAG_METHOD_PUT)) {
                                            executeOutBoxHandler.post(executeOutBoxHandler.new PutRunnable(outboxDB));
                                        }
                                    }
                                }
                            }
                    }
                }
                isCheckOutBoxStarted = false;
            }
        };

        StartOutBoxHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    private class ExecuteOutBoxHandler extends Handler {
        ExecuteOutBoxHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }

        private class PostRunnable implements Runnable {
            private OutboxDB outboxDB;

            PostRunnable(OutboxDB outboxDB) {
                this.outboxDB = outboxDB;
            }

            @Override
            public void run() {
                if (!isStopRequest) {
                    if (outboxDB != null) {
                        if (outboxDB.getId() != 0  && outboxDB.getMethod().equals(OutboxDB.FLAG_METHOD_POST) && !outboxDB.getStatus().equals(OutboxDB.FLAG_SENDING)) {
                            isExecutingOutBox = true;
                            Util.updateOutboxStatus(context,outboxDB.getId(),OutboxDB.FLAG_SENDING,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

                            String outboxStatus = OutboxDB.FLAG_SENDING;

                            RequestBody requestBody = null;
                                requestBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), outboxDB.getParameter());

                            try (Response response = api.post(outboxDB.getUrl(), requestBody)) {
                                if (!response.isSuccessful())
                                    throw new IOException("Unexpected code = " + response);

                                String responseBodyString = response.body().string();
                                JSONObject responseBodyObject = new JSONObject(responseBodyString);
                                if (responseBodyObject.getBoolean("status")) {
                                    outboxStatus = OutboxDB.FLAG_DONE;
                                } else {

                                        outboxStatus = OutboxDB.FLAG_FAILED;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                outboxStatus = OutboxDB.FLAG_FAILED;
                            } catch (JSONException e) {
                                e.printStackTrace();
                                outboxStatus = OutboxDB.FLAG_FAILED;
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                                outboxStatus = OutboxDB.FLAG_FAILED;
                            } finally {
                                Util.updateOutboxStatus(context,outboxDB.getId(),outboxStatus,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

                                outBoxIDInProccessList.remove(outBoxIDInProccessList.indexOf(outboxDB.getId()));
                                isExecutingOutBox = false;

                                long idNextExecute = Util.getOutboxGetNextidexecute(context, outboxDB.getId());
                                if (idNextExecute != 0){
                                    OutboxDB db = OutboxDB.findById(OutboxDB.class, idNextExecute);
                                    if (db != null && !db.getStatus().equals(OutboxDB.FLAG_DONE) && !outBoxIDInProccessList.contains(db.getId())) {
                                        outBoxIDInProccessList.add(db.getId());
                                        if (db.getMethod().equals(OutboxDB.FLAG_METHOD_POST)) {
                                            executeOutBoxHandler.post(executeOutBoxHandler.new PostRunnable(db));
                                        } else if (db.getMethod().equals(OutboxDB.FLAG_METHOD_PUT)) {
                                            executeOutBoxHandler.post(executeOutBoxHandler.new PutRunnable(db));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        private class PutRunnable implements Runnable {
            private OutboxDB outboxDB;

            PutRunnable(OutboxDB outboxDB) {
                this.outboxDB = outboxDB;
            }

            @Override
            public void run() {
                if (!isStopRequest) {
                    if (outboxDB != null) {
                        if (outboxDB.getId() != 0  && outboxDB.getMethod().equals(OutboxDB.FLAG_METHOD_PUT) && !outboxDB.getStatus().equals(OutboxDB.FLAG_SENDING)) {
                            isExecutingOutBox = true;
                            Util.updateOutboxStatus(context,outboxDB.getId(),OutboxDB.FLAG_SENDING,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

                            String outboxStatus=OutboxDB.FLAG_SENDING;
                            RequestBody requestBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), outboxDB.getParameter());
                            try (Response response = api.put(outboxDB.getUrl(), requestBody)) {
                                if (!response.isSuccessful())
                                    throw new IOException("Unexpected code = " + response);

                                String responseBodyString = response.body().string();
                                JSONObject responseBodyObject = new JSONObject(responseBodyString);
                                if (responseBodyObject.getBoolean("status")) {
                                    outboxStatus = OutboxDB.FLAG_DONE;
                                } else {
                                    outboxStatus = OutboxDB.FLAG_FAILED;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                outboxStatus=OutboxDB.FLAG_FAILED;
                            } catch (JSONException e) {
                                e.printStackTrace();
                                outboxStatus=OutboxDB.FLAG_FAILED;
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                                outboxStatus=OutboxDB.FLAG_FAILED;
                            } finally {
                                Util.updateOutboxStatus(context,outboxDB.getId(),outboxStatus,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

                                outBoxIDInProccessList.remove(outBoxIDInProccessList.indexOf(outboxDB.getId()));
                                isExecutingOutBox = false;

                                long idNextExecute = Util.getOutboxGetNextidexecute(context, outboxDB.getId());
                                if (idNextExecute != 0){
                                    OutboxDB db = OutboxDB.findById(OutboxDB.class, idNextExecute);
                                    if (db != null && !db.getStatus().equals(OutboxDB.FLAG_DONE) && !outBoxIDInProccessList.contains(db.getId())) {
                                        outBoxIDInProccessList.add(db.getId());
                                        if (db.getMethod().equals(OutboxDB.FLAG_METHOD_POST)) {
                                            executeOutBoxHandler.post(executeOutBoxHandler.new PostRunnable(db));
                                        } else if (db.getMethod().equals(OutboxDB.FLAG_METHOD_PUT)) {
                                            executeOutBoxHandler.post(executeOutBoxHandler.new PutRunnable(db));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


}
