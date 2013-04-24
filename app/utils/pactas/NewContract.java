package utils.pactas;

//POST /contracts/
//{
//        // This is hardcoded. We have to create the tariff during a setup step and then use
//        "TariffId": "5176a7719746c964ecc7b73d",
//        // The id of the billing group we just created:
//        "BillingGroupId": "5177a915675ec911e8d91ed7",
//        StartDate: "2013-04-24",
//        // The if of the contact we just created
//        "ContactId": "6eac972d1b3f481287c0559942dbe361",
//        "AutoExtendPeriod":
//        {
//                "Unit": "Week",
//                "Quantity": 2
//        },
//        "InitialPeriod":
//        {
//                "Unit": "Month",
//                "Quantity": 3
//        },
//        "InitialCancellationPeriod":
//        {
//                "Unit": "Month",
//                "Quantity": 1
//        },
//        "RegularCancellationPeriod":
//        {
//                "Unit": "Week",
//                "Quantity": 2
//        },
//        "PaymentPeriodMode": "PrePaid",
//}

public class NewContract {
    public String TariffId = "5176a7719746c964ecc7b73d";
    public String PaymentPeriodMode = "PrePaid";
    public String BillingGroupId;
    public String ContactId;
    public NewContract(String billingGroupId, String customerId) {
        BillingGroupId = billingGroupId;
        ContactId = customerId;
    }
    public Period AutoExtendPeriod = new Period("Month", 1);
    public Period InitialPeriod = new Period("Month", 1);
    public Period InitialCancellationPeriod = new Period("Month", 1);
    public Period RegularCancellationPeriod = new Period("Month", 1);
}
