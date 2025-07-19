window.onload = async function () {

    const response = await fetch("../LoadProductData");

    if (response.ok) {
        const json = await response.json();
        if (json.status) {

            loadSelect("gameCategory", json.categoryList, "name");
            loadSelect("gameDeveloper", json.developerList, "name");
            loadSelect("gameMod", json.modeList, "name");
            loadSelect("gameMin", json.requirementList, "os");
            loadSelect("gameMax", json.requirementList, "os");

        } else {
            document.getElementById("message").innerHTML = "Unable to get product data!";
        }
    } else {
        document.getElementById("message").innerHTML = "Unable to get product data! Please try again later.";
    }

}


function loadSelect(selectId, list, property) {

    const select = document.getElementById(selectId);

    list.forEach(item => {
        const option = document.createElement("option");
        option.value = item.id;
        option.innerHTML = item[property];
        select.appendChild(option);
    });

}


async function addGame() {

    const gameName = document.getElementById("gameName").value;
    const gameDescription = document.getElementById("gameDescription").value;
    const gamePrice = document.getElementById("gamePrice").value;
    const gameLink = document.getElementById("gameLink").value;
    const gameSize = document.getElementById("gameSize").value;
    const gameDate = document.getElementById("gameDate").value;
    const gameCategory = document.getElementById("gameCategory").value;
    const gameMod = document.getElementById("gameMod").value;
    const gameDeveloper = document.getElementById("gameDeveloper").value;
    const gameTag = document.getElementById("gameTag").value;
    const gameMin = document.getElementById("gameMin").value;
    const gameMax = document.getElementById("gameMax").value;
    const cardImage = document.getElementById("cardImage").files[0];
    const thumbnailImage = document.getElementById("thumbnailImage").files[0];


    const form = new FormData();
    form.append("gameName", gameName);
    form.append("gameDescription", gameDescription);
    form.append("gamePrice", gamePrice);
    form.append("gameLink", gameLink);
    form.append("gameSize", gameSize);
    form.append("gameDate", gameDate);
    form.append("gameCategory", gameCategory);
    form.append("gameMod", gameMod);
    form.append("gameDeveloper", gameDeveloper);
    form.append("gameTag", gameTag);
    form.append("gameMin", gameMin);
    form.append("gameMax", gameMax);
    form.append("cardImage", cardImage);
    form.append("thumbnailImage", thumbnailImage);

    const response = await fetch("../SaveProduct", {
        method: "POST",
        body: form,
    });

    const result = await response.text();
    console.log("Server Response:", result);
}