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
                        <img src="../assets/Category/${category.id}/cat.jpg" class="category-thumbnail" alt="Category-Card">
                    </td>
                    <td>
                        <a href="#" class="btn btn-sm btn-warning category-edit-btn" 
                            data-bs-toggle="tooltip" data-bs-placement="top"
                            data-bs-title="Update Category">
                            <i class="fas fa-edit"></i>
                        </a>
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