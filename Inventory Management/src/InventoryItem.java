
public class InventoryItem {
	//required variables
	private String productID;
	private String productName;
	private int quantity;
	private double price;
	private String category;
	
	//Constructor
	public InventoryItem(String productID, String productName, int quantity, double price, String category ) {
		this.productID = productID;
		this.productName = productName;
		this.quantity = quantity;
		this.price = price;
		this.category = category;
	}
	//getter methods
	public String getProductID()
	{
		return productID;
	}
	public String getProductName()
	{
		return productName;
	}
	public int getQuantity()
	{
		return quantity;
	}
	public double getPrice()
	{
		return price;
	}
	public String getCategory()
	{
		return category;
	}
	
	//setter methods
	public void setProductID(String productID)
	{
		this.productID = productID;
	}
	public void setProductName(String productName)
	{
		this.productName = productName;
	}
	public void setQuantity(int quantity)
	{
		this.quantity = quantity;
	}
	public void setPrice(double price)
	{
		this.price = price;
	}
	public void setCategory(String category)
	{
		this.category = category;
	}
	
	//toString
	@Override
	public String toString() {
		return "ProductID: " + productID + ", Name: " + productName + ", Quantity: " + quantity + ", Price: ₹" + price + ", Category: " + category;  
	}
}
