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