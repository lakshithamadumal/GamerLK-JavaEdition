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
        json.productList.forEach(gamelist => {
            let productCloneHtml = storeGames.cloneNode(true);

            // Set data
            productCloneHtml.querySelector("a").href = "game-details.html?id=" + gamelist.id;
            productCloneHtml.querySelector("img").src = "../../assets/Games\\" + gamelist.id + "\\thumb-image.jpg";
            productCloneHtml.querySelector(".game-title").innerHTML = gamelist.title;
            productCloneHtml.querySelector(".game-price-row span").innerHTML = "$" + new Intl.NumberFormat(
                "en-US",
                { minimumFractionDigits: 2 }
            ).format(gamelist.price);

            storeGamesContainer.appendChild(productCloneHtml);
        });

    } else {
        notyf.error({
            message: "Games loading failed..."
        });
    }
});