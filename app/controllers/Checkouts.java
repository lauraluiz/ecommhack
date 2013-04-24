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


    public static Result submitShippingAddress() {
        Form<SetAddress> form = form(SetAddress.class).bindFromRequest();
        if (form.hasErrors()) {
            return badRequest();
        }
        SetAddress setAddress = form.get();
        sphere().currentCart().setShippingAddress(setAddress.getAddress());
        sphere().currentCart().setCountry(setAddress.getCountryCode());
        return ok();
    }

    public static Result addToCart() {
        Form<AddToCart> form = form(AddToCart.class).bindFromRequest();
        if (form.hasErrors()) {
            return badRequest();
        }
        AddToCart addToCart = form.get();
        return ok();
    }

    public static Result paymill() {
        Form<Paymill> form = form(Paymill.class).bindFromRequest();
        if (form.hasErrors()) {
            return badRequest("Some error during payment");
        }
        Paymill paymill = form.get();
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
