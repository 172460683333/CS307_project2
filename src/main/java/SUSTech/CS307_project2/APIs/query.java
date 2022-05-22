package SUSTech.CS307_project2.APIs;

public interface query {
    public String getAllStaffCount();           //6
    public String getContractCount();           //7
    public String getOrderCount();              //8
    public String getNeverSoldProductCount();   //9
    public String getFavoriteProductModel();    //10
    public String getAvgStockByCenter();        //11
    public String getProductByNumber(String product_number);         //12
    public String getContractInfo(String contract_number);            //13
}
