package it.tiw.beans;

import java.sql.Date;

public class Appello {
    private int idAppello;
    private int idCorso;
    private Date dataAppello;

    public int getIdAppello() { return idAppello; }
    public void setIdAppello(int idAppello) { this.idAppello = idAppello; }

    public int getIdCorso() { return idCorso; }
    public void setIdCorso(int idCorso) { this.idCorso = idCorso; }

    public Date getDataAppello() { return dataAppello; }
    public void setDataAppello(Date dataAppello) { this.dataAppello = dataAppello; }
}

