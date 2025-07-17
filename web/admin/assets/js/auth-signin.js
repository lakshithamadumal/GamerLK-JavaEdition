async function SignIn() {

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const signinBtn = document.getElementById("signinBtn");
    const btnText = document.getElementById("btnText");

    // Disable the button + show spinner
    signinBtn.disabled = true;
    btnText.innerHTML = `<span class="spinner"></span> Signin...`;

    function resetButton() {
        signinBtn.disabled = false;
        btnText.innerHTML = "Create Account";
    }

    const SignIn = {
        email: email,
        password: password
    };

    const SignInJson = JSON.stringify(SignIn);

    const response = await fetch(
        "../AdminSignIn",
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
                window.location = "/Gamerlk/admin/index.html";
        } else {
            notyf.error(json.message);
            resetButton();
        }
    } else {
        notyf.error("Log In failed. Please try again later.");
        resetButton();
    }
}