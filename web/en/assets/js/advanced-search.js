async function LoadData() {
    const response = await fetch("../../LoadDataAdvancedSearch");

    if (response.ok) {
        const json = await response.json();
        if (json.status) {

            loadSelect("gameCategory", json.categoryList, "name");
            loadSelect("gameMod", json.modeList, "name");

        } else {
            notyf.error("Unable to get product data!");
        }
    } else {
        notyf.error("Unable to get product data! Please try again later.");
    }
}

const notyf = new Notyf({
    position: {
        x: 'center',
        y: 'top'
    }
});


function loadSelect(selectId, list, property) {
    const select = document.getElementById(selectId);

    list.forEach(item => {
        const option = document.createElement("option");
        option.value = item.id;
        option.textContent = item[property];
        select.appendChild(option);
    });
}



async function searchProducts(firstResult) {
    const gameCategory = document.getElementById("gameCategory").value;
    const gameMod = document.getElementById("gameMod").value;
    const gamesort = document.getElementById("gamesort").value;
    const rawPrice = document.getElementById("price-value").innerText;
    const gametext = document.getElementById("gametext").value.trim();
    const pricevalue = parseFloat(rawPrice.replace(/[^0-9.]/g, ''));


    const data = {
        firstResult: firstResult,
        gameCategory: gameCategory,
        gameMod: gameMod,
        gamesort: gamesort,
        gametext: gametext,
        pricevalue: pricevalue
    };

    try {
        const response = await fetch("../../SearchProducts", {
            method: "POST",
            body: JSON.stringify(data),
            headers: {
                "Content-Type": "application/json"
            }
        });

        if (!response.ok) {
            notyf.erro("Failed to fetch products");
        }

        const json = await response.json();

        if (json.status) {
            console.log("Total Products", json);

            updateProductView(json);

        } else {
            notyf.error("Product fetch failed. Backend responded with status false.");
        }

    } catch (error) {
        notyf.error("Error fetching products");
    }
}

var searchProduct = document.getElementById("searchProduct");
var currentPage = 0;


function updateProductView(json) {
    let searchProductContainer = document.getElementById("searchProductContainer");
    searchProductContainer.innerHTML = "";

    // If no products found
    if (!json.productList || json.productList.length === 0) {
        notyf.error("No Games found!");
        // Hide advanced result section
        const advancedResultSection = document.querySelector('.advanceSearchResult:not(.d-none)');
        if (advancedResultSection) {
            advancedResultSection.classList.add('d-none');
        }
        // Show AllProductsContainer
        const allProductsContainer = document.querySelector('.AllProductsContainer.d-none');
        if (allProductsContainer) {
            allProductsContainer.classList.remove('d-none');
        }
        return;
    }

    json.productList.forEach(product => {
        let searchProduct_clone = searchProduct.cloneNode(true);

        //update cards
        searchProduct_clone.querySelector("#searchProduct-a1").href = "game-details.html?id=" + product.id;
        searchProduct_clone.querySelector("#searchProduct-image").src = "../../assets/Games\\" + product.id + "\\thumb-image.jpg";
        searchProduct_clone.querySelector("#searchProduct-title").innerHTML = product.title;
        searchProduct_clone.querySelector("#searchProduct-price").innerHTML = "$" + new Intl.NumberFormat(
            "en-US",
            {
                minimumFractionDigits: 2
            }
        ).format(product.price);


        // Add to Cart button event for search products
        const searchCartBtn = searchProduct_clone.querySelector("#add-to-cart-ad");
        searchCartBtn.addEventListener("click", (e) => {
            addToCart(product.id);
            e.preventDefault();
        });

        // Add to wishlist button event for search products
        const searchWishlistBtn = searchProduct_clone.querySelector("#add-to-wishlist-ad");
        searchWishlistBtn.addEventListener("click", (e) => {
            addToWishlist(product.id);
            e.preventDefault();
        });

        // wishlist design ad
        const searchWishlistBtnDesign = searchProduct_clone.querySelector("#add-to-wishlist-ad");
        searchWishlistBtnDesign.addEventListener("click", function () {
            this.querySelector('i').classList.toggle('fas');
            this.querySelector('i').classList.toggle('far');
        });

        searchProductContainer.appendChild(searchProduct_clone);

    });

    // Show the advanced result section by removing d-none
    const advancedResultSection = document.querySelector('.advanceSearchResult.d-none');
    if (advancedResultSection) {
        advancedResultSection.classList.remove('d-none');
    }
    // Hide AllProductsContainer
    const allProductsContainer = document.querySelector('.AllProductsContainer:not(.d-none)');
    if (allProductsContainer) {
        allProductsContainer.classList.add('d-none');
    }

    // Pagination logic
    let paginationContainer = document.getElementById("advanced-pagination-container");
    paginationContainer.innerHTML = "";

    let product_count = json.allProductCount;
    const product_per_page = 4;
    let pages = Math.ceil(product_count / product_per_page);

    // Previous button
    let prevBtn = document.createElement("button");
    prevBtn.className = "pagination-btn";
    prevBtn.innerHTML = `<i class="fas fa-chevron-left"></i>`;
    if (currentPage === 0) {
        prevBtn.classList.add("disabled");
    } else {
        prevBtn.onclick = function () {
            currentPage--;
            searchProducts(currentPage * product_per_page);
        };
    }
    paginationContainer.appendChild(prevBtn);

    // Page numbers
    for (let i = 0; i < pages; i++) {
        let pageSpan = document.createElement("span");
        pageSpan.textContent = (i + 1);
        if (i === currentPage) {
            pageSpan.className = "active";
        } else {
            pageSpan.onclick = function () {
                currentPage = i;
                searchProducts(i * product_per_page);
            };
            pageSpan.style.cursor = "pointer";
        }
        paginationContainer.appendChild(pageSpan);
    }

    // Next button
    let nextBtn = document.createElement("button");
    nextBtn.className = "pagination-btn";
    nextBtn.innerHTML = `<i class="fas fa-chevron-right"></i>`;
    if (currentPage >= pages - 1) {
        nextBtn.classList.add("disabled");
    } else {
        nextBtn.onclick = function () {
            currentPage++;
            searchProducts(currentPage * product_per_page);
        };
    }
    paginationContainer.appendChild(nextBtn);
}

document.addEventListener("DOMContentLoaded", function () {
    const closeBtn = document.querySelector(".close-advance-result");
    if (closeBtn) {
        closeBtn.addEventListener("click", function () {
            const advanceResultSection = document.querySelector(".advanceSearchResult");
            if (advanceResultSection) {
                advanceResultSection.classList.add("d-none");
            }
            // Show AllProductsContainer when closing advanced search
            const allProductsContainer = document.querySelector('.AllProductsContainer.d-none');
            if (allProductsContainer) {
                allProductsContainer.classList.remove('d-none');
            }
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
                notyf.success("Already Added");
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
            notyf.success("Already Added");
        } else {
            notyf.error(json.message);

        }
    } else {
        notyf.error("Game Add to wishlist failed.");

    }

}