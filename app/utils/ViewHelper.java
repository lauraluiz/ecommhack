package utils;

import java.math.BigDecimal;
import java.util.*;

import com.neovisionaries.i18n.CountryCode;
import io.sphere.client.model.Money;
import io.sphere.client.shop.model.*;
import org.apache.commons.lang3.text.WordUtils;
import play.mvc.Http;
import sphere.Sphere;

import static java.util.Currency.*;

public class ViewHelper {

	/**
	 * Returns the current Cart in session.
	 */
	public static Cart getCurrentCart() {
		return Sphere.getInstance().currentCart().fetch();
	}

    public static int getOften(Cart cart) {
        if (cart.getLineItems().size() < 1) {
            return 0;
        }
        return cart.getLineItems().get(0).getQuantity();
    }

    public static int getVariant(Cart cart) {
        if (cart.getLineItems().size() < 1) {
            return 0;
        }
        return cart.getLineItems().get(0).getVariant().getId();
    }

    public static Customer getCurrentCustomer() {
        Customer customer = null;
        if (Sphere.getInstance().isLoggedIn()) {
            customer = Sphere.getInstance().currentCustomer().fetch();
        }
        return customer;
    }

    public static boolean isLoggedIn() {
        return Sphere.getInstance().isLoggedIn();
    }

	/**
	 * Returns the list of root categories
	 */
	public static List<Category> getRootCategories() {
        return Sphere.getInstance().categories().getRoots();
	}

    public static String getReturnUrl() {
        return Http.Context.current().session().get("returnUrl");
    }

    public static String capitalizeInitials(String text) {
        return WordUtils.capitalizeFully(text);
    }

    public static String getCountryName(String code) {
        try {
            return CountryCode.getByCode(code).getName();
        } catch (Exception e) {
            return "";
        }
    }

    public static String getPrice(Money money) {
        return money.getAmount().toString() + " " + getInstance(money.getCurrencyCode()).getSymbol(Locale.GERMANY);
    }

    public static BigDecimal getPercentage(double amount) {
        return BigDecimal.valueOf(amount * 100).stripTrailingZeros();
    }

	/**
	 * Compares the categories and returns the 'active' class if are the same.
	 * 
	 * @param category
     * @param currentCategory
	 * @return 'active' if categories are the same, otherwise an empty string.
	 */
	public static String getActiveClass(Category category, Category currentCategory) {
        String active = "";
        if (currentCategory != null && currentCategory.getPathInTree().contains(category)) {
            active = "active";
        }
		return active;
	}

    public static boolean isCustomerAddressSet(Cart cart) {
        return cart.getShippingAddress() != null;
    }

	/**
	 * Check whether the given product has more than one attribute value
	 * 
	 * @param product
     * @param attributeName
	 * @return true if the product has more than one attribute value, false otherwise
	 */
	public static boolean hasMoreAttributeValues(Product product, String attributeName) {
        return product.getVariants().getAvailableAttributes(attributeName).size() > 1;
    }

    /**
     * Check whether the given Product has more than one 'color' attribute
     *
     * @param product
     * @return true if the product has more than one color, false otherwise
     */
    public static boolean hasMoreColors(Product product) {
        return hasMoreAttributeValues(product, "color");
    }

    /**
     * Check whether the given Product has more than one 'size' attribute
     *
     * @param product
     * @return true if the product has more than one size, false otherwise
     */
    public static boolean hasMoreSizes(Product product) {
        return hasMoreAttributeValues(product, "size");
    }

    public static Money getShippingCost() {
        // TODO Implement correct shipping cost
        return new Money(BigDecimal.valueOf(10), "EUR");
    }

    public static List<String> getPossibleSizes(Product product, Variant variant) {
        List<Variant> variants = getPossibleVariants(product, variant, "size");
        List<String> sizes = new ArrayList<String>();
        for (Variant matchedVariant : variants) {
            sizes.add(matchedVariant.getString("size"));
        }
        return sizes;
    }

    public static List<Variant> getPossibleVariants(Product product, Variant variant, String selectedAttribute) {
        List<Variant> matchingVariantList = new ArrayList<Variant>();
        List<Attribute> desiredAttributes = new ArrayList<Attribute>();
        for (Attribute attribute : variant.getAttributes()) {
            if (!selectedAttribute.equals(attribute.getName()) && hasMoreAttributeValues(product, attribute.getName())) {
                desiredAttributes.add(attribute);
            }
        }
        VariantList variantList = product.getVariants().byAttributes(desiredAttributes);
        for (Attribute attr : product.getVariants().getAvailableAttributes(selectedAttribute)) {
            if (variantList.byAttributes(attr).size() < 1) {
                matchingVariantList.add((product.getVariants().byAttributes(attr).first()).orNull());
            } else {
                matchingVariantList.add((variantList.byAttributes(attr).first()).orNull());
            }
        }
        matchingVariantList.removeAll(Collections.singleton(null));
        return matchingVariantList;
    }
}
