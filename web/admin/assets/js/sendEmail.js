async function SendEmail() {
    const emailSubject = document.getElementById("emailSubject").value;
    const emailBody = document.getElementById("emailBody").value;

    const sendBtn = document.getElementById("sendBtn");
    const btnText = document.getElementById("btnText");

    // Disable the button + show spinner
    sendBtn.disabled = true;
    btnText.innerHTML = `<span class="spinner"></span> Sending...`;

    function resetButton() {
        sendBtn.disabled = false;
        btnText.innerHTML = "Send Email";
    }

    const Send = {
        emailSubject: emailSubject,
        emailBody: emailBody,
    };

    const SendJson = JSON.stringify(Send);

    try {
        const response = await fetch(
            "../SendEmail",
            {
                method: "POST",
                body: SendJson,
                headers: {
                    "Content-type": "application/json"
                }
            }
        );
        const result = await response.json();

        if (result.status) {
            Swal.fire({
                icon: "success",
                title: "Success",
                text: result.message || "Emails are being sent!",
                timer: 2500,
                showConfirmButton: false
            });
            resetButton();
            //Clear Field 
            document.getElementById("emailSubject").value = "";
            document.getElementById("emailBody").value = "";
        } else {
            Swal.fire({
                icon: "error",
                title: "Error",
                text: result.message || "Failed to send email.",
                timer: 2500,
                showConfirmButton: false
            });
            resetButton();
        }
    } catch (error) {
        Swal.fire({
            icon: "error",
            title: "Error",
            text: "Network error. Please try again.",
            timer: 2500,
            showConfirmButton: false
        });
        resetButton();

    }
}



async function SendSingleEmail() {
    const Singleemail = document.getElementById("Singleemail").value;
    const SingleemailSubject = document.getElementById("SingleemailSubject").value;
    const SingleemailBody = document.getElementById("SingleemailBody").value;

    const SingleSendBtn = document.getElementById("SingleSendBtn");
    const SingleBtnText = document.getElementById("SingleBtnText");

    // Disable the button + show spinner
    SingleSendBtn.disabled = true;
    SingleBtnText.innerHTML = `<span class="spinner"></span> Sending...`;

    function resetButton() {
        SingleSendBtn.disabled = false;
        SingleBtnText.innerHTML = "Send Email";
    }

    const SingleSend = {
        Singleemail: Singleemail,
        SingleemailSubject: SingleemailSubject,
        SingleemailBody: SingleemailBody
    };

    const SingleSendJson = JSON.stringify(SingleSend);

    try {
        const response = await fetch(
            "../SingleSendEmail",
            {
                method: "POST",
                body: SingleSendJson,
                headers: {
                    "Content-type": "application/json"
                }
            }
        );
        const result = await response.json();

        if (result.status) {
            Swal.fire({
                icon: "success",
                title: "Success",
                text: result.message || "Emails are being sent!",
                timer: 2500,
                showConfirmButton: false
            });
            resetButton();
                        //Clear Field 
            document.getElementById("Singleemail").value = "";
            document.getElementById("SingleemailSubject").value = "";
            document.getElementById("SingleemailBody").value = "";
        } else {
            Swal.fire({
                icon: "error",
                title: "Error",
                text: result.message || "Failed to send email.",
                timer: 2500,
                showConfirmButton: false
            });
            resetButton();
        }
    } catch (error) {
        Swal.fire({
            icon: "error",
            title: "Error",
            text: "Network error. Please try again.",
            timer: 2500,
            showConfirmButton: false
        });
        resetButton();

    }
}
