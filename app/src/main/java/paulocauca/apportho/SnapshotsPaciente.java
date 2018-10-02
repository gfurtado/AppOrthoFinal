package paulocauca.apportho;

import java.util.Date;

public class SnapshotsPaciente {
    public Date data;
    public Paciente paciente;
    public String imgUrl;

    public SnapshotsPaciente(){  };

    public SnapshotsPaciente(Date data,Paciente paciente,String imgUrl ){
        this.data = data;
        this.paciente = paciente;
        this.imgUrl = imgUrl;

    };

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
