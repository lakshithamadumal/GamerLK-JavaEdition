async function Subscribe() {
    const email = document.getElementById("email").value;

    const subscribeBtn = document.getElementById("subscribeBtn");
    const btnText = document.getElementById("btnText");

    // Disable the button + show spinner
    subscribeBtn.disabled = true;
    btnText.innerHTML = `<span class="spinner"></span>`;

    const Email = {
        email: email
    };

    const EmailJson = JSON.stringify(Email);

    const response = await fetch(
        "../Subscribe",
        {
            method: "POST",
            body: EmailJson,
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
            document.getElementById("email").value = "";
            resetButton();
        } else {
            notyf.error(json.message);
            resetButton();
        }
    } else {
        notyf.error("Subscribe failed. Please try again later.");
        resetButton();
    }

    function resetButton() {
        subscribeBtn.disabled = false;
        btnText.innerHTML = "Subscribe";
    }
}