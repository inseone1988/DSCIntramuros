package mx.com.vialogika.mist;

public class GruposView {

    private int gpoNo;
    private String siteName;
    private String ProviderName;
    private String plantillaTotal;
    private String plantillaCount;
    private String grupo;
    private String saved;
    private boolean isEditable;

    public GruposView(String saved,int gpoNo,String siteName,String providerName,String plantillaTotal,String plantillaCount,String gpo){
        this.saved = saved;
        this.gpoNo = gpoNo;
        this.siteName = siteName;
        this.ProviderName = providerName;
        this.plantillaTotal = plantillaTotal;
        this.plantillaCount = plantillaCount;
        this.grupo = gpo;
    }

    public int getGpoNo() {
        return gpoNo;
    }

    public void setGpoNo(int gpoNo) {
        this.gpoNo = gpoNo;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getProviderName() {
        return ProviderName;
    }

    public void setProviderName(String providerName) {
        ProviderName = providerName;
    }

    public String getPlantillaTotal() {
        return plantillaTotal;
    }

    public void setPlantillaTotal(String plantillaTotal) {
        this.plantillaTotal = plantillaTotal;
    }

    public String getPlantillaCount() {
        return plantillaCount;
    }

    public void setPlantillaCount(String plantillaCount) {
        this.plantillaCount = plantillaCount;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getSaved() {
        return saved;
    }

    public void setSaved(String saved) {
        this.saved = saved;
    }
}
