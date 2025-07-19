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