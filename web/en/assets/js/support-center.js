async function SendMessage() {

    const name = document.getElementById("name").value;
    const email = document.getElementById("email").value;
    const message = document.getElementById("message").value;

    const submitBtn = document.getElementById("submitBtn");
    const btnText = document.getElementById("btnText");

    // Disable the button + show spinner
    submitBtn.disabled = true;
    btnText.innerHTML = `<span class="spinner"></span> Submitting...`;

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

    const notyf = new Notyf({
        position: {
            x: 'center',
            y: 'top'
        }
    });

    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            notyf.success(json.message);
            // Clear fields on success
            document.getElementById("name").value = "";
            document.getElementById("email").value = "";
            document.getElementById("message").value = "";
            resetButton();
        } else {
            notyf.error(json.message);
            resetButton();
        }
    } else {
        notyf.error("Inquiry submitted failed. Please try again later.");
        resetButton();
    }

    function resetButton() {
        submitBtn.disabled = false;
        btnText.innerHTML = "Send Message";
    }
}