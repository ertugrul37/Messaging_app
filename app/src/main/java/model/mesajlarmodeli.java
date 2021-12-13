package model;

public class mesajlarmodeli {
    private String kimden,mesaj,tur;

    public mesajlarmodeli() {
    }

    public mesajlarmodeli(String kimden, String mesaj, String tur) {
        this.kimden = kimden;
        this.mesaj = mesaj;
        this.tur = tur;
    }

    public String getKimden() {
        return kimden;
    }

    public void setKimden(String kimden) {
        this.kimden = kimden;
    }

    public String getMesaj() {
        return mesaj;
    }

    public void setMesaj(String mesaj) {
        this.mesaj = mesaj;
    }

    public String getTur() {
        return tur;
    }

    public void setTur(String tur) {
        this.tur = tur;
    }
}
