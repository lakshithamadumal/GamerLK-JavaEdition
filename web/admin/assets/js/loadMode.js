window.onload = async function () {
    const response = await fetch("../AdminLoadAllModeData");
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            const tbody = document.querySelector("#datatable-buttons tbody");
            tbody.innerHTML = "";

            json.modeList.forEach(mode => {
                const tr = document.createElement("tr");
                tr.innerHTML = `
                    <td class="text-reset">#${mode.id}</td>
                    <td>${mode.name}</td>
                    <td>
                        <button class="btn btn-sm btn-danger mode-delete-btn"
                            data-bs-toggle="tooltip" data-bs-placement="top"
                            data-bs-title="Delete Game Mode">
                            <i class="fas fa-trash-alt"></i>
                        </button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        }
    }
}

// Add Mode (modal button)
document.addEventListener("DOMContentLoaded", function () {
    const addBtn = document.querySelector("#addModeModal .btn.btn-primary");
    if (addBtn) {
        addBtn.addEventListener("click", async function () {
            const name = document.getElementById("add-Mode-name").value.trim();
            if (!name) {
                Swal.fire("Error", "Mode name required", "error");
                return;
            }
            // AJAX call
            const res = await fetch("../AdminAddMode", {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: "name=" + encodeURIComponent(name)
            });
            const data = await res.json();
            if (data.status) {
                Swal.fire({
                    title: "Success!",
                    text: "Mode Added Successfully",
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
    if (e.target.closest(".mode-delete-btn")) {
        e.preventDefault();
        const btn = e.target.closest(".mode-delete-btn");
        const id = btn.closest("tr").querySelector(".text-reset").innerText.replace("#", "");
        Swal.fire({
            title: "Are you sure?",
            text: "Do you want to delete this Game Mode?",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#3085d6",
            cancelButtonColor: "#d33",
            confirmButtonText: "Yes, delete it!"
        }).then(async (result) => {
            if (result.isConfirmed) {
                const res = await fetch("../AdminDeleteMode", {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: "id=" + encodeURIComponent(id)
                });
                const data = await res.json();
                if (data.status) {
                    Swal.fire({
                        title: "Deleted!",
                        text: "Game Mode Deleted Successfully",
                        icon: "success",
                        timer: 1000,
                        showConfirmButton: false
                    }).then(() => location.reload());
                } else {
                    Swal.fire("Error", data.message || "Failed to delete mode", "error").then(() => {
                        if (data.message === "Admin not found") {
                            window.location.href = "auth-signin.html";
                        }
                    });
                }
            }
        });
    }
});
