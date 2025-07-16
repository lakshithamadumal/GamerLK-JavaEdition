async function Register() {
    const firstName = document.getElementById("firstName").value;
    const lastName = document.getElementById("lastName").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const registerBtn = document.getElementById("registerBtn");
    const btnText = document.getElementById("btnText");

    // Disable the button + show spinner
    registerBtn.disabled = true;
    btnText.innerHTML = `<span class="spinner"></span> Creating...`;

    const user = {
        firstName,
        lastName,
        email,
        password
    };

    const response = await fetch("../../SignUp", {
        method: "POST",
        body: JSON.stringify(user),
        headers: {
            "Content-type": "application/json"
        }
    });

    const notyf = new Notyf({
        position: {
            x: 'center',
            y: 'top'
        }
    });

    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            window.location = "/Gamerlk/en/pages/verify-email.html"; // instant redirect
        } else {
            notyf.error(json.message);
            resetButton();
        }
    } else {
        notyf.error("Registration failed. Please try again later.");
        resetButton();
    }

    function resetButton() {
        registerBtn.disabled = false;
        btnText.innerHTML = "Create Account";
    }
}
