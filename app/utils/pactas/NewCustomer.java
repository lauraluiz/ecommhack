package utils.pactas;

//POST /api/v1/contacts
//{
//    "CompanyInfo": {
//        "VatId": null,
//        "CompanyName": "Doeson Incorporated"
//    },
//    "UserInfo": {
//        "FirstName": "Mike",
//        "LastName": "Johnson",
//        "EmailAddress": "mjohnson@emphess.net"
//    },
//    "InvoiceAddress": {
//        "Street": "Spooner Street",
//        "HouseNumber": "78a",
//        "PostalCode": "45645",
//        "City": "Cologne",
//        "State": null,
//        "Country": "DE"
//    },
//    "PaymentChannelPaymill": {
//        "PaymentToken" : "tok_6148669b7dc1b48e3eed"
//        "Type" : "CreditCard"
//    }
//}

public class NewCustomer {
    public static class CompanyInfo {
        public String VatId = null;
        public String CompanyName = "Simpson Incorporated";
    }
    public CompanyInfo CompanyInfo = new CompanyInfo();

    public static class UserInfo {
        public String FirstName = "Homer";
        public String LastName = "Simpson";
        public String EmailAddress = "homer.simpson@springfield.com";
    }
    public UserInfo UserInfo = new UserInfo();

    public static class InvoiceAddress {
        public String Street = "Homer Street";
        public String HouseNumber = "1";
        public String PostalCode = "34567";
        public String City = "Springfield";
        public String State = "OK";
        public String Country = "USA";
    }
    public InvoiceAddress InvoiceAddress = new InvoiceAddress();

    public class PaymentChannelPaymill {
        public String PaymentToken;
        public String Type = "CreditCard";
        public PaymentChannelPaymill(String token) {
            PaymentToken = token;
        }
    }
    public PaymentChannelPaymill PaymentChannelPaymill;

    public NewCustomer(String paymillToken) {
        PaymentChannelPaymill = new PaymentChannelPaymill(paymillToken);
    }
}
