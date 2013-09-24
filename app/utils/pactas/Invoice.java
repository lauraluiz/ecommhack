package utils.pactas;

import com.neovisionaries.i18n.CountryCode;
import com.ning.http.client.Realm;
import io.sphere.client.shop.model.Address;
import io.sphere.client.shop.model.Attribute;
import io.sphere.client.shop.model.Variant;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import play.libs.F;
import play.libs.Json;
import play.libs.WS;
import sphere.Sphere;
import utils.Util;

import java.util.ArrayList;
import java.util.List;

public class Invoice {

    private static String API_URL = "https://sandbox.pactas.com/api/v1/";
    private static String AUTH_URL = "https://sandbox.pactas.com/oauth/token/";
    private static String CLIENT_ID = "523075921d8dd007f822edaa";
    private static String CLIENT_SECRET = "332324095c5c665bd4bc680eb98739e1";

    private String access_token;
    private JsonNode response;

    public void authenticate() {
        if (access_token != null) return;
        try {
            ObjectNode body = Json.newObject();
            body.put("grant_type", "client_credentials");
            // Send request
            F.Promise<WS.Response> promise = WS.url(AUTH_URL)
                    .setContentType("application/x-www-form-urlencoded")
                    .setAuth(CLIENT_ID, CLIENT_SECRET, Realm.AuthScheme.BASIC)
                    .post(body);

            // Read request
            JsonNode res = Json.parse(promise.get().getBody());
            access_token = res.get("access_token").getTextValue();
            play.Logger.debug("Received access token " + access_token);
        } catch (Exception e) {
            play.Logger.error("Error on authentication");
        }
    }


    public String get(String id) {
        authenticate();
        String url = API_URL + "/invoices/" + id;
        try {
            ObjectNode body = Json.newObject();
            body.put("access_token", access_token);
            // Send request
            F.Promise<WS.Response> promise = WS.url(url)
                    .setContentType("application/x-www-form-urlencoded")
                    .setQueryParameter("access_token", access_token)
                    .get();

            // Read request
            response = Json.parse(promise.get().getBody());
        } catch (Exception e) {
            play.Logger.error("Error on requesting invoice #" + id);
        }
        return "ok";
    }

    public Variant getVariant() {
        Variant variant = null;
        List<JsonNode> nodes = response.get("ItemList").findValues("ProductId");
        if (!nodes.isEmpty()) {
            variant = Util.getVariant(nodes.get(0).getTextValue());
        }
        return variant;
    }

    public Address getAddress() {
        Address address = null;
        if (response.has("RecipientAddress")) {
            JsonNode node = response.get("RecipientAddress");
            address = new Address(CountryCode.valueOf(node.get("Country").getTextValue()));
            address.setStreetName(node.get("AddressLine1").getTextValue());
            address.setStreetNumber(node.get("AddressLine2").getTextValue());
            address.setPostalCode(node.get("PostalCode").getTextValue());
            address.setCity(node.get("City").getTextValue());
        }
        return address;
    }
}
