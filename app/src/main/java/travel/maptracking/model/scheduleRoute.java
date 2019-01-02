package travel.maptracking.model;

import java.util.List;

public class scheduleRoute {

    public boolean isStatus() {
        return status;
    }

    public scheduleRoute setStatus(boolean status) {
        this.status = status;
        return this;
    }


    public List<Data> getData() {
        return data;
    }

    public scheduleRoute setData(List<Data> data) {
        this.data = data;
        return this;
    }
    private boolean status;
    private List<Data> data;
    private String message;

    public String getMessage() {
        return message;
    }

    public scheduleRoute setMessage(String message) {
        this.message = message;
        return this;
    }

    public class Data {
        private String id ="";
        private String id_schedule="";
        private String lat ="";
        private String longi ="";
        private String createdate ="";
        private String createby ="";




        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getId_schedule() {
            return id_schedule;
        }

        public void setId_schedule(String id_schedule) {
            this.id_schedule = id_schedule;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLongi() {
            return longi;
        }

        public void setLongi(String longi) {
            this.longi = longi;
        }

        public String getCreatedate() {
            return createdate;
        }

        public void setCreatedate(String createdate) {
            this.createdate = createdate;
        }

        public String getCreateby() {
            return createby;
        }

        public void setCreateby(String createby) {
            this.createby = createby;
        }



        public Data() {
        }

    }





    public scheduleRoute() {
    }



}
