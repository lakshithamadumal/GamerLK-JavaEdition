document.addEventListener("DOMContentLoaded", function () {
    let currentPage = 1;
    const pageSize = 10;

    const notyf = new Notyf({
        position: {
            x: 'center',
            y: 'top'
        }
    });

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
                    <td data-label="Purchase Date">
                    ${new Date(item.purchaseDate).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })}
                    </td>
                    <td data-label="Rate Now">
                        <button class="invoice-btn rate-btn" 
                            data-title="${item.gameTitle}" 
                            data-img="${item.gameImg}"
                            data-orderid="${item.orderId}"
                            data-productid="${item.productId}"
                            data-rating="${item.rating}">
                            <i class="${item.rating > 0 ? 'fas' : 'far'} fa-star"></i>
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
                    window.openRatingModal(
                        this.dataset.img,
                        parseInt(this.dataset.orderid),
                        parseInt(this.dataset.productid),
                        parseInt(this.dataset.rating)
                    );
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

    let selectedRating = 0;
    let hoveredRating = 0;
    let currentOrderId = null;
    let currentProductId = null;
    let alreadyRated = false;

    // Modal logic
    window.openRatingModal = function (img, orderId, productId, rating) {
        const modal = document.getElementById('ratingModal');
        modal.classList.add('active');
        modal.querySelector('.cover-img').src = img;
        currentOrderId = orderId;
        currentProductId = productId;
        selectedRating = rating;
        hoveredRating = 0;
        alreadyRated = rating > 0;

        updateStars();
        updateFeedbackPreview();

        submitBtn.disabled = alreadyRated;
        document.querySelectorAll('.star').forEach(star => {
            star.style.pointerEvents = alreadyRated ? 'none' : 'auto';
        });

        ratingFeedback.classList.remove('show');
        emojiFeedback.textContent = '';
        messageFeedback.textContent = '';
    };

    loadOrders(currentPage);

    // --- Modal submit logic ---
    const stars = document.querySelectorAll('.star');
    const submitBtn = document.getElementById('submitBtn');
    const ratingFeedback = document.getElementById('ratingFeedback');
    const emojiFeedback = document.getElementById('emojiFeedback');
    const messageFeedback = document.getElementById('messageFeedback');

    stars.forEach(star => {
        star.addEventListener('click', () => {
            if (alreadyRated) return;
            selectedRating = parseInt(star.getAttribute('data-rating'));
            updateStars();
            //updateFeedbackPreview();
        });
        star.addEventListener('mouseover', () => {
            if (alreadyRated) return;
            hoveredRating = parseInt(star.getAttribute('data-rating'));
            updateStars();
        });
        star.addEventListener('mouseout', () => {
            if (alreadyRated) return;
            hoveredRating = 0;
            updateStars();
        });
    });

    submitBtn.addEventListener('click', async () => {
        if (alreadyRated) return;
        if (selectedRating > 0 && currentOrderId && currentProductId) {
            submitBtn.disabled = true;
            // Save rating to server
            const res = await fetch('../../SaveRating', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: `orderId=${currentOrderId}&productId=${currentProductId}&rating=${selectedRating}`
            });
            const data = await res.json();
            if (data.status) {
                //showFinalFeedback();
                setTimeout(() => {
                    document.getElementById('ratingModal').classList.remove('active');
                    // Update star icon in table
                    document.querySelectorAll('.rate-btn').forEach(btn => {
                        if (parseInt(btn.dataset.orderid) === currentOrderId && parseInt(btn.dataset.productid) === currentProductId) {
                            btn.querySelector('i').className = 'fas fa-star';
                            btn.dataset.rating = selectedRating;
                        }
                    });
                    // Show notification
                    notyf.success('Thank you for your rating!');
                }, 500);
            } else {
                notyf.error(data.message || 'Error saving rating');
                submitBtn.disabled = false;
            }
        } else {
            showTemporaryMessage('Please select a rating first!');
        }
    });
});
