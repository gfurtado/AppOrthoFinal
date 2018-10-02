package paulocauca.apportho;

public class Photos {
    public String dataconsulta;
    public String paciente_id;
    public String photo_original;
    public String timestamp;

    public Photos(){  };

    public Photos(String dataconsulta, String paciente_id , String photo_original, String timestamp){
        this.dataconsulta = dataconsulta;
        this.paciente_id = paciente_id;
        this.photo_original = photo_original;
        this.timestamp = timestamp;
    };

    public String getDataconsulta() {
        return dataconsulta;
    }

    public void setDataconsulta(String dataconsulta) {
        this.dataconsulta = dataconsulta;
    }

    public String getPaciente_id() {
        return paciente_id;
    }

    public void setPaciente_id(String paciente_id) {
        this.paciente_id = paciente_id;
    }

    public String getPhoto_original() {
        return photo_original;
    }

    public void setPhoto_original(String photo_original) {
        this.photo_original = photo_original;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
