package sendtempchallanstoCS;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ETL
 */
public class TempChallanDTO 
{
    private int transferID;
    private String productCode;
    private String barcode;
    private String fshopID;
    private String tshopID;
    private String clnNo;
    private String subcatId;
    private String broadcatId;

    public String getBroadcatId() {
        return broadcatId;
    }

    public void setBroadcatId(String broadcatId) {
        this.broadcatId = broadcatId;
    }
    
    public int getTransferID() {
        return transferID;
    }

    public void setTransferID(int transferID) {
        this.transferID = transferID;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getFshopID() {
        return fshopID;
    }

    public void setFshopID(String fshopID) {
        this.fshopID = fshopID;
    }

    public String getTshopID() {
        return tshopID;
    }

    public void setTshopID(String tshopID) {
        this.tshopID = tshopID;
    }
    
    public String getClnNo() {
        return clnNo;
    }

    public void setClnNo(String clnNo) {
        this.clnNo = clnNo;
    }
    
    public String getSubcatId() {
        return subcatId;
    }

    public void setSubcatId(String subcatId) {
        this.subcatId = subcatId;
    }
    
   
}