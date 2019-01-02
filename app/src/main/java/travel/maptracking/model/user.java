package travel.maptracking.model;

import java.util.List;

public class user {
    private boolean status;
    private List<Data> data;
    private String message;
    public boolean isStatus() {
        return status;
    }

    public user setStatus(boolean status) {
        this.status = status;
        return this;
    }


    public List<Data> getData() {
        return data;
    }

    public Data getDataItem() {
        return new Data();
    }


    public user setData(List<Data> data) {
        this.data = data;
        return this;
    }


    public String getMessage() {
        return message;
    }

    public user setMessage(String message) {
        this.message = message;
        return this;
    }


    public class Data {
        private String car="";
        private String email="";
        private String hak_akses="";
        private String id="";
        private String nama="";
        private String password="";
        private String phone="";
        private String police_number="";
        private String username="";


        public String getCar() {
            return car;
        }

        public void setCar(String car) {
            this.car = car;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getHak_akses() {
            return hak_akses;
        }

        public void setHak_akses(String hak_akses) {
            this.hak_akses = hak_akses;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getNama() {
            return nama;
        }

        public void setNama(String nama) {
            this.nama = nama;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getPolice_number() {
            return police_number;
        }

        public void setPolice_number(String police_number) {
            this.police_number = police_number;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public Data() {
        }

    }





    public user() {
    }



}
