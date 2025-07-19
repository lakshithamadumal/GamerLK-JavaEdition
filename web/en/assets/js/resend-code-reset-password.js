const countdownElement = document.getElementById('countdown');
const resendLink = document.getElementById('resend-code');

let countdown = 30;
let timer;

function startCountdown() {
    resendLink.classList.add('disabled');
    countdownElement.style.display = 'inline';
    countdownElement.textContent = ` (${countdown})`;

    timer = setInterval(function () {
        countdown--;
        countdownElement.textContent = ` (${countdown})`;

        if (countdown <= 0) {
            clearInterval(timer);
            resendLink.classList.remove('disabled');
            countdownElement.style.display = 'none';
            countdown = 30;
        }
    }, 1000);
}

document.addEventListener('DOMContentLoaded', function () {
    startCountdown(); // Only on first load

    window.ResendCode = function (e) {
        e.preventDefault();

        // Only allow if NOT disabled
        if (!resendLink.classList.contains('disabled')) {
            ResendCodeForResetPassword();
        }
    };
});

async function ResendCodeForResetPassword() {

    const response = await fetch("../../ResendCodeForPW");

    var notyf = new Notyf({
        position: {
            x: 'center',
            y: 'top'
        }
    });

    if (response.ok) {

        const json = await response.json();

        if (json.status) {

            if (json.message === "User found") {
                //Show success message
                notyf.success("Email Sent! Check your inbox.");

                //Restart countdown
                clearInterval(timer);
                countdown = 30;
                startCountdown();
            } else {

                notyf.error(json.message);

            }

        } else {
            notyf.error("Email not found! Please try again.");
        }

    } else {
        notyf.error("Email not sent! Please try again.");
    }

}


// resendLink.classList.add('disabled');
// countdownElement.style.display = 'none';