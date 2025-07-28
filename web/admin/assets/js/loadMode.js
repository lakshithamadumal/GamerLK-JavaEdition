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
                        <a href="#" class="btn btn-sm btn-warning mode-edit-btn" 
                            data-bs-toggle="tooltip" data-bs-placement="top"
                            data-bs-title="Update Game Mode">
                            <i class="fas fa-edit"></i>
                        </a>
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