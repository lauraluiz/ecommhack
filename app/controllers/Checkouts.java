package controllers;

import forms.*;
import io.sphere.client.shop.model.PaymentState;
import io.sphere.client.shop.model.Product;
import io.sphere.client.shop.model.Variant;
import play.data.Form;
import play.mvc.Result;
import sphere.ShopController;
import utils.pactas.Id;
import utils.pactas.PactasClient;

import static play.data.Form.form;

public class Checkouts extends ShopController {

    private static final PactasClient pactas = new PactasClient();

    public static Result show() {
        Product product = sphere().products.bySlug("pink-donuts-box").fetch().orNull();
        Form<SetAddress> addressForm = form(SetAddress.class);
        return ok(views.html.ecommhack.render(product, addressForm));
    }

    public static Result submit() {
        // Get product information
        Form<AddToCart> cartForm = form(AddToCart.class).bindFromRequest();
        if (cartForm.hasErrors()) {
            return badRequest();
        }
        AddToCart addToCart = cartForm.get();
        System.out.println(":" + addToCart.productId);
        System.out.println(":" + addToCart.variantId);
        System.out.println(":" + addToCart.quantity);
        Product product = sphere().products.byId(addToCart.productId).fetch().orNull();
        if (product == null) {
            return badRequest("Missing product");
        }
        Variant variant = product.getVariants().byId(addToCart.variantId).orNull();
        if (variant == null) {
            return badRequest("Missing variant");
        }
        int unit = variant.getInt("unit");

        // Get shipping information
        Form<SetAddress> shippingForm = form(SetAddress.class).bindFromRequest();
        if (shippingForm.hasErrors()) {
            return badRequest();
        }
        SetAddress setAddress = shippingForm.get();
        System.out.println(":" + setAddress.company);
        System.out.println(":" + setAddress.firstName);
        System.out.println(":" + setAddress.lastName);
        System.out.println(":" + setAddress.street);
        System.out.println(":" + setAddress.street2);
        System.out.println(":" + setAddress.postalCode);
        System.out.println(":" + setAddress.city);
        System.out.println(":" + setAddress.country);
        System.out.println(":" + setAddress.phone);
        System.out.println(":" + setAddress.mobile);
        System.out.println(":" + setAddress.email);

        // Get billing information
        Form<Paymill> billingForm = form(Paymill.class).bindFromRequest();
        if (billingForm.hasErrors()) {
            return badRequest("Some error during payment");
        }
        Paymill paymill = billingForm.get();
        System.out.println("Token: " + paymill.paymillToken);

        Id customerId = pactas.createCustomer(paymill.paymillToken);
        Id billingId = pactas.createBillingGroup();
        Id contractId = pactas.createContract(billingId.Id, customerId.Id);
        pactas.createUsageData(contractId.Id, addToCart.productId, addToCart.variantId, addToCart.quantity);
        return ok(views.html.success.render(unit));
    }

    public static Result pactas() {
        Form<Pactas> form = form(Pactas.class).bindFromRequest();
        if (form.hasErrors()) {
            return badRequest("Some error during payment");
        }
        Pactas pactas = form.get();
        sphere().currentCart().addLineItem(pactas.productId, pactas.variantId, pactas.quantity);
        String checkoutId = sphere().currentCart().createCheckoutSummaryId();
        sphere().currentCart().createOrder(checkoutId, PaymentState.Paid);
        System.out.println("Order created!");

        return ok();
    }

}
