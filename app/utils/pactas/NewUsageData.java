package utils.pactas;

//POST /api/v1/Contracts/:contractId/Usage
//{
//        "Usage":
//        [
//                {
//                        "ProductNumber": "ProductId-VariantId",
//                        "Amount": 12.34,
//                        "Description": "Some Strings"
//                }
//        ]
//}

import java.util.Arrays;
import java.util.List;

public class NewUsageData {
    public List<Usage> Usage;
    public String ValidFrom = "2013-04-24";
    public NewUsageData(Usage singleUsage) {
        Usage = Arrays.asList(singleUsage);
    }

    public static class Usage {
        public String ProductNumber;
        public double Amount;
        public String Description = "Get them donuts!";
        public Usage(String productId, int variantId, Double price) {
            ProductNumber = productId + "-" + variantId;
            Amount = price;
        }
    }
}
