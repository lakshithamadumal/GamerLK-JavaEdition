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
    <button class="btn btn-sm btn-primary" onclick="navigator.clipboard.writeText('${subscriber.email}')"
        data-bs-toggle="tooltip" data-bs-placement="top" title="Copy Email">
        Copy
    </button>
</td>
                `;
                tbody.appendChild(tr);
            });
        }
    }
}