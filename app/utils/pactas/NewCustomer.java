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

import io.sphere.client.shop.model.Address;

public class NewCustomer {
    public static class CompanyInfo {
        public String VatId = null;
        public String CompanyName = "Simpson Incorporated";
    }
    public CompanyInfo CompanyInfo = new CompanyInfo();

    public static class UserInfo {
        public String FirstName;
        public String LastName;
        public String EmailAddress;
        public UserInfo(Address address) {
            FirstName = address.getFirstName();
            LastName = address.getLastName();
            EmailAddress = address.getEmail();
        }
    }
    public UserInfo UserInfo;

    public static class InvoiceAddress {
        public String Street;
        public String HouseNumber;
        public String PostalCode;
        public String City;
        public String State;
        public String Country;
        public InvoiceAddress(Address address) {
            Street = address.getFirstName();
            HouseNumber = address.getStreetNumber();
            PostalCode = address.getPostalCode();
            City = address.getCity();
            State = address.getState();
            Country = address.getCountry().toString();
        }
    }
    public InvoiceAddress InvoiceAddress;

    public class PaymentChannelPaymill {
        public String PaymentToken;
        public String Type = "CreditCard";
        public PaymentChannelPaymill(String token) {
            PaymentToken = token;
        }
    }
    public PaymentChannelPaymill PaymentChannelPaymill;

    public NewCustomer(String paymillToken, Address address) {
        PaymentChannelPaymill = new PaymentChannelPaymill(paymillToken);
        UserInfo = new UserInfo(address);
        InvoiceAddress = new InvoiceAddress(address);
    }
}
