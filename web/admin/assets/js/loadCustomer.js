window.onload = async function () {
    const response = await fetch("../AdminLoadAllCustomerData");
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            const tbody = document.querySelector("#datatable-buttons tbody");
            tbody.innerHTML = "";

            json.userList.forEach(user => {
                const tr = document.createElement("tr");
                let actionTd = "";

                if (user.status === "Processing") {
                    actionTd = "";
                } else if (user.status === "Active") {
                    actionTd = `
                        <a href="#" class="btn btn-sm btn-info customer-view-btn"
                            data-bs-toggle="modal"
                            data-bs-target="#info-modal"
                            data-id="${user.id}">
                            <i class="fas fa-eye"></i>
                        </a>
                        <a href="#" class="btn btn-sm btn-danger ban-customer-btn"
                            data-bs-toggle="tooltip" data-bs-placement="top"
                            data-bs-title="Ban Customer">
                            <i class="fas fa-ban"></i>
                        </a>
                    `;
                } else if (user.status === "Inactive") {
                    actionTd = `
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
                    `;
                }

                tr.innerHTML = `
                    <td class="text-reset">#${user.id}</td>
                    <td class="d-flex align-items-center">
                        <img src="../assets/avatar.png" class="avatar avatar-sm rounded-2 me-3" />
                        <p class="mb-0 fw-medium">${user.name}</p>
                    </td>
                    <td>${user.email}</td>
                    <td>${user.downloads}</td>
                    <td>${user.spend}$</td>
                    <td><span class="badge ${user.status === 'Active' ? 'bg-success' : user.status === 'Inactive' ? 'bg-danger' : 'bg-warning'}">${user.status}</span></td>
                    <td>${actionTd}</td>
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
};

// Unban Customer button confirmation using SweetAlert2 (event delegation)
document.addEventListener("click", function (e) {
    if (e.target.closest(".unban-customer-btn")) {
        e.preventDefault();
        const btn = e.target.closest(".unban-customer-btn");
        const userId = btn.closest("tr").querySelector(".text-reset").innerText.replace("#", "");
        Swal.fire({
            title: "Are you sure?",
            text: "Do you want to unban this Customer?",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#3085d6",
            cancelButtonColor: "#d33",
            confirmButtonText: "Yes, unban it!"
        }).then((result) => {
            if (result.isConfirmed) {
                fetch("../AdminUnbanCustomer", {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: "id=" + encodeURIComponent(userId)
                })
                .then(res => res.json())
                .then(data => {
                    if (data.status) {
                        Swal.fire({
                            title: "Unbanned!",
                            text: "Customer unbanned Successfully",
                            icon: "success",
                            timer: 1000,
                            showConfirmButton: false
                        }).then(() => location.reload());
                    } else {
                    Swal.fire("Error", data.message || "Failed to unban Customer", "error").then(() => {
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

// Ban Customer button confirmation using SweetAlert2 (event delegation)
document.addEventListener("click", function (e) {
    if (e.target.closest(".ban-customer-btn")) {
        e.preventDefault();
        const btn = e.target.closest(".ban-customer-btn");
        const userId = btn.closest("tr").querySelector(".text-reset").innerText.replace("#", "");
        Swal.fire({
            title: "Are you sure?",
            text: "Do you want to ban this Customer?",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#3085d6",
            cancelButtonColor: "#d33",
            confirmButtonText: "Yes, ban it!"
        }).then((result) => {
            if (result.isConfirmed) {
                fetch("../AdminBanCustomer", {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: "id=" + encodeURIComponent(userId)
                })
                .then(res => res.json())
                .then(data => {
                    if (data.status) {
                        Swal.fire({
                            title: "Banned!",
                            text: "Customer banned Successfully",
                            icon: "success",
                            timer: 1000,
                            showConfirmButton: false
                        }).then(() => location.reload());
                    } else {
                    Swal.fire("Error", data.message || "Failed to Ban Customer", "error").then(() => {
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

