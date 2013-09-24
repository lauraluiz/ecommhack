package utils;

import io.sphere.client.shop.model.Cart;
import io.sphere.client.shop.model.LineItem;
import io.sphere.client.shop.model.Product;
import io.sphere.client.shop.model.Variant;
import sphere.Sphere;

public class Util {

    public static Product getProduct() {
        return Sphere.getInstance().products().bySlug("donut-box").fetch().orNull();
    }

    public static Variant getVariant(String pactasId) {
        for (Variant variant : getProduct().getVariants().asList()) {
            if (variant.getString("pactas1").equals(pactasId) ||
                variant.getString("pactas2").equals(pactasId) ||
                variant.getString("pactas4").equals(pactasId)) {
                return variant;
            }
        }
        return null;
    }

    public static void clearCart() {
        Cart cart = Sphere.getInstance().currentCart().fetch();
        for (LineItem item : cart.getLineItems()) {
            cart = Sphere.getInstance().currentCart().removeLineItem(item.getId());
        }
        Sphere.getInstance().customObjects().delete("cart-frequency", cart.getId()).execute();
        System.out.println("ID " + cart.getIdAndVersion().getVersion());
    }

    public static boolean isValidCartSnapshot(String cartSnapshot) {
        try {
            Sphere.getInstance().currentCart().isSafeToCreateOrder(cartSnapshot);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
