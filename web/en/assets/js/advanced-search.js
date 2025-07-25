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

            updateProductView(json.productList);

        } else {
            notyf.error("Product fetch failed. Backend responded with status false.");
        }

    } catch (error) {
        notyf.error("Error fetching products");
    }
}
