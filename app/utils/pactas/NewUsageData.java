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
    public NewUsageData(Usage singleUsage) {
        Usage = Arrays.asList(singleUsage);
    }

    public static class Usage {
        public String ProductNumber;
        public double Amount;
        public String Description = "Homer's donuts!";
        public Usage(String productId, String variantId, Double price) {
            ProductNumber = productId + "-" + variantId;
            Amount = price;
        }
    }
}
