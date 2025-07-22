async function loadSingleProductData() {

    const productId = new URLSearchParams(window.location.search).get('id');
    console.log(productId);

    const response = await fetch("../../LoadSingleProduct?id=" + productId);
    if (response.ok) {
        const json = await response.json();
        if (json.status) {

            document.getElementById("thumb-image").src = "../../assets/Games\\" + json.product.id + "\\thumb-image.png";

            //product deatails
            document.getElementById("product-title").innerHTML = json.product.title;
            document.getElementById("release-date").innerHTML = json.product.release_date;
            document.getElementById("product-description").innerHTML = json.product.description;
            document.getElementById("product-price").innerHTML = "$" + json.product.price + " USD";
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
        } else {
            window.location = "../../index.html";
        }
    } else {
        window.location = "../../index.html";
    }

}