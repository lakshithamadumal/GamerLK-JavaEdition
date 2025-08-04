window.onload = async function () {
    const response = await fetch("../AdminLoadAllGamesData");

    if (response.ok) {
        const json = await response.json();
        if (json.status) {

            // Table body
            const tbody = document.querySelector("#datatable-buttons tbody");
            tbody.innerHTML = "";

            json.productList.forEach(product => {
                const tr = document.createElement("tr");
                tr.innerHTML = `
                    <td class="text-reset">#${product.id}</td>
                    <td><img src="../assets/Games/${product.id}/thumb-image.jpg" class="avatar-lg" alt="game-card"></td>
                    <td>${product.title}</td>
                    <td>${product.price}$</td>
                    <td class="downloads-cell" data-id="${product.id}"><span class="spinner-border spinner-border-sm"></span></td>
                    <td class="rating-cell" data-id="${product.id}"><span class="spinner-border spinner-border-sm"></span></td>
                    <td>${product.category_id ? product.category_id.name : '-'}</td>
                    <td>${product.developer_id ? product.developer_id.name : '-'}</td>
                    <td>
                        <span class="badge ${product.status_id && product.status_id.value === 'Active' ? 'bg-success' : 'bg-danger'}">
                            ${product.status_id ? product.status_id.value : '-'}
                        </span>
                    </td>
                    <td>
                        <a href="#" class="btn btn-sm btn-success link-btn game-info-btn" data-id="${product.id}">
                            <i class="fas fa-info-circle" data-bs-toggle="tooltip" data-bs-placement="top" title="Game information"></i>
                        </a>
                        <a href="#" class="btn btn-sm btn-info game-view-btn" data-bs-toggle="tooltip" data-bs-placement="top" data-bs-title="Change Status">
                            <i class="fas fa-eye"></i>
                        </a>
                        <a href="#" class="btn btn-sm btn-warning game-edit-btn" data-bs-toggle="tooltip" data-bs-placement="top" data-bs-title="Update Game">
                            <i class="fas fa-edit"></i>
                        </a>
                        <button class="btn btn-sm btn-danger game-delete-btn" data-bs-toggle="tooltip" data-bs-placement="top" data-bs-title="Delete Game">
                            <i class="fas fa-trash-alt"></i>
                        </button>
                    </td>
                `;
                tbody.appendChild(tr);

                // Fetch downloads count
                fetch(`../ProductOrderCount?id=${product.id}`)
                    .then(res => res.json())
                    .then(data => {
                        const cell = tr.querySelector('.downloads-cell');
                        cell.innerText = data.status ? data.count : '0';
                    });

                // Fetch rating
                fetch(`../ProductRating?id=${product.id}`)
                    .then(res => res.json())
                    .then(data => {
                        const cell = tr.querySelector('.rating-cell');
                        cell.innerText = data.status ? `${data.rating}` : '0.0';
                    });
            });

            // Info button event
            document.querySelectorAll(".game-info-btn").forEach(btn => {
                btn.addEventListener("click", async function (e) {
                    e.preventDefault();
                    const productId = this.getAttribute("data-id");
                    const res = await fetch(`../LoadSingleProduct?id=${productId}`);
                    if (res.ok) {
                        const data = await res.json();
                        if (data.status) {
                            document.getElementById("standard-modalLabel").innerText = data.product.title;
                            document.querySelector("#info-modal img").src = `../assets/Games/${data.product.id}/thumb-image.jpg`;
                            document.querySelector("#info-modal .game-description p").innerText = data.product.description;
                            // Show modal
                            const modal = new bootstrap.Modal(document.getElementById('info-modal'));
                            modal.show();
                        }
                    }
                });
            });

        } else {
            document.getElementById("message").innerHTML = "Unable to get game data!";
        }
    } else {
        document.getElementById("message").innerHTML = "Unable to get game data! Please try again later.";
    }
}


//Change Status button confirmation using SweetAlert2
document.addEventListener("click", function (e) {
    // Change Status
    if (e.target.closest(".game-view-btn")) {
        e.preventDefault();
        const button = e.target.closest(".game-view-btn");
        Swal.fire({
            title: "Are you sure?",
            text: "Do you want to change the status this game?",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#3085d6",
            cancelButtonColor: "#d33",
            confirmButtonText: "Yes, change it!"
        }).then((result) => {
            if (result.isConfirmed) {
                fetch("../AdminChangeGameStatus", {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: "id=" + encodeURIComponent(button.closest("tr").querySelector(".text-reset").innerText.replace("#", ""))
                })
                    .then(res => res.json())
                    .then(data => {
                        if (data.status) {
                            Swal.fire({
                                title: "Changed!",
                                text: "Game Status Changed Successfully",
                                icon: "success",
                                timer: 1000,
                                showConfirmButton: false
                            }).then(() => location.reload());
                        } else {
                            Swal.fire("Error", data.message || "Failed to change status", "error");
                        }
                    });
            }
        });
    }

    // Delete Game
    if (e.target.closest(".game-delete-btn")) {
        e.preventDefault();
        const button = e.target.closest(".game-delete-btn");
        Swal.fire({
            title: "Are you sure?",
            text: "Do you want to delete this game?",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#3085d6",
            cancelButtonColor: "#d33",
            confirmButtonText: "Yes, delete it!"
        }).then((result) => {
            if (result.isConfirmed) {
                fetch("../AdminDeleteGame", {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: "id=" + encodeURIComponent(button.closest("tr").querySelector(".text-reset").innerText.replace("#", ""))
                })
                    .then(res => res.json())
                    .then(data => {
                        if (data.status) {
                            Swal.fire({
                                title: "Deleted!",
                                text: "Game Deleted Successfully",
                                icon: "success",
                                timer: 1000,
                                showConfirmButton: false
                            }).then(() => location.reload());
                        } else {
                            Swal.fire("Error", data.message || "Failed to delete game", "error");
                        }
                    });
            }
        });
    }

    // Edit Game
    if (e.target.closest(".game-edit-btn")) {
        e.preventDefault();
        const button = e.target.closest(".game-edit-btn");
        const productId = button.closest("tr").querySelector(".text-reset").innerText.replace("#", "");
        Swal.fire({
            title: "Are you sure?",
            text: "Do you want to update this game?",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#3085d6",
            cancelButtonColor: "#d33",
            confirmButtonText: "Yes, update it!"
        }).then((result) => {
            if (result.isConfirmed) {
                // Pass productId as query param
                window.location.href = `General-Update-Game.html?id=${productId}`;
            }
        });
    }
});


//Update the game modal trigger to use SweetAlert2 for confirmation
document.addEventListener("DOMContentLoaded", function () {
    const editButtons = document.querySelectorAll(".game-edit-btn");

    editButtons.forEach(button => {
        button.addEventListener("click", function (e) {
            e.preventDefault(); // Stop default modal trigger

            Swal.fire({
                title: "Are you sure?",
                text: "Do you want to update this game?",
                icon: "warning",
                showCancelButton: true,
                confirmButtonColor: "#3085d6",
                cancelButtonColor: "#d33",
                confirmButtonText: "Yes, update it!"
            }).then((result) => {
                if (result.isConfirmed) {
                    // Now open the modal manually
                    window.location.href = "General-Update-Game.html";

                    //Then Success
                    Swal.fire({
                        title: "Updated!",
                        text: "Game Updated Successfully",
                        icon: "success",
                        timer: 1000,
                        showConfirmButton: false
                    });
                }
            });
        });
    });
});
