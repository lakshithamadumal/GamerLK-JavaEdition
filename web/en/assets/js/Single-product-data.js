async function loadSingleProductData() {

    const productId = new URLSearchParams(window.location.search).get('id');
    //console.log(productId);

    const response = await fetch("../../LoadSingleProduct?id=" + productId);
    if (response.ok) {
        const json = await response.json();
        if (json.status) {

            document.getElementById("thumb-image").src = "../../assets/Games\\" + json.product.id + "\\thumb-image.jpg";

            //product deatails
            document.getElementById("product-title").innerHTML = json.product.title;

            // Format date without time
            const rawDate = json.product.release_date;
            const dateObj = new Date(rawDate);
            const options = { year: 'numeric', month: 'short', day: 'numeric' };
            const formattedDate = dateObj.toLocaleDateString('en-US', options);

            document.getElementById("release-date").innerHTML = formattedDate;
            document.getElementById("product-description").innerHTML = json.product.description;

            // Format price with 2 decimal places and comma separator
            const formattedPrice = Number(json.product.price).toLocaleString('en-US', {
                style: 'currency',
                currency: 'USD',
                minimumFractionDigits: 2
            });
            document.getElementById("product-price").innerHTML = formattedPrice + ` USD`;

            document.getElementById("product-category").innerHTML = json.product.category_id.name;
            document.getElementById("product-mode").innerHTML = json.product.mode_id.name;
            document.getElementById("product-tag").innerHTML = json.product.tag;
            document.getElementById("product-developer").innerHTML = json.product.developer_id.name;
            document.getElementById("product-size").innerHTML = json.product.game_size + " GB";
            //Minimum Requirements
            document.getElementById("minOs").innerHTML = json.product.min_requirement_id.os + " (64-bit)";
            document.getElementById("minProcessor").innerHTML = json.product.min_requirement_id.processor;
            document.getElementById("minMemory").innerHTML = json.product.min_requirement_id.memory + " GB RAM";
            document.getElementById("minGraphics").innerHTML = json.product.min_requirement_id.graphics;
            document.getElementById("minStorage").innerHTML = json.product.min_requirement_id.storage;

            //Minimum Requirements
            document.getElementById("recOs").innerHTML = json.product.rec_requirement_id.os + " (64-bit)";
            document.getElementById("recProcessor").innerHTML = json.product.rec_requirement_id.processor;
            document.getElementById("recMemory").innerHTML = json.product.rec_requirement_id.memory + " GB RAM";
            document.getElementById("recGraphics").innerHTML = json.product.rec_requirement_id.graphics;
            document.getElementById("recStorage").innerHTML = json.product.rec_requirement_id.storage;


            //add-to-cart-main
            const addToCartButton = document.getElementById("add-to-cart-main");
            addToCartButton.addEventListener(
                "click", (e) => {
                    addToCart(json.product.id);
                    e.preventDefault();
                }
            );
            //add-to-cart-main


            //add-to--wishlist-main
            const addToWishlistButton = document.getElementById("add-to-wishlist-main");
            addToWishlistButton.addEventListener(
                "click", (e) => {
                    addToWishlist(json.product.id);
                    e.preventDefault();
                }
            );
            //add-to--wishlist-main

            let FratureProductMain = document.getElementById("feature-product-main");
            let FratureProduct = document.getElementById("feature-product");

            FratureProductMain.innerHTML = "";
            json.productList.forEach(item => {
                let productCloneHtml = FratureProduct.cloneNode(true);
                productCloneHtml.querySelector("#feature-product-a1").href = "game-details.html?id=" + item.id;
                productCloneHtml.querySelector("#feature-product-image").src = "../../assets/Games\\" + item.id + "\\thumb-image.jpg";
                productCloneHtml.querySelector("#feature-product-title").innerHTML = item.title;
                productCloneHtml.querySelector("#feature-product-price").innerHTML = "$" + new Intl.NumberFormat(
                    "en-US",
                    { minimumFractionDigits: 2 }
                ).format(item.price);

                // Add to Cart button event for featured products
                const featureCartBtn = productCloneHtml.querySelector("#add-to-cart-feature");
                featureCartBtn.addEventListener("click", (e) => {
                    addToCart(item.id);
                    e.preventDefault();
                });

                // Add to wishlist button event for featured products
                const featureWishlistBtn = productCloneHtml.querySelector("#add-to-wishlist-feature");
                featureWishlistBtn.addEventListener("click", (e) => {
                    addToWishlist(item.id);
                    e.preventDefault();
                });

                // Wishlist Design feature
                const featureWishlistBtnDesign = productCloneHtml.querySelector("#add-to-wishlist-feature");
                featureWishlistBtnDesign.addEventListener("click", function () {
                    this.querySelector('i').classList.toggle('fas');
                    this.querySelector('i').classList.toggle('far');
                });

                // Load and show real rating for featured product
                const featureRatingDiv = productCloneHtml.querySelector("#product-rating");
                fetch("../../ProductRating?id=" + item.id)
                    .then(res => res.json())
                    .then(json => {
                        if (json.status) {
                            featureRatingDiv.innerHTML = `${json.rating} <i class="fas fa-star"></i>`;
                        } else {
                            featureRatingDiv.innerHTML = `0 <i class="fas fa-star"></i>`;
                        }
                    })
                    .catch(() => {
                        featureRatingDiv.innerHTML = `0 <i class="fas fa-star"></i>`;
                    });

                FratureProductMain.appendChild(productCloneHtml);

            });

            // Load and show real rating
            loadProductRating(json.product.id);
            loadProductOrderCount(json.product.id);

        } else {
            window.location = "../index.html";
        }
    } else {
        window.location = "../index.html";
    }

}



// wishlist design main
document.getElementById('add-to-wishlist-main').addEventListener('click', function () {
    this.querySelector('i').classList.toggle('fas');
    this.querySelector('i').classList.toggle('far');
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

async function loadProductRating(productId) {
    const ratingBtn = document.getElementById("product-rating");
    try {
        const res = await fetch("../../ProductRating?id=" + productId);
        if (res.ok) {
            const json = await res.json();
            if (json.status) {

                // Show rating with 1 decimal, and count if you want
                ratingBtn.innerHTML = `${json.rating} <i class="fas fa-star">`;
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