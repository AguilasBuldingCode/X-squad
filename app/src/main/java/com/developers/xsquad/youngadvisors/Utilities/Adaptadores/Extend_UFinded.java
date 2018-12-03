package com.developers.xsquad.youngadvisors.Utilities.Adaptadores;

import com.developers.xsquad.youngadvisors.Utilities.UsersFinded;

public class Extend_UFinded extends UsersFinded {
    private String Id;

    public Extend_UFinded(String Id, String Nombre, String Apellido){
        super(Nombre, Apellido);
        this.Id = Id;
    }

    public Extend_UFinded (String Id, UsersFinded ob){
        super(ob.getNombre(), ob.getApellido());
        this.Id = Id;
    }

    public Extend_UFinded(){

    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
}
