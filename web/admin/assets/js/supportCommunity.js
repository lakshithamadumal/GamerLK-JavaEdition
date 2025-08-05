document.addEventListener("DOMContentLoaded", function () {
    const chatBox = document.querySelector(".chat-conversation ul");
    const input = document.querySelector(".chat-input");
    const sendBtn = document.querySelector(".btn-primary.btn-sm.ms-1");
    let adminEmail = null;

    // Get admin email from session (via endpoint or set manually)
    fetch("../GetCurrentAdmin")
        .then(r => r.json())
        .then(j => { adminEmail = j.email; });

    // Render messages
    function renderMessages(messages) {
        chatBox.innerHTML = "";
        messages.forEach(msg => {
            const isAdmin = msg.user_id == null; // admin messages have no user_id
            const name = isAdmin ? (adminEmail || "Admin") : (
                msg.user_id.username
                    ? msg.user_id.username
                    : (msg.user_id.first_name + " " + msg.user_id.last_name)
            );
            const time = new Date(msg.sent_at).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
            let tick = isAdmin
                ? `<span style="color:#2196f3;font-size:14px;margin-left:4px;" title="Verified"><i class="mdi mdi-check-decagram"></i></span>`
                : "";
            let avatar = isAdmin
                ? `<img src="../assets/avatar.png" alt="admin" class="rounded-circle" width="40">`
                : `<img src="../assets/avatar.png" alt="user" class="rounded-circle" width="40">`;

            chatBox.innerHTML += isAdmin
                ? `
                <li class="mb-3">
                    <div class="d-flex">
                        <div class="flex-shrink-0">${avatar}</div>
                        <div class="flex-grow-1 ms-2">
                            <div class="bg-white p-2 rounded">
                                <h5 class="fs-14 mb-1">${name}${tick}
                                    <small class="text-muted fs-12 ms-1">${time}</small>
                                </h5>
                                <p class="text-muted mb-0">${msg.message}</p>
                            </div>
                        </div>
                    </div>
                </li>
                `
                : `
                <li class="mb-3">
                    <div class="d-flex justify-content-end">
                        <div class="flex-grow-1 me-2 text-end">
                            <div class="bg-primary-subtle text-dark p-2 rounded">
                                <h5 class="fs-14 mb-1">${name}
                                    <small class="opacity-75 fs-12 ms-1">${time}</small>
                                </h5>
                                <p class="mb-0">${msg.message}</p>
                            </div>
                        </div>
                        <div class="flex-shrink-0">${avatar}</div>
                    </div>
                </li>
                `;
        });
        chatBox.parentElement.scrollTop = chatBox.parentElement.scrollHeight;
    }

    // Poll messages
    async function pollMessages() {
        try {
            const response = await fetch("../CommunityChat");
            if (response.ok) {
                const messages = await response.json();
                renderMessages(messages);
            }
        } catch (e) {}
        setTimeout(pollMessages, 2000);
    }
    pollMessages();

    // Send message
    async function sendMessage() {
        const text = input.value.trim();
        if (!text) return;
        sendBtn.disabled = true;
        try {
            const res = await fetch("../AdminCommunityChat", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ message: text })
            });
            const json = await res.json();
            if (json.status) {
                input.value = "";
            } else {
                Swal.fire("Error", json.msg || "Failed to send", "error");
            }
        } catch (e) {
            Swal.fire("Error", "Network error", "error");
        }
        sendBtn.disabled = false;
    }

    sendBtn.addEventListener("click", sendMessage);
    input.addEventListener("keypress", function (e) {
        if (e.key === "Enter") sendMessage();
    });
});