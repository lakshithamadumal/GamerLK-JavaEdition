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

        console.log(json);

        let DownloadProductContainer = document.getElementById("downloadProductContainer");
        let downloadtProduct = document.getElementById("downloadtProduct");


        DownloadProductContainer.innerHTML = "";
        json.downloadItems.forEach(download => {
            let product = download.product_id;
            let order = download.orders_id;
            let productCloneHtml = downloadtProduct.cloneNode(true);
            productCloneHtml.querySelector("#downloadtProduct-a1").href = "game-details.html?id=" + product.id;
            productCloneHtml.querySelector("#downloadtProduct-image").src = "../../assets/Games\\" + product.id + "\\thumb-image.jpg";
            productCloneHtml.querySelector("#downloadtProduct-title").innerHTML = product.title;
            productCloneHtml.querySelector("#downloadtProduct-size").innerHTML = product.game_size + " GB";
            // Format date if needed
            let dateStr = order && order.created_at
                ? new Date(order.created_at).toLocaleDateString('en-US', {
                    year: 'numeric',
                    month: 'short',
                    day: 'numeric'
                })
                : "";

            productCloneHtml.querySelector("#downloadtProduct-date").innerHTML = dateStr;

            productCloneHtml.querySelector("#downloadtProduct-link").setAttribute("onclick", "window.open('" + product.game_link + "', '_blank')");

            DownloadProductContainer.appendChild(productCloneHtml);
        });

    } else {
        notyf.error({
            message: "Library loading failed..."
        });
    }


});