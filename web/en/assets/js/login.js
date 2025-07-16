async function Login() {

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const loginBtn = document.getElementById("loginBtn");
    const btnText = document.getElementById("btnText");

    // Disable the button + show spinner
    loginBtn.disabled = true;
    btnText.innerHTML = `<span class="spinner"></span> Logging...`;

    function resetButton() {
        loginBtn.disabled = false;
        btnText.innerHTML = "Create Account";
    }

    const SignIn = {
        email: email,
        password: password
    };

    const SignInJson = JSON.stringify(SignIn);

    const response = await fetch(
        "../../SignIn",
        {
            method: "POST",
            body: SignInJson,
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
            if (json.message === "Not Verified User") {
                window.location = "/Gamerlk/en/pages/verify-email.html";
            } else if (json.message === "Successful Login") {
                window.location = "/Gamerlk/en/index.html";
            } else {
                notyf.error(json.message);
                resetButton();

            }
        } else {
            notyf.error(json.message);
            resetButton();
        }
    } else {
        notyf.error("Log In failed. Please try again later.");
        resetButton();
    }

}
