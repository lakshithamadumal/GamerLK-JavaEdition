window.onload = async function () {
    const response = await fetch("../AdminLoadAllRequirementData");
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            const tbody = document.querySelector("#datatable-buttons tbody");
            tbody.innerHTML = "";

            json.requirementList.forEach(requirement => {
                const tr = document.createElement("tr");
                tr.innerHTML = `
                    <td class="text-reset">#${requirement.id}</td>
                    <td>${requirement.name}</td>
                    <td>${requirement.os}</td>
                    <td>${requirement.memory}</td>
                    <td>${requirement.processor}</td>
                    <td>${requirement.graphics}</td>
                    <td>${requirement.storage}</td>
                    <td>
                        <button class="btn btn-sm btn-danger requirement-delete-btn"
                            data-bs-toggle="tooltip" data-bs-placement="top"
                            data-bs-title="Delete Requirement">
                            <i class="fas fa-trash-alt"></i>
                        </button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        }
    }
}

// Add Requirement (modal button)
document.addEventListener("DOMContentLoaded", function () {
    const addBtn = document.querySelector("#addRequirementModal .btn.btn-primary");
    if (addBtn) {
        addBtn.addEventListener("click", async function () {
            const name = document.getElementById("add-name").value.trim();
            const os = document.getElementById("add-os-name").value.trim();
            const memory = document.getElementById("add-memory-name").value.trim();
            const processor = document.getElementById("add-processor-name").value.trim();
            const graphics = document.getElementById("add-graphics-name").value.trim();
            const storage = document.getElementById("add-storage-name").value.trim();

            if (!os || !memory || !processor || !graphics || !storage) {
                Swal.fire("Error", "All fields are required", "error");
                return;
            }

            const params = new URLSearchParams({
                name,
                os,
                memory,
                processor,
                graphics,
                storage
            });

            const res = await fetch("../AdminAddRequirement", {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: params.toString()
            });
            const data = await res.json();
            if (data.status) {
                Swal.fire({
                    title: "Success!",
                    text: "Requirement Added Successfully",
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
    if (e.target.closest(".requirement-delete-btn")) {
        e.preventDefault();
        const btn = e.target.closest(".requirement-delete-btn");
        const id = btn.closest("tr").querySelector(".text-reset").innerText.replace("#", "");
        Swal.fire({
            title: "Are you sure?",
            text: "Do you want to remove this Requirement?",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#3085d6",
            cancelButtonColor: "#d33",
            confirmButtonText: "Yes, remove it!"
        }).then(async (result) => {
            if (result.isConfirmed) {
                const res = await fetch("../AdminDeleteRequirement", {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: "id=" + encodeURIComponent(id)
                });
                const data = await res.json();
                if (data.status) {
                    Swal.fire({
                        title: "Removed!",
                        text: "Requirement removed Successfully",
                        icon: "success",
                        timer: 1000,
                        showConfirmButton: false
                    }).then(() => location.reload());
                } else {
                    Swal.fire("Error", data.message || "Failed to remove requirement", "error").then(() => {
                        if (data.message === "Admin not found") {
                            window.location.href = "auth-signin.html";
                        }
                    });
                }
            }
        });
    }
});


