window.onload = async function () {
    const response = await fetch("../AdminLoadAllCategoryData");
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            const tbody = document.querySelector("#datatable-buttons tbody");
            tbody.innerHTML = "";

            json.categoryList.forEach(category => {
                const tr = document.createElement("tr");
                tr.innerHTML = `
                    <td class="text-reset">#${category.id}</td>
                    <td>${category.name}</td>
                    <td>
                        <button class="btn btn-sm btn-danger category-delete-btn"
                            data-bs-toggle="tooltip" data-bs-placement="top"
                            data-bs-title="Delete Category">
                            <i class="fas fa-trash-alt"></i>
                        </button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        }
    }
}

// Add Category (modal button)
document.addEventListener("DOMContentLoaded", function () {
    const addBtn = document.querySelector("#addCategoryModal .btn.btn-primary");
    if (addBtn) {
        addBtn.addEventListener("click", async function () {
            const name = document.getElementById("add-Category-name").value.trim();
            if (!name) {
                Swal.fire("Error", "Category name required", "error");
                return;
            }
            // AJAX call
            const res = await fetch("../AdminAddCategory", {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: "name=" + encodeURIComponent(name)
            });
            const data = await res.json();
            if (data.status) {
                Swal.fire({
                    title: "Success!",
                    text: "Category Added Successfully",
                    icon: "success",
                    timer: 1000,
                    showConfirmButton: false
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

// Delete button confirmation using SweetAlert2 (event delegation)
document.addEventListener("click", function (e) {
    if (e.target.closest(".category-delete-btn")) {
        e.preventDefault();
        const btn = e.target.closest(".category-delete-btn");
        const id = btn.closest("tr").querySelector(".text-reset").innerText.replace("#", "");
        Swal.fire({
            title: "Are you sure?",
            text: "Do you want to delete this category?",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#3085d6",
            cancelButtonColor: "#d33",
            confirmButtonText: "Yes, delete it!"
        }).then(async (result) => {
            if (result.isConfirmed) {
                const res = await fetch("../AdminDeleteCategory", {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: "id=" + encodeURIComponent(id)
                });
                const data = await res.json();
                if (data.status) {
                    Swal.fire({
                        title: "Deleted!",
                        text: "Category Deleted Successfully",
                        icon: "success",
                        timer: 1000,
                        showConfirmButton: false
                    }).then(() => location.reload());
                } else {
                    Swal.fire("Error", data.message || "Failed to delete category", "error").then(() => {
                        if (data.message === "Admin not found") {
                            window.location.href = "auth-signin.html";
                        }
                    });
                }
            }
        });
    }
});
