package utils.pactas;

//POST api/v1/billingGroups/
//{
//        "BillingEpsilonDays": "0",
//        "PeriodBase": "2013-04-24"
//        "BillingInterval":
//        {
//                "Unit": "Month",
//                "Quantity": 1
//        },
//        "BillingTriggerMode": "AutomatedRecurring"
//}

public class NewBillingGroup {
    public String BillingEpsilonDays = "0";
    public String PeriodBase = "2013-04-24";

    public Period BillingInterval = new Period("Month", 1);

    public String BillingTriggerMode = "AutomatedRecurring";
}
