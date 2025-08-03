document.addEventListener("DOMContentLoaded", function () {
    let currentPage = 1;
    const pageSize = 10;

    async function loadOrders(page = 1) {
        const tbody = document.querySelector(".purchase-table tbody");
        tbody.innerHTML = `<tr><td colspan="6">Loading...</td></tr>`;

        const response = await fetch(`../../LoadOrderHistory?page=${page}`);
        const data = await response.json();

        if (data.status) {
            tbody.innerHTML = "";
            if (data.orderItems.length === 0) {
                tbody.innerHTML = `<tr><td colspan="6">No orders found.</td></tr>`;
                renderPagination(1, 1);
                return;
            }
            data.orderItems.forEach(item => {
                const tr = document.createElement("tr");
                tr.innerHTML = `
                    <td data-label="Order ID">${item.orderIdStr}</td>
                    <td data-label="Game Name">
                        <div class="game-info">
                            <img src="${item.gameImg}" alt="Game Cover">
                            <span>${item.gameTitle}</span>
                        </div>
                    </td>
                    <td data-label="Price">$${item.price.toFixed(2)}</td>
                    <td data-label="Purchase Date">${item.purchaseDate.split('T')[0]}</td>
                    <td data-label="Rate Now">
                        <button class="invoice-btn rate-btn" 
                            data-title="${item.gameTitle}" 
                            data-img="${item.gameImg}">
                            <i class="far fa-star"></i>
                        </button>
                    </td>
                    <td data-label="Action">
                        <button class="invoice-btn" onclick="location.href='../includes/order-invoice.html?orderId=${item.orderId.toString().padStart(4, '0')}'">
                            <i class="fas fa-file-invoice"></i>
                            Invoice
                        </button>
                    </td>
                `;
                tbody.appendChild(tr);
            });

            // Add event listeners for rate buttons
            document.querySelectorAll(".rate-btn").forEach(btn => {
                btn.addEventListener("click", function () {
                    openRatingModal(this.dataset.title, this.dataset.img);
                });
            });

            // Pagination
            const totalPages = Math.ceil(data.total / pageSize);
            renderPagination(page, totalPages);
        } else {
            tbody.innerHTML = `<tr><td colspan="6">Unable to load order history.</td></tr>`;
        }
    }

    function renderPagination(current, total) {
        const pag = document.querySelector(".pagination");
        pag.innerHTML = "";

        const prevBtn = document.createElement("button");
        prevBtn.className = "pagination-btn" + (current === 1 ? " disabled" : "");
        prevBtn.innerHTML = `<i class="fas fa-chevron-left"></i>`;
        prevBtn.disabled = current === 1;
        prevBtn.onclick = () => { if (current > 1) { loadOrders(current - 1); } };
        pag.appendChild(prevBtn);

        for (let i = 1; i <= total; i++) {
            const span = document.createElement("span");
            span.textContent = i;
            if (i === current) span.className = "active";
            span.onclick = () => loadOrders(i);
            pag.appendChild(span);
        }

        const nextBtn = document.createElement("button");
        nextBtn.className = "pagination-btn" + (current === total ? " disabled" : "");
        nextBtn.innerHTML = `<i class="fas fa-chevron-right"></i>`;
        nextBtn.disabled = current === total;
        nextBtn.onclick = () => { if (current < total) { loadOrders(current + 1); } };
        pag.appendChild(nextBtn);
    }

    // Modal logic
    window.openRatingModal = function (title, img) {
        const modal = document.getElementById('ratingModal');
        modal.classList.add('active');
        modal.querySelector('.game-title').textContent = title;
        modal.querySelector('.cover-img').src = img;
        // Reset rating UI as before...
        selectedRating = 0;
        hoveredRating = 0;
        updateStars();
        updateFeedbackPreview();
        submitBtn.disabled = false;
        ratingFeedback.classList.remove('show');
        emojiFeedback.textContent = '';
        messageFeedback.textContent = '';
    };

    loadOrders(currentPage);
});
