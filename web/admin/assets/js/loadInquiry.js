window.onload = async function () {
    const response = await fetch("../AdminLoadAllInquiryData");
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            const tbody = document.querySelector("#datatable-buttons tbody");
            tbody.innerHTML = "";

            json.inquiryList.forEach(inquiry => {
                const tr = document.createElement("tr");
                tr.innerHTML = `
                    <td class="text-reset">#${inquiry.id}</td>
                    <td>${inquiry.name}</td>
                    <td>${inquiry.email}</td>
                    <td>
                        <p class="badge ${inquiry.hasAccount ? 'bg-success' : 'bg-danger'} mb-0">
                            ${inquiry.hasAccount ? 'Yes' : 'No'}
                        </p>
                    </td>
                    <td>
                        <a href="#" class="btn btn-sm btn-primary inquiry-info-btn"
                            data-bs-toggle="modal"
                            data-bs-target="#info-modal"
                            data-id="${inquiry.id}">
                            <i class="fas fa-info-circle"
                                data-bs-toggle="tooltip"
                                data-bs-placement="top"
                                title="Inquiry Messages"></i>
                        </a>
                    </td>
                `;
                tbody.appendChild(tr);
            });

            // Info button event
            document.querySelectorAll(".inquiry-info-btn").forEach(btn => {
                btn.addEventListener("click", function () {
                    const inquiryId = this.getAttribute("data-id");
                    const inquiry = json.inquiryList.find(i => i.id == inquiryId);
                    if (inquiry) {
                        document.getElementById("standard-modalLabel").innerText = "Inquiry Message";
                        document.querySelector("#info-modal .modal-body .mb-4 p").innerText = inquiry.message;
                    }
                });
            });
        }
    }
}