window.onload = async function () {
    const response = await fetch("../AdminLoadAllDashboardData");
    if (response.ok) {
        const json = await response.json();
        if (!json.status) return;

        // Today Earning
        document.querySelectorAll(".fs-22.mb-0.me-2.fw-semibold.text-black")[0].innerText = "$" + json.todayEarning;
        // Total Earning
        document.querySelectorAll(".fs-22.mb-0.me-2.fw-semibold.text-black")[1].innerText = "$" + json.totalEarning;
        // Selling Games
        document.querySelectorAll(".fs-22.mb-0.me-2.fw-semibold.text-black")[2].innerText = json.sellingGames;
        // Total Customer
        document.querySelectorAll(".fs-22.mb-0.me-2.fw-semibold.text-black")[3].innerText = json.totalCustomer;

        // Monthly Sales Chart
        const months = json.monthlySales.map(m => m.month);
        const sales = json.monthlySales.map(m => m.count);
        if (window.monthlySalesChart) window.monthlySalesChart.destroy();
        window.monthlySalesChart = new ApexCharts(document.querySelector("#monthly-game-sales"), {
            chart: { type: "bar", height: 307, parentHeightOffset: 0, toolbar: { show: !1 } },
            colors: ["#537AEF"],
            series: [{ name: "Sales", data: sales }],
            fill: { opacity: 1 },
            plotOptions: { bar: { columnWidth: "50%", borderRadius: 4, borderRadiusApplication: "end", borderRadiusWhenStacked: "last", dataLabels: { position: "top", orientation: "vertical" } } },
            grid: { strokeDashArray: 4, padding: { top: -20, right: 0, bottom: -4 }, xaxis: { lines: { show: !0 } } },
            xaxis: { type: "category", categories: months, axisTicks: { color: "#f0f4f7" } },
            yaxis: { title: { text: "Number of Sales", style: { fontSize: "12px", fontWeight: 600 } } },
            tooltip: { theme: "light" },
            legend: { position: "top", show: !0, horizontalAlign: "center" },
            stroke: { width: 0 },
            dataLabels: { enabled: !1 },
            theme: { mode: "light" }
        });
        window.monthlySalesChart.render();

        // Top Selling Games
        const topGamesList = document.querySelectorAll(".card-body ul.list-group.custom-group")[0];
        topGamesList.innerHTML = "";
        json.topGames.forEach(g => {
            topGamesList.innerHTML += `
                <li class="list-group-item align-items-center d-flex justify-content-between">
                    <div class="product-list">
                        <img class="avatar-md p-1 rounded-circle bg-primary-subtle img-fluid me-3" src="../assets/Games/${g.id}/thumb-image.jpg" alt="product-image">
                        <div class="product-body align-self-center">
                            <h6 class="m-0 fw-semibold">${g.title}</h6>
                            <p class="mb-0 mt-1 text-muted">${g.developer}</p>
                        </div>
                    </div>
                    <div class="product-price">
                        <h6 class="m-0 fw-semibold">$${g.revenue}</h6>
                        <p class="mb-0 mt-1 text-muted">${g.sold} Sold</p>
                    </div>
                </li>
            `;
        });

        // Recent Orders
        const recentOrderTable = document.querySelector(".table.table-traffic.mb-0");
        const tbody = recentOrderTable.querySelector("tbody") || recentOrderTable;
        tbody.innerHTML = `
            <tr>
                <th>Order ID</th>
                <th>Customer</th>
                <th>Price</th>
                <th>Created</th>
                <th colspan="2">Status</th>
            </tr>
        `;
        json.recentOrders.forEach(order => {
            tbody.innerHTML += `
                <tr>
                    <td><a href="javascript:void(0);" class="text-reset">#${order.id}</a></td>
                    <td class="d-flex align-items-center"><p class="mb-0 fw-medium">${order.customer}</p></td>
                    <td><p class="mb-0">$${order.price}</p></td>
                    <td><p class="mb-0">${order.date}</p></td>
                    <td><p class="badge bg-success mb-0">${order.status}</p></td>
                    <td>
                        <a href="#" class="btn btn-sm btn-info order-view-btn" data-bs-toggle="modal" data-bs-target="#info-modal" data-id="${order.id}">
                            <i class="fas fa-eye"></i>
                        </a>
                    </td>
                </tr>
            `;
        });

        // Order info modal
        document.querySelectorAll(".order-view-btn").forEach(btn => {
            btn.addEventListener("click", function () {
                const orderId = this.getAttribute("data-id");
                const order = json.recentOrders.find(o => o.id == orderId);
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

        // Top Customers
        const topCustomerList = document.querySelectorAll(".card-body ul.list-group.custom-group")[1];
        topCustomerList.innerHTML = "";
        json.topCustomers.forEach(c => {
            topCustomerList.innerHTML += `
                <li class="list-group-item align-items-center d-flex justify-content-between">
                    <div class="product-list">
                        <img class="avatar-md p-1 rounded-circle bg-primary-subtle img-fluid me-3" src="../assets/avatar.png" alt="product-image">
                        <div class="product-body align-self-center">
                            <h6 class="m-0 fw-semibold">${c.name}</h6>
                            <p class="mb-0 mt-1 text-muted">${c.registerDate}</p>
                        </div>
                    </div>
                    <div class="product-price">
                        <h6 class="m-0 fw-semibold">$${c.spend}</h6>
                        <p class="mb-0 mt-1 text-muted">${c.owned} Owned</p>
                    </div>
                </li>
            `;
        });
    }
}