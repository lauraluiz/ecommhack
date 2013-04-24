$ ->
    marginTop = 1
    checkoutCart = $('#checkout-cart.step')
    checkoutShipping = $('#checkout-shipping.step')
    checkoutBilling = $('#checkout-billing.step')

    checkoutCart.find('label.radio').hover( ->
        $(this).addClass('active')
    , ->
        $(this).removeClass('active')
    )

    checkoutCart.find('label.radio').click( ->
        checkoutCart.find('label.radio').removeClass('selected')
        $(this).addClass('selected')

        # Update price
        checkoutBilling.find('input.amount').val($(this).data("price"))
    )

    # Validate form and mark incorrect fields as invalid
    validateForm = (form, allRequired) ->
        all = form.find(':input')
        required = all.not(':disabled')
        required = required.filter('[required]') if not allRequired

        # Start with all fields marked as valid
        all.attr("aria-invalid", "false")

        # Mark incorrect fields as invalid
        invalid = required.filter -> return not $(this).val()
        return true unless invalid.length > 0
        invalid.attr("aria-invalid", "true")
        return false

    # Get form data
    getFormData = (form) ->
        data = {}
        form.find(':input').not(':disabled').each ->
            data[$(this).attr("name")] = $(this).val()
        return data

    # Fill form summary with form data
    fillSummary = (form, summaryList) ->
        form.find(':input').not(':disabled').each ->
            value = if $(this).is('select') then $(this).find(':selected').text() else $(this).val()
            place = summaryList.find('[data-form=' + $(this).attr("name") + ']')
            if place.length < 1
                place = summaryList.find('[data-form=' + $(this).attr("class") + ']')

            if place.length > 0
                # If there is a list element for this value, set here the data
                place.text(value)
            else
                # Otherwise append a new list element
                summaryList.append("<li>" + value + "</li>")

    # Jump to the next section form
    nextStep = (focused) ->
        next = $('#checkout .step.disabled').filter(':first')

        # Set focused section as visited
        focused.removeClass("disabled current").addClass("visited")

        if next.length > 0
            # Set first disabled section as current
            next.removeClass("visited disabled").addClass("current")

            # Set scroll position to next section
            $('html, body').animate scrollTop: next.offset().top - marginTop, 'slow'
        else
            # Set submit button visible
            footer = $('#checkout-footer').not(':visible')
            footer.fadeIn()
            $('html, body').animate scrollTop: footer.offset().top - marginTop, 'slow'

    # Bind 'change' button click event to allow editing a section form
    $('#checkout .btn-edit').click( ->
        selected = $(this).parentsUntil('.step').parent()
        focused = selected.siblings('.step:not(.disabled):not(.visited)')

        # Set focused section as disabled unless it is also current section
        if focused.hasClass("current")
            focused.addClass("disabled")
        else
            focused.addClass("visited")

        # Set selected section as current
        selected.removeClass("visited disabled")

        # Set scroll position to selected section
        $('html, body').animate scrollTop: selected.offset().top - marginTop, 'slow'
    )

    # Bind cart 'next step' click event to 'next step' functionality
    checkoutCart.find('.btn-next').click( ->
        form = $('#form-checkout-cart')
        place = $('#checkout-cart-summary')

        # Validate form client side
        return unless validateForm(form, false)

        place.find('span[data-form=quantity]').text(form.find('input[name=quantity]').val())
        place.find('span[data-form=size]').text(form.find('label.selected .variant-size').text())
        place.find('span[data-form=price]').text(form.find('label.selected .variant-price').text())

        # Go to next section
        nextStep(checkoutCart)
    )

    # Bind shipping 'next step' click event to 'submit form' and 'next step' functionality
    checkoutShipping.find('.btn-next').click( ->
        form = $('#form-shipping-address')

        # Validate form client side
        return unless validateForm(form, false)

        # Fill form summary data
        fillSummary(form, $('#shipping-address-summary'))

        # Go to next section
        nextStep(checkoutShipping)
    )

    # Bind billing 'next step' click event to 'validate form' and 'next step' functionality
    checkoutBilling.find('.btn-next').click( ->
        form = $('#form-billing-method')

        # Validate form client side
        return unless validateForm(form, true)

        # Fill form summary data
        fillSummary(form, $('#billing-method-summary'))

        # Go to next section
        nextStep(checkoutBilling)
    )
