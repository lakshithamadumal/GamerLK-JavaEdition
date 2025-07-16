async function VerifyAccount() {

    const inputs = document.querySelectorAll('.verification-code-input input');
    let verificationCode = '';

    inputs.forEach(input => {
        verificationCode += input.value.trim();
    });

    const verifyBtn = document.getElementById("verifyBtn");
    const btnText = document.getElementById("btnText");

    // Disable the button + show spinner
    verifyBtn.disabled = true;
    btnText.innerHTML = `<span class="spinner"></span> Verifying...`;

    const verification = {
        verificationCode: verificationCode
    };


    const response = await fetch("../../VerifyAccount", {
        method: "POST",
        body: JSON.stringify(verification),
        headers: {
            "Content-Type": "application/json"
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
            window.location = "/Gamerlk/en/index.html";
        } else {
            if (json.message === "Email not Found") {
                window.location = "/Gamerlk/en/pages/login.html";
            } else {
                notyf.error(json.message);
                resetButton();
            }
        }

    } else {
        notyf.error("Registration failed. Please try again later.");
        resetButton();
    }

    function resetButton() {
        verifyBtn.disabled = false;
        btnText.innerHTML = "Verify & Continue";
    }
}