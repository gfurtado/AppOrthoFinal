package paulocauca.apportho;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by guilh on 02/12/2017.
 */
@IgnoreExtraProperties
public class Clinica {
    public String name;
    public String uid;

    public Clinica(){  };

    public Clinica(String name){
        this.name = name;
    };

    public String getUID() {
        return uid;
    }

    public void setUID(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
