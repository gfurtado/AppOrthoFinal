package paulocauca.apportho;

import com.google.firebase.database.Exclude;

public class Paciente {
    public String name;
    public String clinica_id;
    public String uid;
    @Exclude
    private String key;



    public Paciente(){  };

    public Paciente(String name, String clinica){
        this.name = name;
        this.clinica_id = clinica;
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClinica() {
        return clinica_id;
    }

    public void setClinica(String clinica) {
        this.clinica_id = clinica;
    }

    public String getUID() {
        return uid;
    }

    public void setUID(String uid) {
        this.uid = uid;
    }
}
