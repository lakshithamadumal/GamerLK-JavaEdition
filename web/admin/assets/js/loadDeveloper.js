window.onload = async function () {
    const response = await fetch("../AdminLoadAllDeveloperData");
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            const tbody = document.querySelector("#datatable-buttons tbody");
            tbody.innerHTML = "";

            json.developerList.forEach(developer => {
                const tr = document.createElement("tr");
                tr.innerHTML = `
                    <td class="text-reset">#${developer.id}</td>
                    <td>${developer.name}</td>
                    <td>
                        <button class="btn btn-sm btn-danger Developer-delete-btn"
                            data-bs-toggle="tooltip" data-bs-placement="top"
                            data-bs-title="Delete Developer">
                            <i class="fas fa-trash-alt"></i>
                        </button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        }
    }
}

// Add Developer (modal button)
document.addEventListener("DOMContentLoaded", function () {
    const addBtn = document.querySelector("#addDeveloperModal .btn.btn-primary");
    if (addBtn) {
        addBtn.addEventListener("click", async function () {
            const name = document.getElementById("add-Developer-name").value.trim();
            if (!name) {
                Swal.fire("Error", "Developer name required", "error");
                return;
            }
            // AJAX call
            const res = await fetch("../AdminAddDeveloper", {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: "name=" + encodeURIComponent(name)
            });
            const data = await res.json();
            if (data.status) {
                Swal.fire({
                    title: "Success!",
                    text: "Developer Added Successfully",
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
    if (e.target.closest(".Developer-delete-btn")) {
        e.preventDefault();
        const btn = e.target.closest(".Developer-delete-btn");
        const id = btn.closest("tr").querySelector(".text-reset").innerText.replace("#", "");
        Swal.fire({
            title: "Are you sure?",
            text: "Do you want to delete this Developer?",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#3085d6",
            cancelButtonColor: "#d33",
            confirmButtonText: "Yes, delete it!"
        }).then(async (result) => {
            if (result.isConfirmed) {
                const res = await fetch("../AdminDeleteDeveloper", {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: "id=" + encodeURIComponent(id)
                });
                const data = await res.json();
                if (data.status) {
                    Swal.fire({
                        title: "Deleted!",
                        text: "Developer Deleted Successfully",
                        icon: "success",
                        timer: 1000,
                        showConfirmButton: false
                    }).then(() => location.reload());
                } else {
                    Swal.fire("Error", data.message || "Failed to delete developer", "error").then(() => {
                        if (data.message === "Admin not found") {
                            window.location.href = "auth-signin.html";
                        }
                    });
                }
            }
        });
    }
});