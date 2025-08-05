async function SendEmail() {
    const emailAddress = document.getElementById("emailAddress").value;
    const emailSubject = document.getElementById("emailSubject").value;
    const emailBody = document.getElementById("emailBody").value;
    const sendToAll = document.getElementById("checkEmails").checked;

    console.log(sendToAll);

    const Send = {
        emailAddress: emailAddress,
        emailSubject: emailSubject,
        emailBody: emailBody,
        sendToAll: sendToAll
    };

    const SendJson = JSON.stringify(Send);

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
}
