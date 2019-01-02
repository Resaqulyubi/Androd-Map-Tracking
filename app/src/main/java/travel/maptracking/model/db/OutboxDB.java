package travel.maptracking.model.db;

import com.orm.SugarRecord;

/**
 * Created by KLOPOS on 1/26/2017.
 */

public class OutboxDB extends SugarRecord {
    String name;
    String url;
    String parameter;
    String method;
    String createddate;
    String updatedate;
    String status;
    String iduser;
    String idTableLocal = "";
    String nextidexecute = "";

    public static final String FLAG_METHOD_POST = "0";
    public static final String FLAG_METHOD_PUT = "1";
    public static final String FLAG_METHOD_DELETE = "2";

    public static final String FLAG_INIT = "0";
    public static final String FLAG_SENDING = "1";
    public static final String FLAG_RESEND = "2";
    public static final String FLAG_DONE = "3";
    public static final String FLAG_FAILED = "4";

    public OutboxDB() {

    }

    public OutboxDB(String name, String url, String parameter, String method, String createddate, String updatedate, String status, String iduser) {
        this.name = name;
        this.url = url;
        this.parameter = parameter;
        this.method = method;
        this.createddate = createddate;
        this.updatedate = updatedate;
        this.status = status;
        this.iduser = iduser;
    }

    public OutboxDB(String name, String url, String parameter, String method, String createddate, String updatedate, String status, String iduser, String idTableLocal) {
        this.name = name;
        this.url = url;
        this.parameter = parameter;
        this.method = method;
        this.createddate = createddate;
        this.updatedate = updatedate;
        this.status = status;
        this.iduser = iduser;
        this.idTableLocal = idTableLocal;
    }

    public String getNextidexecute() {
        return nextidexecute;
    }

    public OutboxDB setNextidexecute(String nextidexecute) {
        this.nextidexecute = nextidexecute;
        return this;
    }

    public String getIdTableLocal() {
        return idTableLocal;
    }

    public void setIdTableLocal(String idTableLocal) {
        this.idTableLocal = idTableLocal;
    }

    public String getCreateddate() {
        return createddate;
    }

    public String getParameter() {
        return parameter;
    }

    public String getStatus() {
        return status;
    }

    public String getUpdatedate() {
        return updatedate;
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    public String getIduser() {
        return iduser;
    }

    public void setCreateddate(String createddate) {
        this.createddate = createddate;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public void setUpdatedate(String updatedate) {
        this.updatedate = updatedate;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setIduser(String iduser) {
        this.iduser = iduser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "OutboxDB{" +
                "url='" + url + '\'' +
                ", parameter='" + parameter + '\'' +
                ", method='" + method + '\'' +
                ", createddate='" + createddate + '\'' +
                ", updatedate='" + updatedate + '\'' +
                ", status='" + status + '\'' +
                ", iduser='" + iduser + '\'' +
                '}';
    }
}
