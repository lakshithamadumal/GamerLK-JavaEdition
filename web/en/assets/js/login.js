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
        btnText.innerHTML = "Login";
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
                // Show SweetAlert confirmation instead of direct redirect
                Swal.fire({
                    title: "Verification Required",
                    text: "Your account is not verified. Please verify your email to continue.",
                    icon: "warning", // fallback if iconHtml fails
                    iconHtml: '<i class="fas fa-shield-alt" style="font-size: 2.5rem; color: #FFA726;"></i>',
                    showCancelButton: true,
                    confirmButtonText: "Verify Now",
                    cancelButtonText: "Maybe Later",
                    background: "#121212", // deep dark background
                    color: "#e0e0e0",       // light gray text
                    confirmButtonColor: "#FF5722", // vivid orange for CTA
                    cancelButtonColor: "#2E2E2E",  // soft dark grey
                    customClass: {
                        popup: 'dark-modal',
                        confirmButton: 'confirm-btn',
                        cancelButton: 'cancel-btn'
                    }
                }).then((result) => {
                    if (result.isConfirmed) {
                        window.location = "/Gamerlk/en/pages/verify-email.html";
                    } else {
                        resetButton();

                    }
                });

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
