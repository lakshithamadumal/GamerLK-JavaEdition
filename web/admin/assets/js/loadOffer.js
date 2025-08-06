window.onload = async function () {
  const response = await fetch("../AdminLoadAllOfferData");
  if (response.ok) {
    const json = await response.json();
    if (json.status) {
      const tbody = document.querySelector("#datatable-buttons tbody");
      tbody.innerHTML = "";

      json.offerList.forEach((product) => {
        // Old price
        const oldPrice = product.price;
        // Offer % (int)
        const offerPercent = product.offer;
        // New price calculation
        const newPrice = (oldPrice - (oldPrice * offerPercent) / 100).toFixed(
          2
        );

        const tr = document.createElement("tr");
        tr.innerHTML = `
                    <td class="text-reset">#${product.id}</td>
                    <td>
                        <img src="../assets/Games/${product.id}/thumb-image.jpg" class="avatar-lg" alt="Developer-Card">
                    </td>
                    <td>${product.title}</td>
                    <td>${oldPrice}$</td>
                    <td>${newPrice}$</td>
                    <td>${offerPercent}%</td>
                    <td>
                        <button class="btn btn-sm btn-danger Offer-delete-btn"
                            data-bs-toggle="tooltip"
                            data-bs-placement="top"
                            data-bs-title="Close Offer">
                            <i class="fas fa-trash-alt"></i>
                        </button>
                    </td>
                `;
        tbody.appendChild(tr);
      });
    } else {
      document.getElementById("message").innerHTML =
        "Unable to get offer data!";
    }
  } else {
    document.getElementById("message").innerHTML =
      "Unable to get offer data! Please try again later.";
  }
};

document.addEventListener("click", function (e) {
  const btn = e.target.closest(".Offer-delete-btn");
  if (btn) {
    e.preventDefault();
    const tr = btn.closest("tr");
    const productId = tr
      .querySelector("td")
      .textContent.replace("#", "")
      .trim();

    Swal.fire({
      title: "Are you sure?",
      text: "Do you want to close this Offer?",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText: "Yes, close it!",
    }).then((result) => {
      if (result.isConfirmed) {
        fetch("../AdminCloseOffer", {
          method: "POST",
          headers: { "Content-Type": "application/x-www-form-urlencoded" },
          body: "id=" + encodeURIComponent(productId),
        })
          .then((res) => res.json())
          .then((data) => {
            if (data.status) {
              Swal.fire({
                title: "Closed!",
                text: "Offer closed Successfully",
                icon: "success",
                timer: 1000,
                showConfirmButton: false,
              }).then(() => location.reload());
            } else {
              Swal.fire("Error", data.message, "error").then(() => {
                if (data.message === "Admin not found") {
                  window.location.href = "auth-signin.html";
                }
              });
            }
          });
      }
    });
  }
});
