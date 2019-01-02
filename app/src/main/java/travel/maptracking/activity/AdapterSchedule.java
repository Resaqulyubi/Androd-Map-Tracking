package travel.maptracking.activity;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import travel.maptracking.R;
import travel.maptracking.model.schedule;

/**
 * Created by WINDOWS 10 on 14/09/2017.
 */

public class AdapterSchedule extends BaseAdapter {

    private static final String TAG = "AdapterSchedule";
    private Context mContext;
    private List<schedule.Data> scheduleList = new ArrayList<>();
    TextView tv_nomor,tv_id,tv_tujuan,tv_jalan,tv_status ;
    Button btn_view;
    private int lastFocussedPosition = -1;
    private onEditClickListener mClickListener = null;
    private Handler handler = new Handler();
    public AdapterSchedule(Context mContext , onEditClickListener mClickListener) {
        this.mContext = mContext;
       this.mClickListener = mClickListener;
    }

    @Override
    public int getCount() {
        return scheduleList.size();
    }

    @Override
    public schedule.Data getItem(int position) {
        return scheduleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.adapter_schedule_record, null);


        tv_nomor = v.findViewById(R.id.tv_nomor);
        tv_id = v.findViewById(R.id.tv_id);
        tv_tujuan = v.findViewById(R.id.tv_tujuan);
        tv_jalan = v.findViewById(R.id.tv_jalan);
        tv_status = v.findViewById(R.id.tv_status);
        btn_view = v.findViewById(R.id.btn_view);


        tv_nomor.setText(String.valueOf(position+1));


        schedule.Data  data=  scheduleList.get(position);
        tv_id.setText(data.getId());
        tv_tujuan.setText(data.getPickup_point()+"-"+data.getDestination());
        tv_jalan.setText(data.getRoute());
        tv_status.setText(data.getStatus());


        btn_view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(mClickListener != null)
                    mClickListener.onBtnClick(position);
            }
        });

        return v;
    }

    public void setList(List<schedule.Data> peralatanDBS){
        this.scheduleList = peralatanDBS;
        notifyDataSetChanged();
    }


    public  void setListStatusBayar(){


    }

    public List<schedule.Data> getScheduleList(){
        Log.d(TAG, "setList: "+ scheduleList.toString());
        return scheduleList;

    }

//    public void setOnClick(onEditClickListener mListener){
//        this.mListener = mListener;
//    }

    public interface onEditClickListener {
        public abstract void onBtnClick(int position);
    }


}
