document.addEventListener("DOMContentLoaded", async function () {
    // Get orderId from URL
    const urlParams = new URLSearchParams(window.location.search);
    const orderId = urlParams.get("orderId");
    if (!orderId) return;

    // Fetch invoice data from backend
    const response = await fetch("../../GetOrderInvoice?orderId=" + encodeURIComponent(orderId));
    if (!response.ok) {
        // Unauthorized, forbidden, or not found: redirect to home
        window.location.href = "../index.html";
        return;
    }

    const data = await response.json();

    // Fill BILL TO
    document.getElementById("bill-name").textContent = data.userName;
    document.getElementById("bill-email").textContent = data.userEmail;

    // Fill INVOICE DETAILS
    document.getElementById("invoice-order").textContent = data.orderNumber;
    document.getElementById("invoice-date").textContent = data.dueDate;

    // Fill Table
    const tbody = document.getElementById("invoice-items");
    tbody.innerHTML = "";
    let subtotal = 0;
    data.items.forEach(item => {
        subtotal += item.price;
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td>
                <strong>${item.productName}</strong><br>
                <small>${item.developer}</small>
            </td>
            <td>$${item.price.toFixed(2)}</td>
            <td></td>
            <td class="text-right">$${item.price.toFixed(2)}</td>
        `;
        tbody.appendChild(tr);
    });

    // Subtotal, Discount, Tax, Total
    document.getElementById("subtotal").textContent = "$" + subtotal.toFixed(2);
    document.getElementById("discount").textContent = "$0.00";
    document.getElementById("tax").textContent = "$0.00";
    document.getElementById("total-due").textContent = "$" + subtotal.toFixed(2);
});