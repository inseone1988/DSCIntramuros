package mx.com.vialogika.mist;

import com.orm.SugarRecord;

public class SiteIncidences extends SugarRecord {
    private String capture_date_time;
    private String event_date;
    private String event_time;
    private String event_name;
    private String event_risk_level;
    private String event_responsable;
    private String event_evidence;
    private String event_what;
    private String event_how;
    private String event_when;
    private String event_where;
    private String event_facts;
    private String event_files;
    private String signature_names;
    private String signatures;
    private String signature_roles;
    private long remoteId;
    private String event_user;
    private String event_user_site;

    public SiteIncidences(){

    }

    public SiteIncidences(String eventUser,String event_date, String event_time, String event_name, String event_risk_level, String event_responsable, String event_evidence, String event_what, String event_how, String event_when, String event_where, String event_facts, String event_files,String signature_names,String signatureRoles,String signatures) {
        this.event_user = eventUser;
        this.capture_date_time = Databases.sNow();
        this.event_date = event_date;
        this.event_time = event_time;
        this.event_name = event_name;
        this.event_risk_level = event_risk_level;
        this.event_responsable = event_responsable;
        this.event_evidence = event_evidence;
        this.event_what = event_what;
        this.event_how = event_how;
        this.event_when = event_when;
        this.event_where = event_where;
        this.event_facts = event_facts;
        this.event_files = event_files;
        this.signatures = signatures;
        this.signature_names = signature_names;
        this.signature_roles = signatureRoles;
    }

    public String getCapture_date_time() {
        return capture_date_time;
    }

    public void setCapture_date_time(String capture_date_time) {
        this.capture_date_time = capture_date_time;
    }

    public String getEvent_date() {
        return event_date;
    }

    public void setEvent_date(String event_date) {
        this.event_date = event_date;
    }

    public String getEvent_time() {
        return event_time;
    }

    public void setEvent_time(String event_time) {
        this.event_time = event_time;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getEvent_risk_level() {
        return event_risk_level;
    }

    public void setEvent_risk_level(String event_risk_level) {
        this.event_risk_level = event_risk_level;
    }

    public String getEvent_responsable() {
        return event_responsable;
    }

    public void setEvent_responsable(String event_responsable) {
        this.event_responsable = event_responsable;
    }

    public String getEvent_evidence() {
        return event_evidence;
    }

    public void setEvent_evidence(String event_evidence) {
        this.event_evidence = event_evidence;
    }

    public String getEvent_what() {
        return event_what;
    }

    public void setEvent_what(String event_what) {
        this.event_what = event_what;
    }

    public String getEvent_how() {
        return event_how;
    }

    public void setEvent_how(String event_how) {
        this.event_how = event_how;
    }

    public String getEvent_when() {
        return event_when;
    }

    public void setEvent_when(String event_when) {
        this.event_when = event_when;
    }

    public String getEvent_where() {
        return event_where;
    }

    public void setEvent_where(String event_where) {
        this.event_where = event_where;
    }

    public String getEvent_facts() {
        return event_facts;
    }

    public void setEvent_facts(String event_facts) {
        this.event_facts = event_facts;
    }

    public String getEvent_files() {
        return event_files;
    }

    public void setEvent_files(String event_files) {
        this.event_files = event_files;
    }

    public String getSignature_names() {
        return signature_names;
    }

    public void setSignature_names(String signature_names) {
        this.signature_names = signature_names;
    }

    public String getSignatures() {
        return signatures;
    }

    public void setSignatures(String signatures) {
        this.signatures = signatures;
    }

    public long getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(long remoteId) {
        this.remoteId = remoteId;
    }

    public String getEvent_user() {
        return event_user;
    }

    public void setEvent_user(String event_user) {
        this.event_user = event_user;
    }

    public String getEvent_user_site() {
        return event_user_site;
    }

    public void setEvent_user_site(String event_user_site) {
        this.event_user_site = event_user_site;
    }

    public String getSignature_roles() {
        return signature_roles;
    }

    public void setSignature_roles(String signature_roles) {
        this.signature_roles = signature_roles;
    }
}
