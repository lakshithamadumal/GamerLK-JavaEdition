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
                    <td>${product.downloads || 0}</td>
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