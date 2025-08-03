document.addEventListener("DOMContentLoaded", async function () {
    const notyf = new Notyf({
        position: {
            x: 'center',
            y: 'top'
        }
    });

    const response = await fetch("../../LoadCartItem");
    if (response.ok) {
        const json = await response.json();

        let cartProductContainer = document.getElementById("cartProductContainer");
        let cartProduct = document.getElementById("cartProduct");

        // Check for empty cart message
        if (json.message === "Your Cart is Empty") {
            // Hide cart and checkout sections
            document.querySelector(".cartSectionforUser").classList.add("d-none");
            document.querySelector(".checkoutSectionforUser").classList.add("d-none");
            // Show empty cart section
            document.querySelector(".emptyCartSection").classList.remove("d-none");
            return;
        }

        cartProductContainer.innerHTML = "";
        let subtotal = 0;
        let productCount = json.cartItems.length;

        json.cartItems.forEach(cartlist => {
            let product = cartlist.product_id;
            let productCloneHtml = cartProduct.cloneNode(true);
            productCloneHtml.querySelector("#cartProduct-a1").href = "game-details.html?id=" + product.id;
            productCloneHtml.querySelector("#cartProduct-image").src = "../../assets/Games\\" + product.id + "\\thumb-image.jpg";
            productCloneHtml.querySelector("#cartProduct-title").innerHTML = product.title;
            productCloneHtml.querySelector("#cartProduct-price").innerHTML = "$" + new Intl.NumberFormat(
                "en-US",
                { minimumFractionDigits: 2 }
            ).format(product.price);

            subtotal += parseFloat(product.price);

            // remove to Cart button event for products
            const searchCartBtn = productCloneHtml.querySelector("#remove-to-cart-ad");
            searchCartBtn.addEventListener("click", (e) => {
                removeFromCart(product.id);
                e.preventDefault();
            });

            // Add to wishlist button event for products
            const searchWishlistBtn = productCloneHtml.querySelector("#add-to-wishlist-ad");
            searchWishlistBtn.addEventListener("click", (e) => {
                addToWishlist(product.id);
                e.preventDefault();
            });

            // wishlist design ad
            const searchWishlistBtnDesign = productCloneHtml.querySelector("#add-to-wishlist-ad");
            searchWishlistBtnDesign.addEventListener("click", function () {
                this.querySelector('i').classList.toggle('fas');
                this.querySelector('i').classList.toggle('far');
            });

            cartProductContainer.appendChild(productCloneHtml);
        });

        // Set product count
        document.getElementById("Orderitems").textContent = productCount + " items";
        // Set subtotal
        document.getElementById("OrderSubtotal").textContent = "$" + subtotal.toFixed(2);

        // Discount default 0
        let discount = 0;
        document.getElementById("OrderDiscount").textContent = "-$" + discount.toFixed(2);

        // Tax 5% of subtotal
        let tax = subtotal * 0.0;
        document.getElementById("Ordertax").textContent = "$" + tax.toFixed(2);

        // Total calculation
        let total = subtotal - discount + tax;
        document.getElementById("OrderTotal").textContent = "$" + total.toFixed(2);


    } else {
        notyf.error({
            message: "Cart items loading failed..."
        });
    }


});



async function addToWishlist(productId) {

    const notyf = new Notyf({
        position: {
            x: 'center',
            y: 'top'
        }
    });

    const response = await fetch("../../AddToWishlist?prId=" + productId);
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            notyf.success(json.message);
        } else if (json.message === "Already Added") {
            notyf.error("Already Added");
        } else {
            notyf.error(json.message);

        }
    } else {
        notyf.error("Game Add to wishlist failed.");

    }

}

async function removeFromCart(productId) {

    const notyf = new Notyf({
        position: {
            x: 'center',
            y: 'top'
        }
    });

    const response = await fetch("../../RemoveCart?prId=" + productId);
    if (response.ok) {
        const json = await response.json();
        if (json.status) {

            if (json.message === "Game Removed from cart") {
                notyf.success("Game Removed from cart");
                // Reload the cart items after successful removal
                setTimeout(() => {
                    window.location.reload();
                }, 1000);

            } else if (json.message === "Already Removed from cart") {
                notyf.error("Already Removed from cart");
            } else {
                notyf.error(json.message);
            }

        } else {
            notyf.error("Game Remove to cart failed.");

        }

    }
}
