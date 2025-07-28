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

            cartProductContainer.appendChild(productCloneHtml);
        });

    } else {
        notyf.error({
            message: "Cart items loading failed..."
        });
    }


});