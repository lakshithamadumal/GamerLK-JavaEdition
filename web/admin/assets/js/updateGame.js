window.onload = async function () {
    const response = await fetch("../LoadProductData");

    if (response.ok) {
        const json = await response.json();
        if (json.status) {

            loadSelect("gameCategory", json.categoryList, "name");
            loadSelect("gameDeveloper", json.developerList, "name");
            loadSelect("gameMod", json.modeList, "name");
            loadSelect("gameMin", json.requirementList, "name");

            // Disable gameMax initially
            const gameMaxSelect = document.getElementById("gameMax");
            gameMaxSelect.disabled = true;

            // Add event to gameMin
            document.getElementById("gameMin").addEventListener("change", function () {
                const selectedMinId = this.value;

                // Enable max select
                gameMaxSelect.disabled = false;

                // Reload max select excluding selectedMinId
                loadMaxSelect("gameMax", json.requirementList, "name", selectedMinId);
            });

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
        option.textContent = item[property];
        select.appendChild(option);
    });
}

// 🛠️ Fix: Add skipId param and check with string comparison
function loadMaxSelect(selectId, list, property, skipId) {
    const select = document.getElementById(selectId);
    select.innerHTML = ""; // clear old

    list.forEach(item => {
        if (item.id.toString() !== skipId.toString()) { // make sure type-safe
            const option = document.createElement("option");
            option.value = item.id;
            option.textContent = item[property];
            select.appendChild(option);
        }
    });
}



async function updateGame() {

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
    form.append("thumbnailImage", thumbnailImage);

    const response = await fetch("../UpdateProduct", {
        method: "POST",
        body: form,
    });

    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            Swal.fire({
                icon: 'success',
                title: 'Updated',
                text: json.message,
                timer: 2000,
                showConfirmButton: false
            }).then(() => {
                location.reload();
            });
        } else {
            // Check for "Admin Not found" message
            if (json.message === "Admin Not found") {
                Swal.fire({
                    icon: 'error',
                    title: json.message,
                    timer: 2000,
                    showConfirmButton: false
                }).then(() => {
                    location.reload();
                });
            } else if (json.message === "Game Already Added") {
                Swal.fire({
                    icon: 'info',
                    title: json.message,
                    timer: 2000,
                    showConfirmButton: false
                });
            } else {
                Swal.fire({
                    icon: 'error',
                    title: json.message,
                    timer: 2000,
                    showConfirmButton: false
                });
            }
        }
    } else {
        Swal.fire({
            icon: 'error',
            title: 'Log In failed. Please try again later.',
            showConfirmButton: false
        });
    }

}

document.addEventListener("DOMContentLoaded", async function () {
    // Get product id from URL
    const urlParams = new URLSearchParams(window.location.search);
    const productId = urlParams.get("id");
    if (!productId) return;

    // Load game details
    const res = await fetch(`../LoadSingleProduct?id=${productId}`);
    if (res.ok) {
        const data = await res.json();
        if (data.status) {
            const game = data.product;
            document.getElementById("gameName").value = game.title;
            document.getElementById("gameDescription").value = game.description;
            document.getElementById("gamePrice").value = game.price;
            document.getElementById("gameLink").value = game.game_link;
            document.getElementById("gameSize").value = game.game_size;
            document.getElementById("gameDate").value = game.release_date ? game.release_date.split("T")[0] : "";
            document.getElementById("gameTag").value = game.tag;

            // Dropdowns: Category, Mode, Developer, Min/Rec Requirement
            // Load all options first (if not loaded), then set selected
            // Example for category:
            const catSel = document.getElementById("gameCategory");
            if (catSel && game.category_id) catSel.value = game.category_id.id;

            const modeSel = document.getElementById("gameMod");
            if (modeSel && game.mode_id) modeSel.value = game.mode_id.id;

            const devSel = document.getElementById("gameDeveloper");
            if (devSel && game.developer_id) devSel.value = game.developer_id.id;

            const minSel = document.getElementById("gameMin");
            if (minSel && game.min_requirement_id) minSel.value = game.min_requirement_id.id;

            const maxSel = document.getElementById("gameMax");
            if (maxSel && game.rec_requirement_id) maxSel.value = game.rec_requirement_id.id;

            // Thumbnail preview (optional)
            const thumb = document.getElementById("thumbnail-image-preview");
            if (thumb) {
                thumb.src = `../assets/Games/${game.id}/thumb-image.jpg`;
                thumb.style.display = "block";
            }
        }
    }
});

// document.getElementById("addGameForm").addEventListener("submit", async function (e) {
//     e.preventDefault();
//     const urlParams = new URLSearchParams(window.location.search);
//     const productId = urlParams.get("id");
//     const formData = new FormData(this);
//     formData.append("id", productId);

//     const res = await fetch("../UpdateProduct", {
//         method: "POST",
//         body: formData
//     });
//     const data = await res.json();
//     if (data.status) {
//         Swal.fire("Success", data.message, "success").then(() => {
//             window.location.href = "General-Games.html";
//         });
//     } else {
//         Swal.fire("Error", data.message, "error");
//     }
// });