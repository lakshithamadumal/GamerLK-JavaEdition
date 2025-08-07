var notyf = new Notyf({ position: { x: "center", y: "top" } });

// Payment completed. It can be a successful failure.
payhere.onCompleted = function onCompleted(orderId) {
  console.log("Payment completed. OrderID:" + orderId);
  notyf.success("Payment completed!");
  // Remove '#' if present at the start
  if (orderId.startsWith("#")) {
    orderId = orderId.substring(1);
  }
  setTimeout(function () {
    window.location.href = "../pages/my-downloads.html";
  }, 1000);
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
    method: "POST",
  });

  if (response.ok) {
    const json = await response.json();
    if (json.status) {
      payhere.startPayment(json.payhereJson);
    } else {
      if (json.message && json.message.startsWith("Already purchased")) {
        notyf.error(json.message);
        setTimeout(function () {
          window.location.href = "../pages/my-downloads.html";
        }, 1500);
      } else {
        notyf.error(json.message || "Checkout failed...");
      }
    }
  } else {
    notyf.error({
      message: "Checkout failed...",
    });
  }
}
