async function SendMessage() {

    const name = document.getElementById("name").value;
    const email = document.getElementById("email").value;
    const message = document.getElementById("message").value;

    const Message = {
        name: name,
        email: email,
        message: message
    };
    const MessageJson = JSON.stringify(Message);

    const response = await fetch(
        "../../MessageInquiry",
        {
            method: "POST",
            body: MessageJson,
            headers: {
                "Content-type": "application/json"
            }
        }
    );
}