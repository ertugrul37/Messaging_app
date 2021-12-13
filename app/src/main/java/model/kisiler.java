package model;

public class kisiler {
    String ad,durum,resim;

    public kisiler()
    {


    }
    public kisiler(String ad, String durum, String resim) {
        this.ad = ad;
        this.durum = durum;
        this.resim = resim;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getDurum() {
        return durum;
    }

    public void setDurum(String durum) {
        this.durum = durum;
    }

    public String getResim() {
        return resim;
    }

    public void setResim(String resim) {
        this.resim = resim;
    }
}
