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
            document.getElementById("product-price").innerHTML = formattedPrice;

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
            document.getElementById("minStorage").innerHTML = json.product.min_requirement_id.storage + " GB available space";

            //Minimum Requirements
            document.getElementById("recOs").innerHTML = json.product.rec_requirement_id.os + " (64-bit)";
            document.getElementById("recProcessor").innerHTML = json.product.rec_requirement_id.processor;
            document.getElementById("recMemory").innerHTML = json.product.rec_requirement_id.memory + " GB RAM";
            document.getElementById("recGraphics").innerHTML = json.product.rec_requirement_id.graphics;
            document.getElementById("recStorage").innerHTML = json.product.rec_requirement_id.storage + " GB available space";


            //add-to-cart-main
            const addToCartButton = document.getElementById("add-to-cart-main");
            addToCartButton.addEventListener(
                "click", (e) => {
                    addToCart(json.product.id);
                    e.preventDefault();
                }
            );
            //add-to-cart-main

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


                FratureProductMain.appendChild(productCloneHtml);

            });

        } else {
            window.location = "../index.html";
        }
    } else {
        window.location = "../index.html";
    }

}


function addToCart(productId) {
    console.log(productId);
}
