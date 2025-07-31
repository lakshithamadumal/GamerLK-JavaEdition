document.addEventListener("DOMContentLoaded", async function () {
    const notyf = new Notyf({
        position: {
            x: 'center',
            y: 'top'
        }
    });
    const response = await fetch("../../LoadStoreGmames");

    if (response.ok) {
        const json = await response.json();

        let storeGamesContainer = document.getElementById("game-list-container");
        let storeGames = document.getElementById("game-card");

        storeGamesContainer.innerHTML = "";

        // Group by 4
        for (let i = 0; i < json.productList.length; i += 4) {
            // Create new game-list div for each group of 4
            let groupDiv = document.createElement("div");
            groupDiv.className = "game-list";

            // For each product in this group
            for (let j = i; j < i + 4 && j < json.productList.length; j++) {
                let gamelist = json.productList[j];
                let productCloneHtml = storeGames.cloneNode(true);

                // Set data
                productCloneHtml.querySelector("a").href = "game-details.html?id=" + gamelist.id;
                productCloneHtml.querySelector("img").src = "../../assets/Games\\" + gamelist.id + "\\thumb-image.jpg";
                productCloneHtml.querySelector(".game-title").innerHTML = gamelist.title;
                productCloneHtml.querySelector(".game-price-row span").innerHTML = "$" + new Intl.NumberFormat(
                    "en-US",
                    { minimumFractionDigits: 2 }
                ).format(gamelist.price);

                // Add to Cart button event for products
                const searchCartBtn = productCloneHtml.querySelector("#add-to-cart-ad");
                searchCartBtn.addEventListener("click", (e) => {
                    addToCart(gamelist.id); // <-- FIXED
                    e.preventDefault();
                });

                // Add to wishlist button event for products
                const searchWishlistBtn = productCloneHtml.querySelector("#add-to-wishlist-ad");
                searchWishlistBtn.addEventListener("click", (e) => {
                    addToWishlist(gamelist.id); // <-- FIXED
                    e.preventDefault();
                });

                // wishlist design ad
                const searchWishlistBtnDesign = productCloneHtml.querySelector("#add-to-wishlist-ad");
                searchWishlistBtnDesign.addEventListener("click", function () {
                    this.querySelector('i').classList.toggle('fas');
                    this.querySelector('i').classList.toggle('far');
                });

                groupDiv.appendChild(productCloneHtml);
            }

            storeGamesContainer.appendChild(groupDiv);
        }

    } else {
        notyf.error({
            message: "Games loading failed..."
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