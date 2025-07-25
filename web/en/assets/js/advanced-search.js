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

    json.productList.forEach(product => {
        let searchProduct_clone = searchProduct.cloneNode(true);

        //update cards
        searchProduct_clone.querySelector("#searchProduct-a1").href = "game-details.html?pid=" + product.id;
        searchProduct_clone.querySelector("#searchProduct-image").src = "../../assets/Games\\" + product.id + "\\thumb-image.jpg";
        searchProduct_clone.querySelector("#searchProduct-title").innerHTML = product.title;
        searchProduct_clone.querySelector("#searchProduct-price").innerHTML = "$" + new Intl.NumberFormat(
                "en-US",
                {
                    minimumFractionDigits: 2
                }
        ).format(product.price);

        searchProductContainer.appendChild(searchProduct_clone);

    });

    // //start pagination
    // let st_pagination_container = document.getElementById("st-pagination-container");
    // st_pagination_container.innerHTML = "";

    // let product_count = json.allProductCount;
    // const product_per_page = 6;

    // let pages = Math.ceil(product_count / product_per_page);

    // //add previous button
    // if (currentPage != 0) {
    //     let st_pagination_button_clone_prev = st_pagination_button.cloneNode(true);
    //     st_pagination_button_clone_prev.innerHTML = "Prev";

    //     st_pagination_button_clone_prev.addEventListener("click", e => {
    //         currentPage--;
    //         searchProducts(currentPage * 6);
    //     });

    //     st_pagination_container.appendChild(st_pagination_button_clone_prev);
    // }

    // //add page buttons
    // for (let i = 0; i < pages; i++) {
    //     let st_pagination_button_clone = st_pagination_button.cloneNode(true);
    //     st_pagination_button_clone.innerHTML = i + 1;

    //     st_pagination_button_clone.addEventListener("click", e => {
    //         currentPage = i;
    //         searchProducts(i * 6);
    //     });

    //     if (i === currentPage) {
    //         st_pagination_button_clone.className = "axil-btn btn-bg-secondary me-2";
    //     } else {
    //         st_pagination_button_clone.className = "axil-btn btn-bg-primary me-2";
    //     }

    //     st_pagination_container.appendChild(st_pagination_button_clone);
    // }

    // //add Next button
    // if (currentPage != (pages - 1)) {
    //     let st_pagination_button_clone_next = st_pagination_button.cloneNode(true);
    //     st_pagination_button_clone_next.innerHTML = "Next";

    //     st_pagination_button_clone_next.addEventListener("click", e => {
    //         currentPage++;
    //         searchProducts(currentPage * 6);
    //     });

    //     st_pagination_container.appendChild(st_pagination_button_clone_next);
    // }

}
