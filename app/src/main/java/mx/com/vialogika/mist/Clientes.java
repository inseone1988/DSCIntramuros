package mx.com.vialogika.mist;

import com.orm.SugarRecord;

public class Clientes extends SugarRecord<Clientes> {
    private String serverId;
    private String Social;
    private String Name;
    private String Alias;
    private String siteId;

    public Clientes(){

    }

    public Clientes(String sid,String social,String clientName,String clientAlias,String siteId){
        this.serverId = sid;
        this.Social = social;
        this.Name = clientName;
        this.Alias = clientAlias;
        this.siteId = siteId;
    }

    public String getSocial() {
        return Social;
    }

    public void setSocial(String social) {
        Social = social;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAlias() {
        return Alias;
    }

    public void setAlias(String alias) {
        Alias = alias;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }
}
