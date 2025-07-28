window.onload = async function () {
    const response = await fetch("../AdminLoadAllSubscriberData");
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            const tbody = document.querySelector("#datatable-buttons tbody");
            tbody.innerHTML = "";

            json.subscriberList.forEach(subscriber => {
                const tr = document.createElement("tr");
                tr.innerHTML = `
                    <td class="text-reset">#${subscriber.id}</td>
                    <td>${subscriber.email}</td>
                    <td>
                        <p class="badge ${subscriber.hasAccount === 'Yes' ? 'bg-success' : 'bg-danger'} mb-0">${subscriber.hasAccount}</p>
                    </td>
                    <td>
                        <a href="mailto:${subscriber.email}"
                            class="btn btn-sm btn-primary" data-bs-toggle="tooltip"
                            data-bs-placement="top" title="Send Email to Subscriber">
                            Send
                        </a>
                    </td>
                    <td>
                        <a href="#" class="btn btn-sm btn-danger remove-Subscriber-btn"
                            data-bs-toggle="tooltip" data-bs-placement="top"
                            data-bs-title="Remove Subscriber">
                            <i class="fas fa-ban"></i>
                        </a>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        }
    }
}