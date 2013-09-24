package controllers;

import com.neovisionaries.i18n.CountryCode;
import forms.*;
import io.sphere.client.model.CustomObject;
import io.sphere.client.shop.model.*;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import play.data.Form;
import play.mvc.Result;
import sphere.ShopController;
import sphere.Sphere;
import utils.Util;
import utils.pactas.Id;
import utils.pactas.Invoice;
import utils.pactas.PactasClient;
import utils.pactas.WebhookCallbackData;
import views.html.index;
import views.html.order;
import views.html.success;

import java.io.IOException;
import java.util.Currency;

import static play.data.Form.form;

public class Application extends ShopController {

    private static final PactasClient pactas = new PactasClient();
    private static final ObjectMapper jsonMapper = new ObjectMapper()
            .configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    final static Form<AddToCart> addToCartForm = form(AddToCart.class);


    public static Result showProduct() {
        Cart cart = sphere().currentCart().fetch();
        return ok(index.render(cart, Util.getProduct()));
    }

    public static Result submitProduct() {
        // Case missing or invalid product form
        Form<AddToCart> cartForm = addToCartForm.bindFromRequest();
        if (cartForm.hasErrors()) {
            flash("error", "Please select a box and how often you want it.");
            return showProduct();
        }
        // Case invalid product
        AddToCart addToCart = cartForm.get();
        Variant variant = Util.getProduct().getVariants().byId(addToCart.variantId).orNull();
        if (variant == null) {
            flash("error", "Product not found. Please try again.");
            return showProduct();
        }
        /* Clean cart because we only allow a single product */
        Cart cart = sphere().currentCart().fetch();
        for (LineItem item : cart.getLineItems()) {
            sphere().currentCart().removeLineItem(item.getId());
        }
        cart = sphere().currentCart().addLineItem(Util.getProduct().getId(), variant.getId(), 1);
        /* Store frequency in a custom object related to current cart */
        sphere().customObjects().set("cart-frequency", cart.getId(), addToCart.howOften);
        return redirect(routes.Application.showOrder());
    }

    public static Result showOrder() {
        Cart cart = sphere().currentCart().fetch();
        // Case no product selected
        if (cart.getLineItems().size() < 1) {
            return showProduct();
        }
        // Case missing frequency
        CustomObject frequency = sphere().customObjects().get("cart-frequency", cart.getId()).fetch().orNull();
        if (frequency == null) {
            flash("error", "Missing frequency of delivery. Please try selecting it again.");
            return showProduct();
        }
        // Case product in cart
        LineItem item = cart.getLineItems().get(0);
        return ok(order.render(cart, item, frequency.getValue().asInt()));
    }

    public static Result clearOrder() {
        Util.clearCart();
        return redirect(routes.Application.showProduct());
    }

    public static Result success() {
        String text = "Order is done. Please keep in mind that this shop is for demonstration only." +
                "Therefore we don't ship donuts in reality. Don't worry, no payments will be charged." +
                "If we ship donuts someday in the future you'll be the first that will be informed.";
        return ok(success.render(true, text));
    }

    public static Result failure() {
        String text = "Something went wrong. Please try again later.";
        return ok(success.render(false, text));
    }

    /* Method called by Pactas every time an order must be placed (weekly, monthly...) */
    public static Result executeSubscription() {
        // Clear previous cart
        Util.clearCart();

        // Read order data from Pactas
        if (request().body().asJson() != null) {
            String payload = request().body().asJson().toString();
            System.out.println("------ Pactas webhook: " + payload);
            //play.Logger.debug("------ Pactas webhook: " + payload);
        }

        Invoice invoice = new Invoice();
        invoice.get("524071211d8dd00e489eb1e6");

        // Set cart with subscription data
        sphere().currentCart().addLineItem(Util.getProduct().getId(), invoice.getVariant().getId(), 1);
        sphere().currentCart().setShippingAddress(invoice.getAddress());

        // Create order
        String cartSnapshot = sphere().currentCart().createCartSnapshotId();
        while (!Util.isValidCartSnapshot(cartSnapshot)) { }
        sphere().currentCart().createOrder(cartSnapshot, PaymentState.Paid);

        play.Logger.debug("------ Order created!");
        return ok("Order created!");
    }
}
