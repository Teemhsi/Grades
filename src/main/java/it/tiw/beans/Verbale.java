package it.tiw.beans;

import java.sql.Date;
import java.sql.Timestamp;

public class Verbale {
    private int idVerbale;
    private String codiceVerbale;
    private int idAppello;
    private Timestamp dataCreazione; ;

    public int getIdVerbale() { return idVerbale; }
    public void setIdVerbale(int idVerbale) { this.idVerbale = idVerbale; }

    public int getIdAppello() { return idAppello; }
    public void setIdAppello(int idAppello) { this.idAppello = idAppello; }

    public Timestamp getDataCreazione() { return dataCreazione; }
    public void setDataCreazione(Timestamp dataCreazione) { this.dataCreazione = dataCreazione; }

    public String getCodiceVerbale() {
        return codiceVerbale;
    }

    public void setCodiceVerbale(String codiceVerbale) {
        this.codiceVerbale = codiceVerbale;
    }
}

