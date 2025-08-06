//load all active inactive games
document.addEventListener("DOMContentLoaded", async function () {
  const response = await fetch("../AdminLoadGamesOffer");

  if (response.ok) {
    const json = await response.json();
    if (json.status) {
      loadSelect("offer-game", json.GameList, "title");

      const select = document.getElementById("offer-game");
      select.addEventListener("change", function () {
        const selectedId = this.value;
        const selectedProduct = json.GameList.find(
          (item) => item.id == selectedId
        );
        if (selectedProduct) {
          document.getElementById("recent-price-label").value =
            selectedProduct.price + "$";
        } else {
          document.getElementById("recent-price-label").value = "";
        }
      });
    } else {
      document.getElementById("message").innerHTML =
        "Unable to get product data!";
    }
  } else {
    document.getElementById("message").innerHTML =
      "Unable to get product data! Please try again later.";
  }
});

function loadSelect(selectId, list, property) {
  const select = document.getElementById(selectId);
  select.innerHTML = `<option value="0" selected disabled>-- Choose a Game --</option>`;
  list.forEach((item) => {
    select.innerHTML += `<option value="${item.id}">${item[property]}</option>`;
  });
}

async function createOffer() {
  const gameId = document.getElementById("offer-game").value;
  const gameDiscount = document.getElementById("discount-percentage").value;

  const data = {
    gameId: gameId,
    gameDiscount: gameDiscount,
  };

  const response = await fetch("../AdminCreateOffer", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  });

  if (response.ok) {
    const json = await response.json();
    if (json.status) {
      Swal.fire({
        title: "Success!",
        text: "Offer Created Successfully",
        icon: "success",
        timer: 1000,
        showConfirmButton: false,
      }).then(() => location.reload());
    } else {
      Swal.fire("Error", json.message, "error").then(() => {
        if (json.message === "Admin not found") {
          window.location.href = "auth-signin.html";
        }
      });
    }
  } else {
    Swal.fire("Error", "Network error!", "error");
  }
}
