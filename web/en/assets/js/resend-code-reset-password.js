document.addEventListener('DOMContentLoaded', function () {
    const resendLink = document.getElementById('resend-code');
    const countdownElement = document.getElementById('countdown');

    let countdown = 30;
    let timer;

    // Countdown start function
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
                countdown = 30; // reset for future clicks
            }
        }, 1000);
    }

    // Immediately start countdown on page load
    startCountdown();

    // Only expose click function (resend logic) for onclick
    window.ResendCode = function (e) {
        e.preventDefault();

        if (!resendLink.classList.contains('disabled')) {

            ResendCodeForResetPassword();

        }
    };
});



async function ResendCodeForResetPassword() {
    const response = await fetch("../../ResendCodeForPW");

    if (response.ok) {
        const json = await response.json();
        if (json.status) {

            // ✅ Show success message
            notyf.success("Email Sent! Check your inbox.");

            // 🔄 Restart countdown
            clearInterval(timer);
            countdown = 30;
            startCountdown();
        } else {
            notyf.error("Email Not Found");
        }
    } else {
        notyf.error("Email Sent failed. Please try again later.");
    }
}
