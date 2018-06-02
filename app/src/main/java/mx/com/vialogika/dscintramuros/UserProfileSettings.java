package mx.com.vialogika.dscintramuros;

import com.orm.SugarRecord;

public class UserProfileSettings extends SugarRecord {

    private Long userId;
    private Long siteId;
    private Long userProviderid;
    private String userName;
    private String userFname;
    private String userLname;
    private String corpName;


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Long getUserProviderid() {
        return userProviderid;
    }

    public void setUserProviderid(Long userProviderid) {
        this.userProviderid = userProviderid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserFname() {
        return userFname;
    }

    public void setUserFname(String userFname) {
        this.userFname = userFname;
    }

    public String getUserLname() {
        return userLname;
    }

    public void setUserLname(String userLname) {
        this.userLname = userLname;
    }

    public String getCorpName() {
        return corpName;
    }

    public void setCorpName(String corpName) {
        this.corpName = corpName;
    }
}
