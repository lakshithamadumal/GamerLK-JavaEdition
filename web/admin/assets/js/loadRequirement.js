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
                        <a href="#" class="btn btn-sm btn-warning requirement-edit-btn" 
                            data-bs-toggle="tooltip" data-bs-placement="top"
                            data-bs-title="Update Requirement">
                            <i class="fas fa-edit"></i>
                        </a>
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