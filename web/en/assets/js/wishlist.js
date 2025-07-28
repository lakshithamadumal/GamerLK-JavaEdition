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

            wishlistProductContainer.appendChild(productCloneHtml);
        });

    } else {
        notyf.error({
            message: "Wishlist items loading failed..."
        });
    }


});