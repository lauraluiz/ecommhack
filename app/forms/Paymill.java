package forms;

import play.data.validation.Constraints;

public class Paymill {

    @Constraints.Required
    public String paymillToken;

    public Paymill() {
    }

}
