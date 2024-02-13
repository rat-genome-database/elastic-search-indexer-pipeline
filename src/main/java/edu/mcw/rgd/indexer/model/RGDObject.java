package edu.mcw.rgd.indexer.model;

import edu.mcw.rgd.dao.impl.RGDManagementDAO;
import edu.mcw.rgd.datamodel.*;

public class RGDObject<T> {
    T obj;
    int rgdId;
    int key;
    String objectStatus;
    RGDManagementDAO managementDAO=new RGDManagementDAO();
     RGDObject(T obj) throws Exception {
        this.obj=obj;
        setRgdId();
        setObjectStatus();
    }
     void setRgdId(){
        if(this.obj instanceof Gene){
            this.rgdId=((Gene) this.obj).getRgdId();
            this.key=((Gene) this.obj).getKey();
        }
        if(this.obj instanceof QTL){
            this.rgdId=((QTL) this.obj).getRgdId();
            this.key=((QTL) this.obj).getKey();

        }
        if(this.obj instanceof SSLP){
            this.rgdId=((SSLP) this.obj).getRgdId();
            this.key=((SSLP) this.obj).getKey();

        }
        if(this.obj instanceof Strain){
            this.rgdId=((Strain) this.obj).getRgdId();
            this.key=((Strain) this.obj).getKey();

        }
        if(this.obj instanceof Variant){
            this.rgdId=((Variant) this.obj).getRgdId();

        }

    }


    public String getObjectStatus() {
        return objectStatus;
    }

     void setObjectStatus() throws Exception {
        RgdId id = managementDAO.getRgdId(rgdId);
        this.objectStatus=id.getObjectStatus();
    }


}
