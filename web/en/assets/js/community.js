document.addEventListener("DOMContentLoaded", function () {
    const chatMessages = document.querySelector(".chat-messages");
    const messageInput = document.querySelector(".message-input");
    const sendBtn = document.querySelector(".send-btn");
    const emptyState = document.querySelector(".empty-state");

    let lastMessageCount = 0;

    function renderMessages(messages) {
        chatMessages.innerHTML = "";
        if (!messages || messages.length === 0) {
            emptyState.style.display = "flex";
            return;
        }
        emptyState.style.display = "none";
        messages.forEach(msg => {
            const isSent = msg.user_id && msg.user_id.id === window.currentUserId;
            const messageDiv = document.createElement("div");
            messageDiv.className = "message " + (isSent ? "sent" : "received");
            messageDiv.innerHTML = `
                <div class="message-avatar">
                    <img src="../../assets/avatar.png" alt="${msg.user_id ? msg.user_id.username : "User"}">
                    ${!isSent ? '<span class="online-badge"></span>' : ""}
                </div>
                <div class="message-content">
                    <div class="message-header">
                        ${!isSent ? `<span class="username">${msg.user_id
                        ? (msg.user_id.username
                            ? msg.user_id.username
                            : (msg.user_id.first_name + " " + msg.user_id.last_name))
                        : "Unknown"
                    }</span>` : ""}
                        <span class="timestamp">${new Date(msg.sent_at).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })}</span>
                    </div>
                    <div class="message-text">${msg.message}</div>
                </div>
            `;
            chatMessages.appendChild(messageDiv);
        });
        chatMessages.scrollTop = chatMessages.scrollHeight;
    }

    // Get current user id (implement this endpoint or set via JS)
    window.currentUserId = null;
    fetch("../../getCurrentUser")
        .then(r => r.json())
        .then(j => { window.currentUserId = j.id; });

    async function pollMessages() {
        try {
            const response = await fetch("../../CommunityChat");
            if (response.ok) {
                const messages = await response.json();
                if (messages.length !== lastMessageCount) {
                    renderMessages(messages);
                    lastMessageCount = messages.length;
                }
            }
        } catch (e) { }
        setTimeout(pollMessages, 2000);
    }
    pollMessages();

    async function sendMessage() {
        const messageText = messageInput.value.trim();
        if (!messageText) return;
        sendBtn.disabled = true;
        try {
            const res = await fetch("../../CommunityChat", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ message: messageText })
            });
            const json = await res.json();
            if (json.status) {
                messageInput.value = "";
            } else {
                alert(json.msg || "Failed to send");
            }
        } catch (e) {
            alert("Network error");
        }
        sendBtn.disabled = false;
    }

    sendBtn.addEventListener("click", sendMessage);
    messageInput.addEventListener("keypress", function (e) {
        if (e.key === "Enter") sendMessage();
    });
});