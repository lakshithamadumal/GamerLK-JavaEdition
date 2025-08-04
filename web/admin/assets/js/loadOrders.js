window.onload = async function () {
    const response = await fetch("../AdminLoadAllOrderData");
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            const tbody = document.querySelector("#datatable-buttons tbody");
            tbody.innerHTML = "";

            json.orderList.forEach(order => {
                const tr = document.createElement("tr");
                tr.innerHTML = `
                    <td class="text-reset">#${order.id}</td>
                    <td class="d-flex align-items-center">
                        <p class="mb-0 fw-medium">${order.customer}</p>
                    </td>
                    <td>$${order.price}</td>
                    <td>${order.items}</td>
                    <td>${order.created}</td>
                    <td>
                        <p class="badge bg-success mb-0">${order.status}</p>
                    </td>
                    <td>
                        <a href="#" class="btn btn-sm btn-info order-view-btn"
                            data-bs-toggle="modal"
                            data-bs-target="#info-modal"
                            data-id="${order.id}">
                            <i data-bs-toggle="tooltip" data-bs-placement="top" data-bs-title="Order Infomation" class="fas fa-eye"></i>
                        </a>
                    </td>
                `;
                tbody.appendChild(tr);
            });

            // Info button event
            document.querySelectorAll(".order-view-btn").forEach(btn => {
                btn.addEventListener("click", function () {
                    const orderId = this.getAttribute("data-id");
                    const order = json.orderList.find(o => o.id == orderId);
                    if (order) {
                        document.getElementById("standard-modalLabel").innerText = "Order #" + order.id;
                        const modalBody = document.querySelector("#info-modal .modal-body .mb-4 .row");
                        modalBody.innerHTML = "";
                        order.games.forEach(game => {
                            modalBody.innerHTML += `
                                <div class="col-12">
                                    <div class="border rounded-3 px-3 py-3 d-flex justify-content-between align-items-center">
                                        <div>
                                            <h5 class="mb-1 text-dark">${game.title}</h5>
                                        </div>
                                        <div class="text-end">
                                            <h5 class="mb-1 text-success">$${game.price}</h5>
                                        </div>
                                    </div>
                                </div>
                            `;
                        });
                    }
                });
            });
        } else {
            document.getElementById("message").innerHTML = "Unable to get order data!";
        }
    } else {
        document.getElementById("message").innerHTML = "Unable to get order data! Please try again later.";
    }
}