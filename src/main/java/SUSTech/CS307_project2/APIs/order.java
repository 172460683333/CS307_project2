package SUSTech.CS307_project2.APIs;

public interface order {

    void stockIn(String path);

    void placeOrder(String path);

    void updateOrder(String path);

    void deleteOrder(String path);
}
