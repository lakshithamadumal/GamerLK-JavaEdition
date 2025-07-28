window.onload = async function () {
    const response = await fetch("../AdminLoadAllCustomerData");
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            const tbody = document.querySelector("#datatable-buttons tbody");
            tbody.innerHTML = "";

            json.userList.forEach(user => {
                const tr = document.createElement("tr");
                tr.innerHTML = `
                    <td class="text-reset">#${user.id}</td>
                    <td class="d-flex align-items-center">
                        <img src="../assets/avatar.png" class="avatar avatar-sm rounded-2 me-3" />
                        <p class="mb-0 fw-medium">${user.name}</p>
                    </td>
                    <td>${user.email}</td>
                    <td>${user.downloads}</td>
                    <td>${user.spend}</td>
                    <td><span class="badge ${user.status === 'Active' ? 'bg-success' : 'bg-danger'}">${user.status}</span></td>
                    <td>
                        <a href="#" class="btn btn-sm btn-info customer-view-btn"
                            data-bs-toggle="modal"
                            data-bs-target="#info-modal"
                            data-id="${user.id}">
                            <i class="fas fa-eye"></i>
                        </a>
                        <a href="#" class="btn btn-sm btn-success unban-customer-btn"
                            data-bs-toggle="tooltip" data-bs-placement="top"
                            data-bs-title="Unban Customer">
                            <i class="fas fa-unlock"></i>
                        </a>
                        <a href="#" class="btn btn-sm btn-danger ban-customer-btn"
                            data-bs-toggle="tooltip" data-bs-placement="top"
                            data-bs-title="Ban Customer">
                            <i class="fas fa-ban"></i>
                        </a>
                    </td>
                `;
                tbody.appendChild(tr);
            });

            // Info button event
            document.querySelectorAll(".customer-view-btn").forEach(btn => {
                btn.addEventListener("click", function () {
                    const userId = this.getAttribute("data-id");
                    const user = json.userList.find(u => u.id == userId);
                    if (user) {
                        document.getElementById("standard-modalLabel").innerText = user.name;
                        // Wishlist count
                        document.querySelector("#info-modal .fa-bookmark").parentElement.nextElementSibling.innerText = user.wishlistCount;
                        // Cart count
                        document.querySelector("#info-modal .fa-shopping-cart").parentElement.nextElementSibling.innerText = user.cartCount;
                    }
                });
            });
        }
    }
}