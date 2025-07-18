async function GetEmail() {
    const email = document.getElementById("email").value;

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
                window.location = "/Gamerlk/en/pages/verify-email.html";
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
}