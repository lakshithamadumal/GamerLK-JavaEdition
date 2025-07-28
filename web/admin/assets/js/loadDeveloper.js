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
                        <img src="../assets/Developers/${developer.id}/dev.jpg" class="developer-thumbnail" alt="Developer-Card">
                    </td>
                    <td>
                        <a href="#" class="btn btn-sm btn-warning Developer-edit-btn"
                            data-bs-toggle="tooltip" data-bs-placement="top"
                            data-bs-title="Update Developer">
                            <i class="fas fa-edit"></i>
                        </a>
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