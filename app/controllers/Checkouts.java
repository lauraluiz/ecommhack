package controllers;

import forms.*;
import io.sphere.client.shop.model.PaymentState;
import io.sphere.client.shop.model.Product;
import play.data.Form;
import play.mvc.Result;
import sphere.ShopController;

import static play.data.Form.form;

public class Checkouts extends ShopController {

    public static Result show() {
        Product product = sphere().products.bySlug("pink-donuts-box").fetch().orNull();
        Form<SetAddress> addressForm = form(SetAddress.class);
        return ok(views.html.ecommhack.render(product, addressForm));
    }

    public static Result paymill() {
        // Get product information
        Form<AddToCart> cartForm = form(AddToCart.class).bindFromRequest();
        if (cartForm.hasErrors()) {
            return badRequest();
        }
        AddToCart addToCart = cartForm.get();

        // Get shipping information
        Form<SetAddress> shippingForm = form(SetAddress.class).bindFromRequest();
        if (shippingForm.hasErrors()) {
            return badRequest();
        }
        SetAddress setAddress = shippingForm.get();

        // Get billing information
        Form<Paymill> billingForm = form(Paymill.class).bindFromRequest();
        if (billingForm.hasErrors()) {
            return badRequest("Some error during payment");
        }
        Paymill paymill = billingForm.get();
        System.out.println("Token: " + paymill.paymillToken);
        return ok();
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
