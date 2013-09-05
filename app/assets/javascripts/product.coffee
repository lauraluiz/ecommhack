$ ->
    # Scroll to frequency when product is selected
    $('input[name=variantId]').click( ->
        $('html, body').animate scrollTop: $('#product-often').offset().top, 'slow'
    )
