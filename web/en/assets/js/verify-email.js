async function VerifyAccount() {

    const inputs = document.querySelectorAll('.verification-code-input input');
    let verificationCode = '';

    inputs.forEach(input => {
        verificationCode += input.value.trim();
    });


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


}