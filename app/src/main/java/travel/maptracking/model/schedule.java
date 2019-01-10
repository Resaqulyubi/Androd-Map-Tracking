package travel.maptracking.model;

import java.util.List;

public class schedule {

    public boolean isStatus() {
        return status;
    }

    public schedule setStatus(boolean status) {
        this.status = status;
        return this;
    }


    public List<Data> getData() {
        return data;
    }

    public schedule setData(List<Data> data) {
        this.data = data;
        return this;
    }
    private boolean status;
    private List<Data> data;
    private String message;

    public String getMessage() {
        return message;
    }

    public schedule setMessage(String message) {
        this.message = message;
        return this;
    }

    public static class Data {
        private String id ="";
        private String id_driver ="";
        private String id_customer ="";
        private String customer_name ="";
        private String email="";
        private String phone="";
        private String address ="";
        private String payment_code="";
        private String paket="";
        private String pickup_point="";
        private String pickup_time="";
        private String destination="";
        private String route="";
        private String arrival="";
        private String createby="";
        private String createdate="";
        private String status="";



        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getId_driver() {
            return id_driver;
        }

        public void setId_driver(String id_driver) {
            this.id_driver = id_driver;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getId_customer() {
            return id_customer;
        }

        public void setId_customer(String id_customer) {
            this.id_customer = id_customer;
        }

        public String getCustomer_name() {
            return customer_name;
        }

        public void setCustomer_name(String customer_name) {
            this.customer_name = customer_name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPayment_code() {
            return payment_code;
        }

        public void setPayment_code(String payment_code) {
            this.payment_code = payment_code;
        }

        public String getPaket() {
            return paket;
        }

        public void setPaket(String paket) {
            this.paket = paket;
        }

        public String getPickup_point() {
            return pickup_point;
        }

        public void setPickup_point(String pickup_point) {
            this.pickup_point = pickup_point;
        }

        public String getPickup_time() {
            return pickup_time;
        }

        public void setPickup_time(String pickup_time) {
            this.pickup_time = pickup_time;
        }

        public String getDestination() {
            return destination;
        }

        public void setDestination(String destination) {
            this.destination = destination;
        }

        public String getRoute() {
            return route;
        }

        public void setRoute(String route) {
            this.route = route;
        }

        public String getArrival() {
            return arrival;
        }

        public void setArrival(String arrival) {
            this.arrival = arrival;
        }

        public String getCreateby() {
            return createby;
        }

        public void setCreateby(String createby) {
            this.createby = createby;
        }

        public String getCreatedate() {
            return createdate;
        }

        public void setCreatedate(String createdate) {
            this.createdate = createdate;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }


        public Data() {
        }

    }





    public schedule() {
    }



}
