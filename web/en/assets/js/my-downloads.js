document.addEventListener("DOMContentLoaded", async function () {
    const notyf = new Notyf({
        position: {
            x: 'center',
            y: 'top'
        }
    });

    const response = await fetch("../../LoadDownloads");
    if (response.ok) {
        const json = await response.json();

        let DownloadProductContainer = document.getElementById("downloadProductContainer");
        let downloadtProduct = document.getElementById("downloadtProduct");


        DownloadProductContainer.innerHTML = "";
        json.wishlistItems.forEach(download => {
            let product = download.product_id;
            let productCloneHtml = downloadtProduct.cloneNode(true);
            productCloneHtml.querySelector("#downloadtProduct-a1").href = "game-details.html?id=" + product.id;
            productCloneHtml.querySelector("#downloadtProduct-image").src = "../../assets/Games\\" + product.id + "\\thumb-image.jpg";
            productCloneHtml.querySelector("#downloadtProduct-title").innerHTML = product.title;
            productCloneHtml.querySelector("#downloadtProduct-size").innerHTML = product.game_size;
            productCloneHtml.querySelector("#downloadtProduct-date").innerHTML = orders.created_at;
            productCloneHtml.querySelector("#downloadtProduct-link").setAttribute("onclick", "window.open('" + product.game_link + "', '_blank')");

            DownloadProductContainer.appendChild(productCloneHtml);
        });

    } else {
        notyf.error({
            message: "Library loading failed..."
        });
    }


});