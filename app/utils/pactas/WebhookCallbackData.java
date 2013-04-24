package utils.pactas;

// This is what we receive from Pactas in the webhook.
//{
//   "Amount":49.91,
//   "CustomerId":"7e04519f130c4c539c2f775b37fccd75",
//   "Items":[
//      {
//         "Description":"Red Donut\r\nTest",
//         "PricePerUnit":6.99,
//         "ProductNumber":"ProductId-VariantId",
//         "Quantity":6,
//         "VatPercentage":19
//      }
//   ],
//   "PaymentSuccess":true,
//   "PaymentTokenId":"tok_754b196d5aed1f45591a",
//   "YourCustomerId":""
//}

import java.util.List;

public class WebhookCallbackData {
    public double Amount;
    public String CustomerId;
    public boolean PaymentSuccess;
    public String PaymentTokenId;
    //public String YourCustomerId; // we are not creating a customer in Sphere.io
    public List<LineItem> Items;

    public static class LineItem {
        public String Description;
        public Double PricePerUnit;
        public String ProductNumber;
        public int Quantity;
        public int VatPercentage;
        public String productId() {
            return ProductNumber.split("-")[0];
        }
        public String variantId() {
            return ProductNumber.split("-")[1];
        }
    }
}
