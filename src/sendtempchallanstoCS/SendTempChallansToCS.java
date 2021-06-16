/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sendtempchallanstoCS;

import databasemanager.DatabaseManager;
import static java.lang.System.exit;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;


/**
 *
 * @author Shahrin
 */
public class SendTempChallansToCS {

    /**
     * @param args the command line arguments
     */
    static final Logger logger = Logger.getLogger(SendTempChallansToCS.class.getName());
    
    //private DBConnection dbConn;
    private Connection conn;
    private ResultSet rs = null;
    private Statement stmt;
    TempChallanDTO tcDto = null;
    HashMap<Integer,String> stylewiseTrnsferIdMap = new HashMap<Integer,String>();
    
    private void sendTempChallan()
    {
        HashMap<Integer,TempChallanDTO> tempChallanMap;
        String sql,tshopId;
        ArrayList<String> shopList=new ArrayList<String>();
        //HashMap<String,String> prodSubcatMap=new HashMap<String,String>();
        HashMap<String,String> prodBroadcatMap=new HashMap<String,String>();
        
        try
        {
            /*
            dbConn = new DBConnection();
            dbConn.localDatabase();
            */
            conn = DatabaseManager.getInstance().getConnection();
            
            
            sql="SELECT shop_id FROM shops WHERE ( shop_type IN('general_shop','discount_shop') OR shop_id='LRCSOnline' OR shop_id='CS2') "
                    + " AND is_physical_shop='yes' AND notification_enable='yes' AND delete_status=0";
            
            
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while(rs.next())
            {
              shopList.add(rs.getString("shop_id"));
            }
                    
            shopList.add("CS");
            
            for(int index=0;index<shopList.size();index++)
            {
             
                tshopId=shopList.get(index);
                //tshopId ="LR08";
                tempChallanMap = new HashMap<Integer,TempChallanDTO>();            
                     
                sql="SELECT * FROM temp_challans WHERE clnNo IN ('') AND trnsStatus=0 AND deleteStatus=0 AND is_paused='no' and tshopID='"+tshopId+"' and (fshopID='CS' OR fshopID='CS2' OR fshopID='CS3')"
                        //+ "order by tshopID,transferID,trnsDT";
                        +" order by product_code";
                logger.debug(sql);

                stmt = conn.createStatement();
                rs = stmt.executeQuery(sql);

                //int qty =0;
                String product_code="";
                int count_unique_styles=0;
                int max_style_count =15;
                ArrayList<Integer> transferIdList = new ArrayList<Integer>();
                
                while (rs.next()) 
                {
                    if(product_code=="" || product_code.equals(rs.getString("product_code"))==false)
                    {
                        count_unique_styles++;
                    }    
                    
                    tcDto = new TempChallanDTO();
                    tcDto.setTransferID(rs.getInt("transferID"));
                    tcDto.setProductCode(rs.getString("product_code"));
                    tcDto.setBarcode(rs.getString("barcode"));
                    tcDto.setFshopID(rs.getString("fshopID"));
                    tcDto.setTshopID(rs.getString("tshopID"));
                          

                    tempChallanMap.put(tcDto.getTransferID(), tcDto);
                    stylewiseTrnsferIdMap.put(tcDto.getTransferID(),tcDto.getProductCode());                    
                    
                    /*
                    if(count_unique_styles>max_style_count)
                    {
                        transferIdList = getTransferIds(stylewiseTrnsferIdMap.get(stylewiseTrnsferIdMap.size()-1));
                        
                        for(int j=0;j<transferIdList.size();j++)
                        {
                            tempChallanMap.remove(transferIdList.get(j));
                        }
                        
                        break;
                    }
                    */
                    
                    product_code= rs.getString("product_code");
                }
                prodBroadcatMap = getProdSubcategories(stylewiseTrnsferIdMap);
       
        
                Iterator<Integer> iterator = tempChallanMap.keySet().iterator();
                HashMap<String,String> shopwiseTempChallanNoMap = new HashMap<String,String>();
                String broadCat="",subcat="",fShop="",cln_no="",combo="";
                int transferId;
       

                while(iterator.hasNext())
                {
                    transferId = iterator.next();
                    tcDto = tempChallanMap.get(transferId);
                   // tcDto.setSubcatId(prodSubcatMap.get(tcDto.getProductCode()));
                    tcDto.setBroadcatId(prodBroadcatMap.get(tcDto.getProductCode()));

                    if(tcDto!=null)
                    {                
                        fShop = tcDto.getFshopID();
                        //subcat = tcDto.getSubcatId();
                        broadCat =tcDto.getBroadcatId();

                        //if(fShop.equals("CS"))
                        {
                                try{
                                    sql="select sum(init_qty) as total_production,sum(available_qty) as total_balance from inventories where product_code='"+tcDto.getProductCode()+"' having total_production=total_balance";
                                    //logger.debug(sql);
                                    stmt = conn.createStatement();
                                    rs = stmt.executeQuery(sql);
                                    if(rs.next())
                                    {
                                       // combo="NEW-CS"+"_"+subcat;
                                        combo="NEW-"+fShop+"_"+broadCat;
                                        logger.debug("new : "+combo);
                                        
                                        if(shopwiseTempChallanNoMap.containsKey(combo))
                                                cln_no = shopwiseTempChallanNoMap.get(combo);
                                        else
                                        {
                                                cln_no =fShop+"-"+String.valueOf(tcDto.getTransferID());
                                            shopwiseTempChallanNoMap.put(combo, cln_no); 
                                        }

                                    }
                                    else
                                    {
                                        //combo="OLD-CS"+"_"+subcat;
                                        combo="OLD-"+fShop+"_"+broadCat;
                                        logger.debug("old : "+combo);
                                        
                                        if(shopwiseTempChallanNoMap.containsKey(combo))
                                                cln_no = shopwiseTempChallanNoMap.get(combo); 
                                        else
                                        {
                                                cln_no =fShop+"-"+String.valueOf(tcDto.getTransferID());
                                            shopwiseTempChallanNoMap.put(combo, cln_no);  
                                        }
                                    }
                                }
                                catch(Exception e)
                                {
                                        logger.fatal(e);
                                }
                        }
                        /*
                        else{

                            if(shopwiseTempChallanNoMap.containsKey(fShop))
                            {
                                cln_no = shopwiseTempChallanNoMap.get(fShop); 
                            }
                            else
                            {
                                cln_no =fShop+"-"+String.valueOf(tcDto.getTransferID());
                                shopwiseTempChallanNoMap.put(fShop, cln_no);
                            }
                        }
                                */
                       try
                        {
                            sql="Update temp_challans set clnNo='"+cln_no+"' where transferID ="+transferId+" and clnNo IN ('')";
                            logger.debug(sql);

                            stmt = conn.createStatement();
                            stmt.execute(sql);          

                        }
                        catch (Exception e)
                        {                   
                            logger.error(e);
                        }

                    }       

                }
            }
        }
        catch (Exception e)
        {            
            logger.error(e);
        }
        finally 
        {
            try{               
                if(conn!=null)
                    conn.close();
                if(stmt!=null)
                    stmt.close();
            }
            catch (Exception e)
            {
                logger.error(e);
            }
            
            exit(0);
        }

        
    }
    public HashMap<String,String> getProdSubcategories(HashMap<Integer,String> stylewiseTrnsferIdMap)
    {
        HashMap<String,String> prodSubcatMap = new HashMap<String,String>();
        HashMap<String,String> subcatBroadcatMap = new HashMap<String,String>();
        HashMap<String,String> prodBroadcatMap = new HashMap<String,String>();
        ArrayList<String> prodList = new ArrayList<String>();
        String sql="",prod_code_str="",subcat_str="",sql1="";
     
        try
            {
                for(Map.Entry<Integer,String> map : stylewiseTrnsferIdMap.entrySet()){
                        prodList.add(map.getValue());
                }
                
                if(prodList.size()>0)
                {                    
                    prod_code_str = arrayListToString(prodList);
                    sql="SELECT product_code,subcat_id FROM inventories WHERE product_code IN ( "+prod_code_str+" )";
                    logger.debug(sql);

                    stmt = conn.createStatement();
                    rs = stmt.executeQuery(sql);
                    
                    while(rs.next())
                    {
                        prodSubcatMap.put(rs.getString("product_code"), rs.getString("subcat_id"));
                        
                        if(subcat_str.equals(""))
                            subcat_str = rs.getString("subcat_id");
                        else
                            subcat_str = subcat_str+","+rs.getString("subcat_id");
                    }
                    
                    if(subcat_str.equals("")==false)
                    {
                        sql1="SELECT id,category_id FROM subcategories WHERE id IN ("+subcat_str+")";                        
                        logger.debug(sql1);

                        stmt = conn.createStatement();
                        rs = stmt.executeQuery(sql1);
                        
                         while(rs.next())
                         {
                            subcatBroadcatMap.put(rs.getString("id"), rs.getString("category_id"));
                         }
                         
                        Iterator<String> iterator = prodSubcatMap.keySet().iterator();
                        String keyProdCode="";
                        
                        while(iterator.hasNext())
                        {
                            keyProdCode = iterator.next();
                        
                            prodBroadcatMap.put(keyProdCode, subcatBroadcatMap.get(prodSubcatMap.get(keyProdCode)));
                        }
                    }
                        
                }

            }
            catch (Exception e)
            {                   
                logger.error(e);
            }
        
        return prodBroadcatMap;
    }
    private String arrayListToString(ArrayList data) 
    {
        StringBuffer result = new StringBuffer();
        
        if (data != null)
        {
            int size = data.size();

            if(size > 0)
                 result.append("'" + data.get(0) + "'");

            for (int i = 1; i < size; i++) 
            {
                 result.append(",");
                 result.append("'" + data.get(i) + "'");
            }
        }
        
        return result.toString();
    } 
    
    public ArrayList<Integer> getTransferIds(String style_no)
    {
        ArrayList<Integer> transferIdList = new ArrayList<Integer>();
        Iterator<Integer> iterator1 = stylewiseTrnsferIdMap.keySet().iterator();
        int key;
        String val;
        
        while(iterator1.hasNext())
        {
            key = iterator1.next();
            val = stylewiseTrnsferIdMap.get(key);
            
            if(val.equals(style_no))
            {
                transferIdList.add(key);
            }
        }
        
        return transferIdList;        
    }
     
    public static void main(String[] args) {
        // TODO code application logic here        
        try
        {
            SendTempChallansToCS sCtoPOS = new SendTempChallansToCS();
            sCtoPOS.sendTempChallan();
        }
        catch(Exception e)
        {
            logger.error(e);
        }
    }
}
