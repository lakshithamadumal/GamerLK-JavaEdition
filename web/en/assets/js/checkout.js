var notyf = new Notyf({ position: { x: 'left', y: 'top' } });


// Payment completed. It can be a successful failure.
payhere.onCompleted = function onCompleted(orderId) {
    console.log("Payment completed. OrderID:" + orderId);
    notyf.success("Payment completed successfully!");
    // Note: validate the payment and show success or failure page to the customer
};

// Payment window closed
payhere.onDismissed = function onDismissed() {
    // Note: Prompt user to pay again or show an error page
    console.log("Payment dismissed");
    notyf.error("Payment window closed without completing the payment.");
};

// Error occurred
payhere.onError = function onError(error) {
    // Note: show an error page
    console.log("Error:" + error);
    notyf.error("An error occurred during payment: " + error);
};



async function Checkout() {

    const response = await fetch("../../LoadCartSummary", {
        method: "POST"
    });

    if (response.ok) {
        const json = await response.json(); // <-- You need this line!
        if (json.status) {

            const form = document.createElement("form");
            form.method = "POST";
            form.action = "https://sandbox.payhere.lk/pay/checkout"; // ✅ Not checkoutJ

            const payhereData = json.payhereJson;

            for (const key in payhereData) {
                if (payhereData.hasOwnProperty(key)) {
                    const input = document.createElement("input");
                    input.type = "hidden";
                    input.name = key;
                    input.value = payhereData[key];
                    form.appendChild(input);
                }
            }

            document.body.appendChild(form);
            form.submit();

        } else {
            notyf.error(json.message || "Checkout failed...");

        }

    } else {
        notyf.error({
            message: "Checkout failed..."
        });
    }
}
