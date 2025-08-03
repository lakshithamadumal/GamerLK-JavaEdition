document.addEventListener("DOMContentLoaded", async function () {
    const notyf = new Notyf({
        position: {
            x: 'center',
            y: 'top'
        }
    });

    const response = await fetch("../../LoadWishlistItem");
    if (response.ok) {
        const json = await response.json();

        let wishlistProductContainer = document.getElementById("wishlistProductContainer");
        let wishlistProduct = document.getElementById("wishlistProduct");

        // Check for empty wishlist message
        if (json.message === "Your Wishlist is Empty") {
            document.querySelector(".wishlistSectionforUser").classList.add("d-none");
            // Show empty wishlist section
            document.querySelector(".emptyWishlistSection").classList.remove("d-none");
            return;
        }


        wishlistProductContainer.innerHTML = "";
        json.wishlistItems.forEach(wishlist => {
            let product = wishlist.product_id;
            let productCloneHtml = wishlistProduct.cloneNode(true);
            productCloneHtml.querySelector("#wishlistProduct-a1").href = "game-details.html?id=" + product.id;
            productCloneHtml.querySelector("#wishlistProduct-image").src = "../../assets/Games\\" + product.id + "\\thumb-image.jpg";
            productCloneHtml.querySelector("#wishlistProduct-title").innerHTML = product.title;
            productCloneHtml.querySelector("#wishlistProduct-price").innerHTML = "$" + new Intl.NumberFormat(
                "en-US",
                { minimumFractionDigits: 2 }
            ).format(product.price);

            // Add to Cart button event for products
            const searchCartBtn = productCloneHtml.querySelector("#add-to-cart-ad");
            searchCartBtn.addEventListener("click", (e) => {
                addToCart(product.id);
                e.preventDefault();
            });


            // remove to wishlist button event for products
            const searchWishlistBtn = productCloneHtml.querySelector("#remove-to-wishlist-ad");
            searchWishlistBtn.addEventListener("click", (e) => {
                removeFromWishlist(product.id);
                e.preventDefault();
            });

            wishlistProductContainer.appendChild(productCloneHtml);
        });

    } else {
        notyf.error({
            message: "Wishlist items loading failed..."
        });
    }


});


async function addToCart(productId) {

    const notyf = new Notyf({
        position: {
            x: 'center',
            y: 'top'
        }
    });

    const response = await fetch("../../AddToCart?prId=" + productId);
    if (response.ok) {
        const json = await response.json();
        if (json.status) {

            if (json.message === "Game Added to cart") {
                notyf.success("Game Added to cart");
            } else if (json.message === "Already Added") {
                notyf.error("Game Already in Cart");
            } else if (json.message === "Login Required!") {
                notyf.error("Login Required!");
            } else {
                notyf.error(json.message);
            }

        } else {
            notyf.error("Game Add to cart failed.");

        }

    }
}


async function removeFromWishlist(productId) {

    const notyf = new Notyf({
        position: {
            x: 'center',
            y: 'top'
        }
    });

    const response = await fetch("../../RemoveWishlist?prId=" + productId);
    if (response.ok) {
        const json = await response.json();
        if (json.status) {

            if (json.message === "Game Removed from Wishlist") {
                notyf.success("Game Removed from Wishlist");
                // Reload the Wishlist items after successful removal
                setTimeout(() => {
                    window.location.reload();
                }, 1000);

            } else if (json.message === "Already Removed from Wishlist") {
                notyf.error("Already Removed from Wishlist");
            } else {
                notyf.error(json.message);
            }

        } else {
            notyf.error("Game Remove from Wishlist failed.");

        }

    }
}
