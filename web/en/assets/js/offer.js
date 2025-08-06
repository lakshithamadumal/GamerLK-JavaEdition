document.addEventListener("DOMContentLoaded", async function () {
  const notyf = new Notyf({
    position: {
      x: "center",
      y: "top",
    },
  });

  const response = await fetch("../LoadOfferGmames");

  if (response.ok) {
    const json = await response.json();
    let offerContainer = document.getElementById("offer-container");
    let offerCard = document.getElementById("offer-card");

    console.log(json);
    offerContainer.innerHTML = "";

    // Only show first 5 games
    let gamesToShow = json.productList.slice(0, 5);

    for (let offerlist of gamesToShow) {
      // Clone the template card
      let productCloneHtml = offerCard.cloneNode(true);

      // Set data
      productCloneHtml.querySelector("#offer-a1").href =
        "pages/game-offer-details.html?id=" + offerlist.id;
      productCloneHtml.querySelector("#offer-image").src =
        "../assets/Games/" + offerlist.id + "/thumb-image.jpg";
      productCloneHtml.querySelector("#offer").innerHTML =
        offerlist.offer + "% OFF";

      // Show the card (remove id to avoid duplicates)
      productCloneHtml.id = "";
      offerContainer.appendChild(productCloneHtml);
    }
  } else {
    notyf.error({
      message: "Games loading failed...",
    });
  }
});
