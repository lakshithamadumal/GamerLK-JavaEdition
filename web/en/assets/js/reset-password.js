async function GetEmail() {
    const email = document.getElementById("email").value;


    const sendBtn = document.getElementById("sendBtn");
    const btnText = document.getElementById("btnText");

    // Disable the button + show spinner
    sendBtn.disabled = true;
    btnText.innerHTML = `<span class="spinner"></span> Sending...`;

    const UserEmail = {
        email: email
    }

    const notyf = new Notyf({
        position: {
            x: 'center',
            y: 'top'
        }
    });

    const EmailJson = JSON.stringify(UserEmail);

    const response = await fetch(
        "../../ResetPWGetEmail",  //reset pasword to get email and vetify
        {
            method: "POST",
            body: EmailJson,
            headers: {
                "Content-type": "application/json"
            }
        }
    );

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


            } else if (json.message === "User Found") {
                window.location = "/Gamerlk/en/pages/verify-email-password.html";
            } else {
                notyf.error(json.message);
                resetButton();
            }
        } else {
            notyf.error(json.message);
            resetButton();
        }
    } else {
        notyf.error("Verify Email failed. Please try again later.");
        resetButton();
    }


    function resetButton() {
        sendBtn.disabled = false;
        btnText.innerHTML = "Send Verification Code";
    }
}