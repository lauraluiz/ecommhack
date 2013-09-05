$ ->
    orderForm = new Form $('#order-form')
    shippingData = new Form $('#order-shipping')
    billingData = new Form $('#order-billing')
    paymill = new Paymill billingData, $('#form-checkout')

    # Toggle payment form
    billingData.inputs.filter('.paymenttype').click ->
        $(this).addClass('btn-primary disabled')
        if $(this).val() is 'ELV'
            $('#payment-form-elv').show()
            $('#payment-form-cc').hide()
            $('#btn-paymenttype-cc').removeClass('btn-primary disabled')
        else
            $('#payment-form-elv').hide()
            $('#payment-form-cc').show()
            $('#btn-paymenttype-elv').removeClass('btn-primary disabled')


    orderForm.form.submit ->
        # Submit if Paymill already sent token
        return true if orderForm.allowSubmit

        # Validate form client side
        return false unless paymill.validate()

        # Submit payment data to Paymill
        paymill.submit( (error, result) ->
            return billingData.displayErrorMessage(error.apierror) if error

            # Append token to checkout form
            orderForm.form.append "<input type='hidden' name='paymillToken' value='#{result.token}'/>"
            orderForm.allowSubmit = true
            orderForm.form.submit()
        )

        return false