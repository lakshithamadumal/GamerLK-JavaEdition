async function loadSingleOfferProductData() {
  const productId = new URLSearchParams(window.location.search).get("id");
  //console.log(productId);

  const response = await fetch("../../LoadSingleOfferProduct?id=" + productId);
  if (response.ok) {
    const json = await response.json();
    if (json.status) {
      document.getElementById("thumb-image").src =
        "../../assets/Games\\" + json.product.id + "\\thumb-image.jpg";

      //product deatails
      document.getElementById("product-title").innerHTML = json.product.title;

      // Format date without time
      const rawDate = json.product.release_date;
      const dateObj = new Date(rawDate);
      const options = { year: "numeric", month: "short", day: "numeric" };
      const formattedDate = dateObj.toLocaleDateString("en-US", options);

      document.getElementById("release-date").innerHTML = formattedDate;
      document.getElementById("product-description").innerHTML =
        json.product.description;

      // Format price with 2 decimal places and comma separator
      const formattedPrice = Number(json.product.price).toLocaleString(
        "en-US",
        {
          style: "currency",
          currency: "USD",
          minimumFractionDigits: 2,
        }
      );

      // Offer price calculation
      const offerPercent = Number(json.product.offer) || 0;
      const offerPrice =
        Number(json.product.price) -
        (Number(json.product.price) * offerPercent) / 100;

      // Format offer price
      const formattedOfferPrice = offerPrice.toLocaleString("en-US", {
        style: "currency",
        currency: "USD",
        minimumFractionDigits: 2,
      });

      document.getElementById("product-price").innerHTML =
        formattedPrice + ` USD`;
      document.getElementById("offer-percentage").innerHTML =
        json.product.offer + `% OFF`;
      document.getElementById("offer-price").innerHTML =
        formattedOfferPrice + ` USD`;

      document.getElementById("product-category").innerHTML =
        json.product.category_id.name;
      document.getElementById("product-mode").innerHTML =
        json.product.mode_id.name;
      document.getElementById("product-tag").innerHTML = json.product.tag;
      document.getElementById("product-developer").innerHTML =
        json.product.developer_id.name;
      document.getElementById("product-size").innerHTML =
        json.product.game_size + " GB";
      //Minimum Requirements
      document.getElementById("minOs").innerHTML =
        json.product.min_requirement_id.os + " (64-bit)";
      document.getElementById("minProcessor").innerHTML =
        json.product.min_requirement_id.processor;
      document.getElementById("minMemory").innerHTML =
        json.product.min_requirement_id.memory + " GB RAM";
      document.getElementById("minGraphics").innerHTML =
        json.product.min_requirement_id.graphics;
      document.getElementById("minStorage").innerHTML =
        json.product.min_requirement_id.storage;

      //Minimum Requirements
      document.getElementById("recOs").innerHTML =
        json.product.rec_requirement_id.os + " (64-bit)";
      document.getElementById("recProcessor").innerHTML =
        json.product.rec_requirement_id.processor;
      document.getElementById("recMemory").innerHTML =
        json.product.rec_requirement_id.memory + " GB RAM";
      document.getElementById("recGraphics").innerHTML =
        json.product.rec_requirement_id.graphics;
      document.getElementById("recStorage").innerHTML =
        json.product.rec_requirement_id.storage;

      // Load and show real rating
      loadProductRating(json.product.id);
      loadProductOrderCount(json.product.id);

      // Buy Now button click event එකෙන් විතරක් offerCheckout call කරන්න
      document.querySelector(".cart-btn").onclick = function() {
          offerCheckout(json.product.id);
      };
    } else {
      window.location = "../index.html";
    }
  } else {
    window.location = "../index.html";
  }
}

async function loadProductRating(productId) {
  const ratingBtn = document.getElementById("product-rating");
  try {
    const res = await fetch("../../ProductRating?id=" + productId);
    if (res.ok) {
      const json = await res.json();
      if (json.status) {
        ratingBtn.innerHTML = `${json.rating} <i class="fas fa-star"></i>`;
      } else {
        ratingBtn.innerHTML = `0 <i class="fas fa-star"></i>`;
      }
    }
  } catch (e) {
    ratingBtn.innerHTML = `0 <i class="fas fa-star"></i>`;
  }
}

async function loadProductOrderCount(productId) {
  const downloadBtn = document.getElementById("product-download");
  try {
    const res = await fetch("../../ProductOrderCount?id=" + productId);
    if (res.ok) {
      const json = await res.json();
      if (json.status) {
        downloadBtn.innerHTML = `${json.count} Downloads`;
      } else {
        downloadBtn.innerHTML = `0 Downloads`;
      }
    }
  } catch (e) {
    downloadBtn.innerHTML = `0 Downloads`;
  }
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//Payment function
var notyf = new Notyf({ position: { x: 'center', y: 'top' } });

// Payment completed. It can be a successful failure.
payhere.onCompleted = function onCompleted(orderId) {
    console.log("Payment completed. OrderID:" + orderId);
    notyf.success("Payment completed!");
    // Remove '#' if present at the start
    if (orderId.startsWith("#")) {
        orderId = orderId.substring(1);
    }
    setTimeout(function() {
        window.location.href = "../includes/order-invoice.html?orderId=" + orderId;
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

async function offerCheckout(productId) {
  const response = await fetch("../../LoadOfferummary", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: "productId=" + encodeURIComponent(productId)
  });

  if (response.ok) {
    const json = await response.json();
    if (json.status) {
      payhere.startPayment(json.payhereJson);
    } else {
      notyf.error(json.message || "Checkout failed...");
    }
  } else {
    notyf.error({ message: "Checkout failed..." });
  }
}
