package controllers;

import com.neovisionaries.i18n.CountryCode;
import forms.*;
import io.sphere.client.shop.model.*;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import play.data.Form;
import play.mvc.Result;
import sphere.ShopController;
import utils.pactas.Id;
import utils.pactas.PactasClient;
import utils.pactas.WebhookCallbackData;

import java.io.IOException;
import java.util.Currency;

import static play.data.Form.form;

public class Application extends ShopController {

    private static final PactasClient pactas = new PactasClient();

    public static Result show() {
        Product product = sphere().products().bySlug("pink-donuts-box").fetch().orNull();
        Form<SetAddress> addressForm = form(SetAddress.class);
        return ok(views.html.index.render(product, addressForm));
    }

    public static Result submit() {
        // Get product information
        Form<AddToCart> cartForm = form(AddToCart.class).bindFromRequest();
        if (cartForm.hasErrors()) {
            return badRequest();
        }
        AddToCart addToCart = cartForm.get();
        Product product = sphere().products().byId(addToCart.productId).fetch().orNull();
        if (product == null) {
            return badRequest("Missing product");
        }
        Variant variant = product.getVariants().byId(addToCart.variantId).orNull();
        if (variant == null) {
            return badRequest("Missing variant");
        }
        int unit = variant.getInt("unit") * addToCart.quantity;

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
        try {
            // TODO Fix Pactas
            Id customerId = pactas.createCustomer(paymill.paymillToken, setAddress.getAddress());
            Id billingId = pactas.createBillingGroup();
            Id contractId = pactas.createContract(billingId.Id, customerId.Id);
            pactas.createUsageData(contractId.Id, addToCart.productId, addToCart.variantId, addToCart.quantity);
            pactas.lockContract(contractId.Id);
            flash("purchase-header", "Lucky you!");
            flash("purchase-body", "Every month you will be receiving " + unit);
        } catch (Exception e) {
            flash("purchase-header", "Ohhh we're sorry...!");
            flash("purchase-body", "We don't offer our services anymore...");
            flash("purchase-body2", " But imagine what would it be to receive " + unit);
        }

        return ok(views.html.success.render(unit));
    }

    private static final ObjectMapper jsonMapper = new ObjectMapper().configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static Result pactas() {
        // Pactas needs to send Content-Type: application/json for this to work.
        String payload = request().body().asJson().toString();
        play.Logger.debug("------ Pactas webhook: " + payload);

        // Read order data from Pactas
        WebhookCallbackData callbackData;
        try {
            callbackData = jsonMapper.readValue(payload, new TypeReference<WebhookCallbackData>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (callbackData.Items.isEmpty()) {
            return badRequest("No line items in callback data");
        }
        WebhookCallbackData.LineItem lineItemToOrder = callbackData.Items.get(0);
        // TODO Read address and other information from Pactas
        Address address = new Address(CountryCode.DE);

        // Create cart with Pactas data
        Cart cart = sphere().client().carts().createCart(Currency.getInstance("EUR"), CountryCode.DE, Cart.InventoryMode.None).execute();
        CartUpdate cartUpdate = new CartUpdate()
                .setShippingAddress(address)
                .addLineItem(1, lineItemToOrder.productId(), lineItemToOrder.variantId());
        cart = sphere().client().carts().updateCart(cart.getIdAndVersion(), cartUpdate).execute();

        // Create order from cart
        sphere().client().orders().createOrder(cart.getIdAndVersion(), PaymentState.Paid).execute();

        play.Logger.debug("------ Order created!");
        return ok("Order created!");
    }
}
